import java.net.*;

public class Coordinator {
    public static void main (String args[]){
		int receiverPort = 7000;
		int mutexPort = 7001;

		try {    
		    InetAddress c_addr = InetAddress.getLocalHost();
		    String c_name = c_addr.getHostName();
		    System.out.println ("Coordinator address is "+c_addr);
		    System.out.println ("Coordinator host name is "+c_name+"\n\n");    
		}
		catch (Exception e) {
		    System.err.println(e);
		    System.err.println("Error in corrdinator");
		}
				
		// allows defining port at launch time
		if (args.length == 2) {
			receiverPort = Integer.parseInt(args[0]);
			mutexPort = Integer.parseInt(args[1]);
		} else if (args.length != 0) {
			System.out.println("Usage: [receiver-port] [mutex-port]");
			System.exit(1);
		}
	
		// Create and run a CoordinatorReceiver and a CoordinatorMutex object sharing a CoordinatorBuffer object
		CoordinatorBuffer buffer = new CoordinatorBuffer();
		CoordinatorReceiver receiver = new CoordinatorReceiver(buffer, receiverPort);
		CoordinatorMutex mutex = new CoordinatorMutex(buffer, mutexPort);

		// Start the threads
		receiver.start();
		mutex.start();
    }
}
