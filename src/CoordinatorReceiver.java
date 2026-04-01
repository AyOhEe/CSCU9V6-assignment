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
	/** The {@link CoordinatorMutex} handling {@link CoordinatorRequest}s */
	private final CoordinatorMutex mutex;

	/** The {@link ServerSocket} on which we're listening for incoming requests */
	private ServerSocket receiverServer;
	/** When true, the {@link CoordinatorReceiver} will stop serving requests */
	private boolean hasShutdown = false;

	/**
	 * Creates a new {@link CoordinatorReceiver}
	 * @param buffer The {@link CoordinatorBuffer} to pass to {@link CoordinatorConnection} handlers
	 * @param port The port on which to listen for incoming {@link Node} connections
	 * @param mutex The {@link CoordinatorMutex} handling {@link CoordinatorRequest}s
	 */
	public CoordinatorReceiver(CoordinatorBuffer buffer, int port, CoordinatorMutex mutex) {
		this.buffer = buffer;
		this.port = port;
		this.mutex = mutex;
    }

	@Override
    public void run() {
		try {
			// Create the socket that the server will listen to
			receiverServer = new ServerSocket(port);
		} catch (IOException e) {
            System.out.println("<CoordinatorReceiver> Exception occurred when creating ServerSocket: ");
			e.printStackTrace(System.out);
			System.exit(1);
        }

		// Start serving requests
		while (!hasShutdown) {
			processRequest();
		}
	}

	/**
	 * Accepts a single request and creates a new {@link CoordinatorConnection} thread to handle it.
	 */
	private void processRequest() {
		try {
			// Accept a new connection
			Socket nodeSocket = receiverServer.accept();
			System.out.println("<CoordinatorReceiver> Coordinator has received a request...");
			CommonUtil.nap(500);

			// Create a CoordinatorConnection thread to handle the request
			CoordinatorConnection connection = new CoordinatorConnection(nodeSocket, buffer, this);
			connection.start();

		} catch (SocketException e) {
			if (e.getMessage().equals("Socket closed")) {
				// We were shut down - silently close.
				return;
			}
			System.out.println("<CoordinatorReceiver> Exception when accepting a connection: ");
			e.printStackTrace(System.out);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("<CoordinatorReceiver> Exception when accepting a connection: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}

	/**
	 * Stops listening for new connections and instructs the {@link CoordinatorMutex} to close all {@link CoordinatorRequest}s
	 */
	public void shutdown() {
		System.out.println("<CoordinatorReceiver> Closing... ");

		hasShutdown = true;
		try {
			receiverServer.close();
		} catch (IOException e) {
			System.out.println("<CoordinatorReceiver> Exception when trying to shutdown receiverServer: ");
			e.printStackTrace(System.out);
            System.exit(1);
        }

		mutex.shutdown();
    }
}
