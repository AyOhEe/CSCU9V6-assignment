import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Tells the {@link Coordinator} to shut itself down and all connected nodes.
 */
public class ShutdownInstigator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.print("Usage: [coordinator-host] [request-port]");
            System.exit(1);
        }

        String coordinatorHostname = args[0];
        int requestPort = Integer.parseInt(args[1]);

        // Send the coordinator a shutdown request.
        try {
            System.out.println("<ShutdownInstigator> Sending shutdown request to Coordinator");
            Socket requestSocket = new Socket(coordinatorHostname, requestPort);
            PrintWriter requestWriter = new PrintWriter(requestSocket.getOutputStream(), true);
            requestWriter.println("SHUTDOWN");
            requestSocket.close();
        } catch (ConnectException e) {
            System.out.println("<ShutdownInstigator> Failed to connect to Coordinator.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
