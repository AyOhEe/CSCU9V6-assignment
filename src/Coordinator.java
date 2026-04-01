import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.time.LocalDateTime;

/**
 * Configures an instance of {@link CoordinatorBuffer}, {@link CoordinatorMutex}, and {@link CoordinatorReceiver}
 */
public class Coordinator {
	/**
	 * Lock object used to make sure that only one thread can write to log.txt at a time
	 */
	private static final Object LOG_FILE_LOCK = new Object();

    public static void main (String[] args){
		// Defaults specified in assignment spec
		int receiverPort = 7000;
		int mutexPort = 7001;

		// Log the address that we're listening on
		try {    
		    InetAddress localhost = InetAddress.getLocalHost();
		    String hostname = localhost.getHostName();
		    System.out.println("Coordinator address is " + localhost);
		    System.out.println("Coordinator host name is " + hostname + "\n\n");
		}
		catch (Exception e) {
			System.out.println("<Coordinator> Exception occurred when detecting localhost address: ");
			e.printStackTrace(System.out);
			System.exit(1);
		}
				
		// Check to see if we were passed specific port numbers
		if (args.length == 2) {
			receiverPort = Integer.parseInt(args[0]);
			mutexPort = Integer.parseInt(args[1]);
		} else if (args.length != 0) {
			// Being passed no arguments implies that we should use the defaults, but any other number is incorrect
			System.out.println("Usage: [receiver-port] [mutex-port]");
			System.exit(1);
		}

		// Wipe the log file
		try {
			new PrintWriter("log.txt").close();
		} catch (FileNotFoundException e) {
			System.out.println("<Coordinator> Exception occurred when clearing log file: ");
			e.printStackTrace(System.out);
			System.exit(1);
        }

        // Create and run a CoordinatorReceiver and a CoordinatorMutex object sharing a CoordinatorBuffer object
		CoordinatorBuffer buffer = new CoordinatorBuffer();
		CoordinatorMutex mutex = new CoordinatorMutex(buffer, mutexPort);
		CoordinatorReceiver receiver = new CoordinatorReceiver(buffer, receiverPort, mutex);

		// Start the threads
		receiver.start();
		mutex.start();
    }


	/**
	 * Acquires {@link Coordinator#LOG_FILE_LOCK} and writes the provided message to the log.
	 * @param name The name of the caller creating a log entry
	 */
	public static void writeLogEntry(String logEntry, String name) {
		synchronized(LOG_FILE_LOCK) {
			try {
				FileWriter logWriter = new FileWriter("log.txt", true);
				logWriter.write("[" + LocalDateTime.now() + "--" + name + "] " + logEntry + "\n");
				logWriter.close();
			} catch (IOException e) {
				System.out.println("<" + name + "> Exception occurred when writing to log: ");
				e.printStackTrace(System.out);
				System.exit(1);
			}
		}
	}
}
