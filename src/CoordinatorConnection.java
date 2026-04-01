import java.net.*;
import java.io.*;

/**
 * Handles an incoming {@link Socket} connection from a {@link Node}.
 * Receives and records the {@link Node}'s request in the provided {@link CoordinatorBuffer}.
 */
public class CoordinatorConnection extends Thread {
	/** The buffer in which to store a {@link CoordinatorRequest} object */
    private final CoordinatorBuffer requestBuffer;
	/** The {@link Socket} to listen from */
    private final Socket socket;
	/** The {@link CoordinatorReceiver} that created this {@link CoordinatorConnection} */
	private final CoordinatorReceiver receiver;

	/**
	 * Creates a new {@link CoordinatorConnection} to handle an incoming request
	 * @param socket The {@link Socket} to listen from
	 * @param buffer The buffer in which to store a {@link CoordinatorRequest} object
	 * @param receiver The {@link CoordinatorReceiver} that created this {@link CoordinatorConnection}
	 */
	public CoordinatorConnection(Socket socket, CoordinatorBuffer buffer, CoordinatorReceiver receiver) {
    	this.socket = socket;
    	this.requestBuffer = buffer;
		this.receiver = receiver;
    }

	@Override
    public void run() {
		System.out.println("<CoordinatorConnection> dealing with request from socket " + socket);

		// Wait a litle bit for console log readability
		CommonUtil.nap(500);

		try {
			// Configure our stream
		    InputStream inputStream = socket.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			// What kind of request is it?
			String type = bufferedReader.readLine();
			switch (type) {
				// Just checking that the server is still alive.
                case "HEARTBEAT" -> {
					socket.close();
				}

				// Shut down the Coordinator.
				case "SHUTDOWN" -> {
					receiver.shutdown();
					socket.close();
				}

				// Asking for the token
				case "REQUEST" -> {
					// Read the host and port and store them as a CoordinatorRequest object
					String host = bufferedReader.readLine();
					int port = Integer.parseInt(bufferedReader.readLine());
					CoordinatorRequest.Priority priority = CoordinatorRequest.Priority.valueOf(bufferedReader.readLine());
					CoordinatorRequest request = new CoordinatorRequest(host, port, priority);
					System.out.println("<CoordinatorConnection> received and recorded request from " + host + ":" + port + " with priority " + priority + " (socket closed)");

					// Pass the CoordinatorRequest to the buffer. This has to be synchronized as the request could be
					// handled before we can output to the console and the log.
					synchronized(requestBuffer) {
						requestBuffer.saveRequest(request);
						requestBuffer.show();
						Coordinator.writeLogEntry("Request from " + host + ":" + port + " logged with priority " + priority + ". Queue size: " + requestBuffer.size(), "CoordinatorConnection");
					}

					// And close out.
					socket.close();
				}

				// Anything else should complain
                default -> {
                    System.out.println("<CoordinatorConnection> Received unknown request type: " + type);
                    System.exit(-1);
                }
            }
		} 
		catch (IOException e){
			System.out.println("<CoordinatorConnection> Exception occurred in CoordinatorConnection: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
 	}
}
