import java.io.*;
import java.net.*;

/**
 * Handles incoming {@link Node} connections, sending and retrieving the mutual exclusion token between {@link Node}s
 */
public class CoordinatorMutex extends Thread {
	/** The {@link CoordinatorBuffer} to store {@link CoordinatorRequest}s in */
    private final CoordinatorBuffer buffer;
	/** The port on which to listen for incoming {@link Node} connections */
    private final int port;

	/** The {@link ServerSocket} on which we're listening for tokens being returned */
	private ServerSocket returnServer;
	/** When true, all {@link CoordinatorRequest}s in the {@link CoordinatorMutex#buffer} will be instructed to shut down. */
	private boolean hasShutdown = false;

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
			returnServer = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when creating ServerSocket: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}

		// Start serving requests
		while (!hasShutdown || buffer.size() != 0) {
			processRequest();
		}

		// Close the server once we're done serving requests
		try {
			returnServer.close();
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when closing ServerSocket: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}

	/**
	 * Retrieves and handles a single {@link CoordinatorRequest}. Sleeps for 1000ms if {@link CoordinatorMutex#buffer} is empty.
	 */
	private void processRequest() {
		// Print some info on the current buffer content for debugging purposes.
		System.out.println("<CoordinatorMutex> Buffer size is: " + buffer.size());
		buffer.show();

		// Grab the request object for the next Node in the queue (FIFO)
		CoordinatorRequest nextRequest = buffer.getRequest();
		if (hasShutdown) {
			informShutdown(nextRequest);
		} else {
			grantToken(nextRequest);
		}
	}

	/**
	 * Shuts down the node at {@code nextRequest}
	 */
	private void informShutdown(CoordinatorRequest nextRequest) {
		// Inform the node of the closure
		System.out.println("<CoordinatorMutex> Shutting down Node " + nextRequest);
		try {
			Socket requestSocket = new Socket(nextRequest.host(), nextRequest.port());
			PrintWriter requestWriter = new PrintWriter(requestSocket.getOutputStream(), true);
			requestWriter.println("SHUTDOWN");
			requestSocket.close();
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when shutting down Node: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
		// Log that we did so
		Coordinator.writeLogEntry("Shut down Node " + nextRequest, "CoordinatorMutex");
	}

	/**
	 * Grants the node at {@code nextRequest} the token, and waits for it to be returned
	 */
	private void grantToken(CoordinatorRequest nextRequest) {
		// Grant the token
		System.out.println("<CoordinatorMutex> Sending token to " + nextRequest);
		try {
			Socket requestSocket = new Socket(nextRequest.host(), nextRequest.port());
			PrintWriter requestWriter = new PrintWriter(requestSocket.getOutputStream(), true);
			requestWriter.println("GRANTED");
			requestSocket.close();
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when passing token to Node: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
		// Log that we did so
		Coordinator.writeLogEntry("Issued token to " + nextRequest, "CoordinatorMutex");

		// Retrieve the token
		System.out.println("<CoordinatorMutex> Waiting for token to be returned...");
		try {
			returnServer.accept();
			System.out.println("<CoordinatorMutex> Token returned");
		} catch (IOException e) {
			System.out.println("<CoordinatorMutex> Exception occurred when waiting for the token to be returned: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
		// Log that we did so
		Coordinator.writeLogEntry("Received token back from " + nextRequest + ". Queue size: " + buffer.size(), "CoordinatorMutex");
	}

	/**
	 * Starts informing {@link Node}s at {@link CoordinatorRequest}s that they should shut down.
	 */
	public void shutdown() {
		System.out.println("<CoordinatorMutex> Closing... ");
		hasShutdown = true;
	}
}
