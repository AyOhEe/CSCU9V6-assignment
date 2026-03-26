import java.io.IOException;
import java.net.*;

/**
 * Handles incoming {@link Node} connections, passing them to {@link CoordinatorConnection} threads to be resolved
 */
public class CoordinatorReceiver extends Thread {
    /** The {@link CoordinatorBuffer} to pass to {@link CoordinatorConnection} handlers */
    private CoordinatorBuffer buffer;
	/** The port on which to listen for incoming {@link Node} connections */
    private int port;

	/**
	 * Creates a new {@link CoordinatorReceiver}
	 * @param buffer The {@link CoordinatorBuffer} to pass to {@link CoordinatorConnection} handlers
	 * @param port The port on which to listen for incoming {@link Node} connections
	 */
	public CoordinatorReceiver(CoordinatorBuffer buffer, int port){
		this.buffer = buffer;
		this.port = port;
    }
    
    public void run () {
		ServerSocket receiverServer;
		try {
			// >>> create the socket the server will listen to
			receiverServer = new ServerSocket(port);
		} catch (IOException e) {
            System.out.println("Exception when creating CoordinatorReceiver ServerSocket " + e);
			return;
        }

        while (true) {
			try {
				// >>> get a new connection
				Socket nodeSocket = receiverServer.accept();
				System.out.println("CoordinatorReceiver    Coordinator has received a request ...");

				// >>> create a separate thread to service the request, a CoordinatorConnection thread.
				CoordinatorConnection connection = new CoordinatorConnection(nodeSocket, buffer);
				connection.start();

			} catch (java.io.IOException e) {
				System.out.println("Exception when creating a connection " + e);
			}
		}
	}
}
