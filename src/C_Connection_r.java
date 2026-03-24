import java.net.*;
import java.io.*;
// Reacts to a node request.
// Receives and records the node request in the buffer.
//
public class C_Connection_r extends Thread {
	
    // class variables
    C_buffer requestBuffer;
    Socket socket;
    InputStream inputStream;
    BufferedReader bufferedReader;
       	
    public C_Connection_r(Socket s, C_buffer b){
    	this.socket = s;
    	this.requestBuffer = b;
    }
    
    public void run() {
		CoordinatorRequest request;
		
		System.out.println("C:connection IN  dealing with request from socket "+ socket);
		try {
		    // >>> read the request, i.e. node ip and port from the socket s
		    // >>> save it in a request object and save the object in the buffer (see C_buffer's methods).
	
		    inputStream = socket.getInputStream();
		    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String host = bufferedReader.readLine();
			int port = Integer.parseInt(bufferedReader.readLine());
			request = new CoordinatorRequest(host, port);

			requestBuffer.saveRequest(request);
		    
		    socket.close();
		    System.out.println("C:connection OUT    received and recorded request from "+ request.host()+":"+request.port()+ "  (socket closed)");
		    	
		} 
		catch (java.io.IOException e){
				System.out.println(e);
				System.exit(1);
		} 	
		requestBuffer.show();
		
 	}
}
