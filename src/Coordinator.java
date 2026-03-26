import java.net.*;

public class Coordinator {
    public static void main (String[] args){
		// Defaults specified in assignment spec
		int receiverPort = 7000;
		int mutexPort = 7001;

		// Log the address that we're listening on
		try {    
		    InetAddress localhost = InetAddress.getLocalHost();
		    String hostname = localhost.getHostName();
		    System.out.println("Coordinator address is " + localhost);
		    System.out.println("Coordinator host name is " + hostname + "\n\n");
		}
		catch (Exception e) {
			System.out.println("<Coordinator> Exception occurred when detecting localhost address: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
				
		// Check to see if we were passed specific port numbers
		if (args.length == 2) {
			receiverPort = Integer.parseInt(args[0]);
			mutexPort = Integer.parseInt(args[1]);
		} else if (args.length != 0) {
			// Being passed no arguments implies that we should use the defaults, but any other number is incorrect
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
