package ch.epfl.lpd.store;
/**Data Structure*/
public class Message{
	private int directid;
	private int psid;
	private String message;
	private long  seqid;
	private String splitchar=":";
	public Message(int psid,int directid, long  seqid,String message){
		this.directid=directid;
		this.psid=psid;
		this.message=message;
		this.seqid=seqid;
	}
	public int getPsid(){
		return this.psid;
	}
	public String getMessage(){
		return this.message;
	}
	public long getSeqid(){
		return this.seqid;
	}
	public int getDirectid(){
		return this.directid;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + psid;
		result = prime * result + (int) (seqid ^ (seqid >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (psid != other.psid)
			return false;
		if (seqid != other.seqid)
			return false;
		return true;
	}
	public void setSplitChar(String sc){
		this.splitchar=sc;
	}
	@Override
	public String toString() {
		return  this.psid + this.splitchar + this.directid + this.splitchar+ seqid+ this.splitchar+ message  ;
	}
	public void setDirectid(int did) {
		// TODO Auto-generated method stub
		this.directid=did;
	}
	
	
}