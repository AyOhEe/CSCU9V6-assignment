import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

/**
 * Repeatedly sends token requests to the address of a {@link Coordinator}
 */
public class Node extends Thread {

	/** The hostname which this {@link Node} will listen on */
	private final String hostname;
	/** The port which this {@link Node} will listen on */
	private final int localPort;
	/** The hostname of the {@link Coordinator} */
	private final String coordinatorHostname;
	/** The port of the {@link CoordinatorReceiver} */
	private final int requestPort;
	/** The port of the {@link CoordinatorMutex} */
	private final int returnPort;
	/** On average, how long should the critical section wait for? */
	private final int meanDelay;

	/**
	 * Creates a new {@link Node}
	 * @param hostname The hostname which this {@link Node} will listen on
	 * @param localPort The port which this {@link Node} will listen on
	 * @param coordinatorHostname The hostname of the {@link Coordinator}
	 * @param requestPort The port of the {@link CoordinatorReceiver}
	 * @param returnPort The port of the {@link CoordinatorMutex}
	 * @param meanDelay On average, how long should the critical section wait for?
	 */
	public Node(String hostname, int localPort, String coordinatorHostname, int requestPort, int returnPort, int meanDelay) {
		this.hostname = hostname;
		this.localPort = localPort;
		this.coordinatorHostname = coordinatorHostname;
		this.requestPort = requestPort;
		this.returnPort = returnPort;
		this.meanDelay = meanDelay;
	}

	@Override
	public void run() {
		System.out.println("<Node> Active at " + hostname + ":" + localPort);

		while (true) {
			// Wait a little bit between requests
			CommonUtil.randomNap(100);

			try {
				// **** Send to the coordinator a token request.
				// send your ip address and port number
				System.out.println("<Node> Sending token request to Coordinator");
				Socket requestSocket = new Socket(coordinatorHostname, requestPort);
				PrintWriter requestWriter = new PrintWriter(requestSocket.getOutputStream(), true);
				requestWriter.println(hostname);
				requestWriter.println(localPort);
				requestSocket.close();

				// **** Then Wait for the token
				// Print suitable messages
				ServerSocket tokenServer = new ServerSocket(localPort);
				Socket tokenSocket = tokenServer.accept();
				tokenSocket.close();
				tokenServer.close();
				System.out.println("<Node> Token received!");
				// Small nap for readability
				CommonUtil.nap(500);

				// Pretend to do something important. This emulates a critical section
				System.out.println("<Node> Entering critical section");
				CommonUtil.randomNap(meanDelay);
				System.out.println("<Node> Leaving critical section");

				// **** Return the token
				// Print suitable messages - also considering communication failures
				Socket returnSocket = new Socket(coordinatorHostname, returnPort);
				returnSocket.close();
				System.out.println("<Node> Token returned");

			} catch (IOException e) {
				System.out.println(e);
				System.exit(1);
			}
		}
	}



	public static void main(String args[]) {
		// Port and average waiting time are specific to a node. The others are just configurable
		if (args.length != 5) {
			System.out.print("Usage: Node [port number] [coordinator host] [request port] [return port] [millisecs]");
			System.exit(1);
		}

		// Get the IP address of this machine
		String hostname;
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			hostname = localhost.getHostName();
			System.out.println("Node address is " + localhost);
			System.out.println("Node host name is " + hostname);
		} catch (java.net.UnknownHostException e) {
			System.out.println("<Node> Exception occurred when detecting localhost address: ");
			e.printStackTrace(System.out);
			System.exit(1);
			return; // Yes, this unreachable. Java doesn't care as it doesn't acknowledge that System.exit
			        // is special and never returns.
		}

		int port = Integer.parseInt(args[0]);
		String coordinatorHostname = args[1];
		int requestPort = Integer.parseInt(args[2]);
		int returnPort = Integer.parseInt(args[3]);
		int meanDelay = Integer.parseInt(args[4]);
		System.out.println("Node listening for responses on port " + port + "\n\n");
		System.out.println("Node targeting requests to " + coordinatorHostname + ":" + requestPort);
		System.out.println("Node returning to " + coordinatorHostname + ":" + returnPort);

		Node n = new Node(hostname, port, coordinatorHostname, requestPort, returnPort, meanDelay);
		n.start();
	}
}
