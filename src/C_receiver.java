import java.net.*;

public class C_receiver extends Thread{
    
    private C_buffer 	 buffer; 
    private int 		port;
    private ServerSocket 	s_socket; 
    private Socket		socketFromNode;
    private C_Connection_r	connect;
    
    public C_receiver (C_buffer b, int p){
		buffer = b;
		port = p;
    }
    
    public void run () {
	
	// >>> create the socket the server will listen to

	while (true) {
	    try{
	        
			// >>> get a new connection
	
			System.out.println ("C:receiver    Coordinator has received a request ...") ;
			
			// >>> create a separate thread to service the request, a C_Connection_r thread.

	    }
	    catch (java.io.IOException e) {
	    	System.out.println("Exception when creating a connection "+e);
	    }
	    
	}
    }//end run
}
