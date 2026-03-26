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

	/**
	 * Creates a new {@link CoordinatorConnection} to handle an incoming request
	 * @param socket The {@link Socket} to listen from
	 * @param buffer The buffer in which to store a {@link CoordinatorRequest} object
	 */
	public CoordinatorConnection(Socket socket, CoordinatorBuffer buffer) {
    	this.socket = socket;
    	this.requestBuffer = buffer;
    }

	@Override
    public void run() {
		System.out.println("<CoordinatorConnection> dealing with request from socket " + socket);
		try {
			// Configure our stream
		    InputStream inputStream = socket.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			// Read the host and port and store them as a CoordinatorRequest object
			String host = bufferedReader.readLine();
			int port = Integer.parseInt(bufferedReader.readLine());
			CoordinatorRequest request = new CoordinatorRequest(host, port);

			// Pass the CoordinatorRequest to the buffer
			requestBuffer.saveRequest(request);
			requestBuffer.show();

			// And close out.
		    socket.close();
		    System.out.println("<CoordinatorConnection> received and recorded request from " + request.host() + ":" + request.port() + " (socket closed)");
		    	
		} 
		catch (IOException e){
			System.out.println("<CoordinatorConnection> Exception occurred in CoordinatorConnection: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
 	}
}
