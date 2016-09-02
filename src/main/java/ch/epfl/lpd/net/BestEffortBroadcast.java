package ch.epfl.lpd.net;

import java.util.List;

import ch.epfl.lpd.store.Message;

public class BestEffortBroadcast {
  private List<PerfectLink> links;
  private int pid;
  private ReliableBroadcast rb;
  public BestEffortBroadcast(){}
  
  public BestEffortBroadcast(List<PerfectLink> links,int pid){
	  this.links=links;
	  this.pid=pid;
  }
  
  public void setReliableBroadcast(ReliableBroadcast rb){
	  this.rb=rb;
  }
  
  public void broadcast(Message message){
	  
	  for(PerfectLink plink:links){
		try {
			plink.sendOnce("%"+message.toString()+"%");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
  }
  
  public void delivermessage(String message){
	 
			try {
				String st=message;
				if(st.isEmpty())
					return;
				String[] melsit=st.split("%");
				for(String sm:melsit){
					if(sm.equals(""))
						continue;
				String[] ma=sm.split("@");
				
				this.rb.rbdeliver(new Message(Integer.valueOf(ma[0]),Integer.valueOf(ma[1]),Long.valueOf(ma[2]),ma[3]));
				}//return st;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
  }
  
}
