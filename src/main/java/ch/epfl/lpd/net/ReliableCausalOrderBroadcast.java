package ch.epfl.lpd.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.lpd.store.Message;
import ch.epfl.lpd.store.StoreEigene;

/**
* Implements:
*CausalOrderReliableBroadcast, instance crb. Uses:
*ReliableBroadcast, instance rb.
*upon event ⟨ crb, Init ⟩ do delivered := ∅;
*past := [];
*upon event ⟨ crb, Broadcast | m ⟩ do
*trigger ⟨ rb, Broadcast | [DATA, past, m] ⟩; append(past, (self, m));
*upon event ⟨ rb, Deliver | p, [DATA, mpast, m] ⟩ do if m ̸∈ delivered then
*forall (s, n) ∈ mpast do if n ̸∈ delivered then
*trigger ⟨ crb, Deliver | s, n ⟩; delivered := delivered ∪ {n}; if (s, n) ̸∈ past then
*append(past, (s, n)); trigger ⟨ crb, Deliver | p, m ⟩;
*delivered := delivered ∪ {m}; if (p, m) ̸∈ past then
*append(past, (p, m));*/
public class ReliableCausalOrderBroadcast{
	private long coutrer=0;
	private int selfid;
   private StoreEigene se;
   private ReliableBroadcast rb;
   private Map<Integer, ArrayList<Message>> pms=Collections.synchronizedMap(new HashMap<Integer,ArrayList<Message>>()); 
   private List<Message> delivered=Collections.synchronizedList(new ArrayList<Message>());
   public ReliableCausalOrderBroadcast(ReliableBroadcast rb,int selfid){
	   this.rb=rb;
	   this.selfid=selfid;
	   for(int i=0;i<3;i++)
	     pms.put(i, new ArrayList<Message>());
   }
   
   public void setStoreEigene(StoreEigene se){
	   this.se=se;
   }
   public void rcbBroadcast(Message message){
	  // System.out.println("");
	String listmessage= this.broadPast();
	listmessage+="/"+message.toString();
	this.rb.rbBroadcast(listmessage);
	this.pms.get(message.getDirectid()).add(message);	   
   }
   
   public String broadPast(){
	   synchronized (this.delivered) {
		    Set<Integer> set= pms.keySet();
	  String listmessage="";
	  Iterator<Integer> its=set.iterator();
	  while(its.hasNext()){
		  Integer pid=its.next();
		  ArrayList<Message> mlist=pms.get(pid);
		  for(Message m:mlist)
			  listmessage+=m.toString()+";";
	  }
	    return listmessage;
		  }
	
	
	
   }
   
   public void deliver(Message message){
		// TODO Auto-generated method stub
	//	Message message=rb.rbdeliver(); 
			String sb=message.getMessage();
			//System.out.println("RCB.deliver(): "+sb);
			String[] arrays=sb.split("/");
			String[] ma=arrays[1].split(":");
			//System.out.println(arrays[1].split(":"));
			Message newme=new Message(Integer.valueOf(ma[0]),Integer.valueOf(ma[1]),Long.valueOf(ma[2]),ma[3]);
			
		if(!isDelivered(newme)){
			String[] pm=arrays[0].split(";");
			for(int i=0;i<pm.length;i++){
			    if(pm[i].equals(""))
			    	continue;
				//int psid,int directid, long  seqid,String message
				String[] mifno=pm[i].split(":");
				Message me=new Message(Integer.valueOf(mifno[0]),Integer.valueOf(mifno[1]),Long.valueOf(mifno[2]),mifno[3]);
				if(!isDelivered(me)){
					//deliver to the application
					this.se.excuteCommand(me);
					synchronized (this.delivered) {
					    this.delivered.add(me);
					  }
					synchronized (this.pms) {
						this.pms.get(Integer.valueOf(mifno[0])).add(me);
					  }
					
				}
			}
			
			//deliver m to the application
			this.se.excuteCommand(newme);
			synchronized(this.delivered){
			this.delivered.add(newme);}
			synchronized(this.pms){
			this.pms.get(newme.getPsid()).add(newme);}
		}
   }
   
   private boolean isDelivered(Message message) {
		// TODO Auto-generated method stub
	   synchronized(this.delivered){
		for(Message m:delivered)
			if(m.getPsid()==message.getPsid() && m.getSeqid()==message.getSeqid())
				return true;
		return false;
	   }
	}

public   void  startRunning() throws InterruptedException {
	// TODO Auto-generated method stub
	boolean isrunning=true;
	
	while(isrunning){	
		String message=this.se.getCommand();
		this.se.count++;	
		if(message==null){
			break;
			}
	
		//sending 
		Message mob=new Message(this.selfid,this.selfid,this.coutrer,message);
			 String[] paras=message.split(",");
			 if(paras[0].equals("get"))
			 {
				//deliver m to the application
			   this.se.excuteCommand(mob); 
			   continue;
			 }
		this.rcbBroadcast(mob);
		this.coutrer++;
	   	
	}
	System.out.println("==========Process RealiableCausalOrderBoradcast end"+"====================");
	//this.se.output(this.selfid+".output");
	
}



}


