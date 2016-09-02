package ch.epfl.lpd.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.lpd.store.Message;

public class ReliableBroadcast {
  private int selfid;
  private long bmcounter=0;
  private BestEffortBroadcast bfb;
  ReliableCausalOrderBroadcast rco;
  private List<Message> delivered= Collections.synchronizedList(new ArrayList<Message>());
  private List<Integer> pcCo=Collections.synchronizedList(new ArrayList<Integer>());
  private List<Message> forwarded= Collections.synchronizedList(new ArrayList<Message>());
  private Map<Integer,ArrayList<Message>> mapMes= Collections.synchronizedMap(new HashMap<Integer,ArrayList<Message>>());
  public ReliableBroadcast(BestEffortBroadcast bfb,int selfid){
	  this.bfb=bfb;
	  this.addALL(3);
	  this.selfid=selfid;
	   for(int i=0;i<3;i++)
		   mapMes.put(i, new ArrayList<Message>());
  }
  public void setReliableCausalOrderBroadcast(ReliableCausalOrderBroadcast rcb){
	  this.rco=rcb;
  }
  public void addALL(int n){
	  for(int i=0;i<n;i++)
	  this.pcCo.add(i);
  }
  
  public void rbBroadcast(String message){
	  //(int psid,int directid, long  seqid,String message)
	  Message m=new Message(selfid,selfid, bmcounter, message);	 
	  m.setSplitChar("@");
	  synchronized(this.delivered){
	  this.delivered.add(m);
	  }
	  rco.deliver(m);
	  this.bfb.broadcast(m);
	  bmcounter++;
  }

public void rbdeliver(Message message) {
	// TODO Auto-generated method stub
	//Message message=bfb.delivermessage(); 
	if(!this.isForwarded(message)){
	this.forwarded.add(message);
	 message.setDirectid(selfid);
	 message.setSplitChar("@");
	 this.bfb.broadcast(message);
	}
	if(!isDelivered(message)){
		synchronized(this.delivered){
		this.delivered.add(message);
		}
		this.rco.deliver(message);
		if(!this.pcCo.contains(message.getPsid())){
			message.setDirectid(selfid);
			message.setSplitChar("@");
			this.bfb.broadcast(message);
			}
		else{
			synchronized(this.mapMes){
			this.mapMes.get(message.getPsid()).add(message);
			}
		}
			
	}
	
	
}


private boolean isForwarded(Message message) {
	// TODO Auto-generated method stub
	// TODO Auto-generated method stub
		synchronized(this.forwarded){
		for(Message m:this.forwarded)
			if(m.getPsid()==message.getPsid() && m.getSeqid()==message.getSeqid())
				return true;
		return false;
		}
	
}
private boolean isDelivered(Message message) {
	// TODO Auto-generated method stub
	synchronized(this.delivered){
	for(Message m:delivered)
		if(m.getPsid()==message.getPsid() && m.getSeqid()==message.getSeqid())
			return true;
	return false;}
}

}
