import java.io.IOException;
import java.net.*;

/**
 * Handles incoming {@link Node} connections, passing them to {@link CoordinatorConnection} threads to be stored and handled by {@link CoordinatorMutex}
 */
public class CoordinatorReceiver extends Thread {
    /** The {@link CoordinatorBuffer} to pass to {@link CoordinatorConnection} handlers */
    private final CoordinatorBuffer buffer;
	/** The port on which to listen for incoming {@link Node} connections */
    private final int port;

	/**
	 * Creates a new {@link CoordinatorReceiver}
	 * @param buffer The {@link CoordinatorBuffer} to pass to {@link CoordinatorConnection} handlers
	 * @param port The port on which to listen for incoming {@link Node} connections
	 */
	public CoordinatorReceiver(CoordinatorBuffer buffer, int port) {
		this.buffer = buffer;
		this.port = port;
    }

	@Override
    public void run() {
		try {
			// Create the socket that the server will listen to
			ServerSocket receiverServer = new ServerSocket(port);

			// Start serving requests
			while (true) {
				processRequest(receiverServer);
			}
		} catch (IOException e) {
            System.out.println("<CoordinatorReceiver> Exception occurred when creating ServerSocket: ");
			e.printStackTrace(System.out);
			System.exit(1);
        }
	}

	/**
	 * Accepts a single request and creates a new {@link CoordinatorConnection} thread to handle it.
	 * @param receiverServer The {@link ServerSocket} on which to listen for incoming requests
	 */
	private void processRequest(ServerSocket receiverServer) {
		try {
			// Accept a new connection
			Socket nodeSocket = receiverServer.accept();
			System.out.println("<CoordinatorReceiver> Coordinator has received a request ...");

			// Create a CoordinatorConnection thread to handle the request
			CoordinatorConnection connection = new CoordinatorConnection(nodeSocket, buffer);
			connection.start();

		} catch (java.io.IOException e) {
			System.out.println("<CoordinatorReceiver> Exception when accepting a connection: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}
}
