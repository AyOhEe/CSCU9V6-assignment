import java.net.*;

public class C_mutex extends Thread{
    C_buffer buffer;
    Socket   s;
    int      port;

    // ip address and port number of the node requesting the token.
    // They will be fetched from the buffer    
    String n_host;
    int    n_port;
	
    public C_mutex (C_buffer b, int p){
		buffer = b;
		port = p;
    }

    public void run(){
	try{ 
	    //  >>>  Listening from the server socket on port 7001
	    // from where the TOKEN will be returned later.
	    ServerSocket ss_back = new ServerSocket(7001);
		
	    while (true){
		// >>> Print some info on the current buffer content for debugging purposes.
		// >>> please look at the available methods in C_buffer

		System.out.println("C:mutex   Buffer size is "+ buffer.size());
		
		// if the buffer is not empty
		if ( ) {		    

		    // >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
		    //       Type conversions may be needed.

		    n_host = 
		    n_port = 
		    
		    
		    // >>>  **** Granting the token
		    //
		    try{
		
		    }
		    catch (java.io.IOException e) {
				System.out.println(e);
				System.out.println("CRASH Mutex connecting to the node for granting the TOKEN" + e);
		    }
			    
			    
		    //  >>>  **** Getting the token back
		    try{
				// THIS IS BLOCKING !
		    }
		    catch (java.io.IOException e) {
				System.out.println(e);
				System.out.println("CRASH Mutex waiting for the TOKEN back" + e);
		    }
		}// endif	
	    }// endwhile
	}catch (Exception e) {System.out.print(e);}
	
   }
}
