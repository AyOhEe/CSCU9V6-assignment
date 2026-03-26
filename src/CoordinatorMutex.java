import java.io.IOException;
import java.net.*;

/**
 * Handles incoming {@link Node} connections, sending and retrieving the mutual exclusion token between {@link Node}s
 */
public class CoordinatorMutex extends Thread {
	/** The {@link CoordinatorBuffer} to store {@link CoordinatorRequest}s in */
    private final CoordinatorBuffer buffer;
	/** The port on which to listen for incoming {@link Node} connections */
    private final int port;

	/**
	 * Creates a new {@link CoordinatorMutex}
	 * @param buffer The {@link CoordinatorBuffer} to store {@link CoordinatorRequest}s in
	 * @param port The port on which to listen for incoming {@link Node} connections
	 */
    public CoordinatorMutex(CoordinatorBuffer buffer, int port) {
		this.buffer = buffer;
		this.port = port;
    }

	@Override
    public void run() {
		try {
			// Create the SocketServer where tokens are returned after Nodes finish processing.
			ServerSocket returnServer = new ServerSocket(port);

			// Start serving requests
			while (true) {
				processRequest(returnServer);
			}
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when creating ServerSocket: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}

	/**
	 * Retrieves and handles a single {@link CoordinatorRequest}. Sleeps for 1000ms if {@link CoordinatorMutex#buffer} is empty.
	 * @param returnServer The {@link ServerSocket} on which to listen for tokens being returned
	 */
	private void processRequest(ServerSocket returnServer) {
		// Print some info on the current buffer content for debugging purposes.
		System.out.println("<CoordinatorMutex> Buffer size is: " + buffer.size());
		buffer.show();

		// Grab the request object for the next Node in the queue (FIFO)
		CoordinatorRequest nextRequest = buffer.getRequest();

		// Grant the token
		try {
			System.out.println("<CoordinatorMutex> Sending token to " + nextRequest.host() + ":" + nextRequest.port());
			Socket requestSocket = new Socket(nextRequest.host(), nextRequest.port());
			// TODO send message representing token
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when passing token to Node: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}

		// Retrieve the token
		try {
			System.out.println("<CoordinatorMutex> Waiting for token to be returned...");
			returnServer.accept();
			System.out.println("<CoordinatorMutex> Token returned");
			// TODO read token back, complain if different from expected
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when waiting for the token to be returned: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}
}
