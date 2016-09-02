package ch.epfl.lpd;


import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import ch.epfl.lpd.net.BestEffortBroadcast;
import ch.epfl.lpd.net.PerfectLink;
import ch.epfl.lpd.net.PointToPointLink;
import ch.epfl.lpd.net.ReliableBroadcast;
import ch.epfl.lpd.net.ReliableCausalOrderBroadcast;
import ch.epfl.lpd.net.StubbornLink;
import ch.epfl.lpd.store.StoreEigene;
import ch.epfl.lpd.store.StoreMap;
import sun.misc.*;





public class App {

    /**
     * TODO: Add any other needed variables.
     * @throws Exception 
     */
//
//	public static void main(String args[]) throws Exception{
//		String[] p0={"127.0.0.1", "8900", "127.0.0.1", "8901", "127.0.0.1", "8902", "0", "0.input"};
//		String[] p1={"127.0.0.1", "8900", "127.0.0.1", "8901", "127.0.0.1", "8902", "1", "1.input"};
//		String[] p2={"127.0.0.1", "8900", "127.0.0.1", "8901", "127.0.0.1", "8902", "2", "2.input"};
//		String[] as="@2324@89879@".split("@");
//		//Thread pc0=new Thread(new Task(p0));
//		//pc0.start();
//		//Thread pc1=new Thread(new Task(p1));
//		//pc1.start();
//		//Thread pc2=new Thread(new Task(p2));
//		//pc2.start();
//		
//		new App().testmain(p0);
//		//App.testmain(p1);
//		//App.testmain(p2);
//		while(true)
//			;
//		
//	}
//
    public static void main(String args[]) throws Exception {

        /**
         * Parse command line arguments.
         */
        List<NodeInfo> nodes = parseCmdNodesInfo(args);
        for (int i = 0; i < 3; i++)
            System.out.println("Node [" + i + "] info " + nodes.get(i).toString());

        int thisNodeIndex = Integer.parseInt(args[6]);
        if (thisNodeIndex > 2 || thisNodeIndex < 0)
            throw new Exception("Invalid node index (" + thisNodeIndex + "); must be >= 0 and <= 2!");
        System.out.println("This node has index: " + thisNodeIndex);

        String inputFilePath = args[7];
        System.out.println("Using input file: " + inputFilePath);

        String outputFilePath = thisNodeIndex + ".out";
        System.out.println("Using output file: " + outputFilePath);

        /**
         * Instantiate the backend, i.e., the local in-memory store.
         */
        StoreMap store = new StoreMap();

        /**
         * Instantiate point-to-point links to the other nodes.
         */
        List<PointToPointLink> ptpLinks = establishPTPLinks(nodes, thisNodeIndex);
        List<StubbornLink> stlinks=establishStuLinks(ptpLinks,thisNodeIndex);
        List<PerfectLink> perferctlinks=establishPefLinks(stlinks,thisNodeIndex);
        
        /**
         * TODO: Implement and instantiate the broadcast implementation on top
         * of PointToPointLinks.
         */
        BestEffortBroadcast bfb=new BestEffortBroadcast(perferctlinks,thisNodeIndex);
        ReliableBroadcast rb=new ReliableBroadcast(bfb,thisNodeIndex);
        ReliableCausalOrderBroadcast rcb=new ReliableCausalOrderBroadcast(rb,thisNodeIndex);
        bfb.setReliableBroadcast(rb);
        rb.setReliableCausalOrderBroadcast(rcb);
       for(int i=0;i<perferctlinks.size();i++)
    	   perferctlinks.get(i).setBestEffortBroadcast(bfb);
        
        /**
         * Now wait for the starting signal...
         *
         * This flag tells the App when it can start executing the trace, i.e.,
         * when SIGINT was received.
         */
        CountDownLatch startSignal = new CountDownLatch(1);
        registerSignalHandler(startSignal);

        String name = ManagementFactory.getRuntimeMXBean().getName();  
        System.out.println("process info: "+name); 
        System.out.println("Awaiting for SIGINT... please send the signal using: kill -INT PID");
        startSignal.await();
        System.out.println("Got SIGINT. Starting...");

        /**
         * TODO: Parse the input file.
         */
         StoreEigene storee=new StoreEigene(inputFilePath,thisNodeIndex);
         rcb.setStoreEigene(storee);
        /**
         * TODO: Coordinate with the other nodes; read/write to/from the local
         * store by executine the trace in the input file.
         */
        
         rcb.startRunning();
       
         System.out.println("==========Process "+thisNodeIndex+" end"+"====================");
         while(true){
        	 ;
         }
        /**
         * TODO: Print to the output file.
         */
        
        
    }

    private static List<StubbornLink> establishStuLinks(List<PointToPointLink> ptpLinks,int pid) {
		// TODO Auto-generated method stub
    	List<StubbornLink> stlinks=new ArrayList<StubbornLink>();
    	for(int i=0;i<3;i++){
    		if(i==pid)
    			continue;
    		int k=0;
    		if(i==2)
    			k=i-1;
    		else
    			k=i;
    		stlinks.add(new StubbornLink(ptpLinks.get(k)));
    	}
		return stlinks;
	}

	private static List<PerfectLink>  establishPefLinks(List<StubbornLink> stlinks,int pid){
		//PerfectLink(StubbornLink stl,int linkid)
		List<PerfectLink>  plinks=new ArrayList<PerfectLink>();
		for(int i=0;i<3;i++){
			if(i==pid)
    			continue;
			int k=0;
    		if(i==2)
    			k=i-1;
    		else
    			k=i;
			plinks.add(new PerfectLink(stlinks.get(k),i));
		}
		
		return plinks;
    	
    }
    

    private static  List<PointToPointLink> establishPTPLinks(List<NodeInfo> nodes, int index) throws Exception {
        List<PointToPointLink> links = new ArrayList<PointToPointLink>();

        int myPort = nodes.get(index).getPort();

        for (int i = 0; i < 3; i++) {
            if (i == index)
                continue;
            links.add(new PointToPointLink(nodes.get(i), myPort));
        }
        return links;
    }


    private static  List<NodeInfo> parseCmdNodesInfo(String args[]) throws Exception {
        if (args.length < 8)
            throw new Exception("Insuffient command line arguments!");

        List<NodeInfo> nInfo = new ArrayList<NodeInfo>();
        for (int i = 0; i < 3; i++)
        {
            int pos = i*2;

            String ip = args[pos];
            int port = Integer.parseInt(args[pos + 1]);
            NodeInfo n = new NodeInfo(ip, port);

            nInfo.add(n);
        }
        return nInfo;
    }

    private static  void registerSignalHandler(final CountDownLatch signalLatch)
    {
        Signal.handle(new Signal("INT"),
            new SignalHandler() {
                public void handle(Signal sig) {
                    // decrement the counter to signal that the App can start
                    signalLatch.countDown();
                }
            }
        );
    }
}
