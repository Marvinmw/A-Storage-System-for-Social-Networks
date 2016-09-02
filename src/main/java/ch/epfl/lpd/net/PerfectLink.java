package ch.epfl.lpd.net;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class PerfectLink implements Runnable{
   private int linkid;
   private StubbornLink stl;
   private int count=1;
   long number=count*10+linkid;
   TreeSet<Long> trs=new  TreeSet<Long>();
   private BestEffortBroadcast beb;
   private Map<Long, String> hist= Collections.synchronizedMap(new HashMap<Long, String>());

   public PerfectLink(StubbornLink stl,int linkid){
	   this.stl=stl;
	   this.linkid=linkid;
	   Thread th=new Thread(this);
	   th.start();
   }
   
   public void setBestEffortBroadcast(BestEffortBroadcast beb){
	   this.beb=beb;
   }
   
  
   public void sendOnce(String ms){ 
	   number=count*10+linkid;
	   String nms="~"+number+"**"+ms+"~";
	  
	   synchronized (this.hist) {
		    stl.sendMessage(nms);
		   hist.put(number, nms);  
	   }
	   count+=1;
   }
   
   public String receiveOnce() throws Exception{
	  
	   Pattern p1=Pattern.compile("\\~");
	   Pattern p2=Pattern.compile("\\*\\*");
	   Pattern p3=Pattern.compile(",");
	   String message=stl.receiveMessage();
	   
	   
	   String[] mas=p1.split(message);
	   String newmessage="";
	   
	   for(int i=0;i<mas.length;i++){
		   if(mas[i].isEmpty())
			   continue;
		   if(mas[i].contains("ack"))
		   {
			  String[] mds=p3.split(mas[i]);
			  long seqc=Integer.valueOf(mds[1]);
			  synchronized (this.hist) {
				  hist.remove(seqc);
			  }
			 
			  continue;
		   }		   
		   
		  String[] ms=p2.split(mas[i]);
		  long seq=Integer.valueOf(ms[0]);
		  if(!trs.contains(seq)){
			  newmessage+=ms[1];
			  trs.add(seq);
			  }
		 
	   }
	   return newmessage;
	  
   }

@Override
public void run() {
	// TODO Auto-generated method stub
	while(true){
		String st="";
		try {
			st = this.receiveOnce();
			if(!(st==null | st.isEmpty()))
			   this.beb.delivermessage(st);
			
			synchronized (this.hist) {
			if(!hist.isEmpty()){
			Set<Long> it=hist.keySet();
			Iterator<Long> itr=it.iterator();
			     while(itr.hasNext()){
			    	 long key=itr.next();
			    	 stl.sendMessage(hist.get(key));
			     }
			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}



}
