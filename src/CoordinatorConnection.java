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
	public CoordinatorConnection(Socket socket, CoordinatorBuffer buffer){
    	this.socket = socket;
    	this.requestBuffer = buffer;
    }
    
    public void run() {
		System.out.println("<CoordinatorConnection: IN>  dealing with request from socket " + socket);
		try {
		    // >>> read the request, i.e. node ip and port from the socket s
		    // >>> save it in a request object and save the object in the buffer (see C_buffer's methods).
	
		    InputStream inputStream = socket.getInputStream();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String host = bufferedReader.readLine();
			int port = Integer.parseInt(bufferedReader.readLine());
			CoordinatorRequest request = new CoordinatorRequest(host, port);

			requestBuffer.saveRequest(request);
		    
		    socket.close();
		    System.out.println("<CoordinatorConnection: OUT>  received and recorded request from " + request.host() + ":" + request.port() + " (socket closed)");
		    	
		} 
		catch (java.io.IOException e){
				System.out.println(e);
				System.exit(1);
		}

		requestBuffer.show();
 	}
}
