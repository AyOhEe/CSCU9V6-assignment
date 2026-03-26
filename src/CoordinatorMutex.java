import java.net.*;

/**
 * Handles incoming {@link Node} connections, sending and retrieving the mutual exclusion token between {@link Node}s
 */
public class CoordinatorMutex extends Thread {
	/** The {@link CoordinatorBuffer} to store {@link CoordinatorRequest}s in */
    private final CoordinatorBuffer buffer;
	/** The port on which to listen for incoming {@link Node} connections */
    private final int port;

    public CoordinatorMutex(CoordinatorBuffer buffer, int port){
		this.buffer = buffer;
		this.port = port;
    }

    public void run() {
		try {
			//  >>>  Listening from the server socket on the given port
			// from where the TOKEN will be returned later.
			ServerSocket returnServer = new ServerSocket(port);

			while (true) {
				// >>> Print some info on the current buffer content for debugging purposes.
				// >>> please look at the available methods in C_buffer

				System.out.println("<CoordinatorMutex> Buffer size is " + buffer.size());

				// if the buffer is not empty
				if (buffer.size() != 0) {

					// >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
					//       Type conversions may be needed.

					CoordinatorRequest nextRequest = buffer.getRequest();


					// >>>  **** Granting the token
					//
					try {

					} catch (java.io.IOException e) {
						System.out.println(e);
						System.out.println("CRASH Mutex connecting to the node for granting the TOKEN" + e);
					}


					//  >>>  **** Getting the token back
					try {
						// THIS IS BLOCKING !
					} catch (java.io.IOException e) {
						System.out.println(e);
						System.out.println("CRASH Mutex waiting for the TOKEN back" + e);
					}
				}
			}
		} catch (Exception e) {
			System.out.print(e);
		}
	}
}
