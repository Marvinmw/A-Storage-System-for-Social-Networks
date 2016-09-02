package ch.epfl.lpd.net;


public class StubbornLink {
	 private PointToPointLink link;
	  int count=2;
	  
	 public StubbornLink(PointToPointLink link){
		 this.link=link;
	 }
   
	public void sendMessage(String message){
		int i=0;
		while(i<count){
			i++;
			for(int k=0;k<count;k++){
				try {
					link.sendOnce(message);
			
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}
	
	public String receiveMessage() throws Exception{
		return link.receiveOnce();
	}
	
	
}
