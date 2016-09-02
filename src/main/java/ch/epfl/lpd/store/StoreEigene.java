package ch.epfl.lpd.store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class StoreEigene {
  private int seflid;
  private String in;
  private String out;
  private StoreMap inputstore=new StoreMap();
  private List<String> outstore= Collections.synchronizedList(new ArrayList<String>());
  private ArrayList<String> messagelist=new ArrayList<String>();
  public int count=0;
  public StoreEigene(String input,int selfid){
         this.seflid=selfid;
         this.readInput(input);
  }
  
 // public  static void main(String[] args){
	// new StoreEigene("0.input");
//  }
  
  
  public void excuteCommand(Message ms){
	// System.out.println("excuteCommand "+ms.toString());
	 String message= ms.getMessage();
	// String cmmond= message.split(",")[0];
	 String[] paras=message.split(",");
//  if(paras[0].equals("get")&paras[1].equals("3"))
	//System.out.println(seflid+"Commadnd: "+message);
	 
      if(paras[0].equals("put")){
      	 this.inputstore.put(paras[1], paras[2]);
      	}else{
      		 String inof=this.inputstore.get(paras[1]);
			 this.outstore.add(paras[1]+","+inof);
			 this.output(this.seflid+".out");
      	}


  }
  
  public String getCommand(){
	  if(this.count>(this.messagelist.size()-1))
		  return null;
	 String command= this.messagelist.get(count);
	// System.out.println(this.seflid+": "+command+" poll");
	 return command;
  }
  
  
  
  public void outputStore(String output){
	  this.outstore.add(output);
  }
  
  public String getInputStore(String key) {
      return inputstore.get(key);
  }

  public void putInputStore(String key, String value) {
	  inputstore.put(key, value);
  }

  public void removeInputStore(String key) {
	  inputstore.remove(key);
  }

  public String toInputStoreString() {
      return inputstore.toString();
  }
  
  
  
  
  
  
  public   void readInput(String filePath) {  
      FileReader fr = null;
	try {
		fr = new FileReader(filePath);
		BufferedReader bufferedreader = new BufferedReader(fr);  
	     String instring;  
	     
	    while (bufferedreader.ready() && (instring = bufferedreader.readLine().trim()) != null) {  
	          if (0 != instring.length()) {  
//	        	  if(instring.equals("get,3"))
	          //      System.out.println(this.seflid+"C "+instring); 
	              messagelist.add(instring);
	              //String[] info=instring.split(",");
	            //  inputstore.put(info[1], info[2]);
	          }  
	      }  
	      fr.close();  
	} catch (  IOException e) {
		// TODO Auto-generated catch block		
		e.printStackTrace();
	}  
     
  }
  
  public void output(String filePath){
	   FileWriter fr = null;
		try {
			fr = new FileWriter(filePath);
			BufferedWriter bufferedreader = new BufferedWriter(fr);  
			synchronized(this.outstore){
		    for(String m:this.outstore){
		    	//System.out.println("output :"+m);
		    	bufferedreader.write(m);
		    	bufferedreader.newLine();
		    	bufferedreader.flush();
		    }
			}
		      fr.close();  
		} catch (  IOException e) {
			// TODO Auto-generated catch block		
			e.printStackTrace();
		}  
	     
  }
}
