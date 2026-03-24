import java.util.*;

/**
 * Keeps track of all {@link CoordinatorRequest}s and the order in which they were received
 */
public class CoordinatorBuffer {
	/** {@link Vector} containing the {@link CoordinatorRequest}s stored in this buffer */
    private final Vector<CoordinatorRequest> requests = new Vector<>();

    /**
     * @return The number of stored {@link CoordinatorRequest} objects
     */
    public int size(){
    	return requests.size();
    }

    /**
     * Saves a new request to the buffer
     * @param r The {@link CoordinatorRequest} to save
     */
    public synchronized void saveRequest(CoordinatorRequest r){
    	requests.add(r);
    }

    /**
     * Prints a string representation of every {@link CoordinatorRequest} stored in this buffer to {@link System#out}
     */
    public void show() {
        // Using a StringBuilder here means we don't constantly create new String objects. It's a little better on memory and compute.
        StringBuilder sb = new StringBuilder();
        for (CoordinatorRequest request : requests) {
            sb.append(request).append(", ");
        }
        // We'd have an extraneous comma if we don't delete the last one that was added - another benefit of using a StringBuilder here
        sb.delete(sb.length() - 2, sb.length());

		System.out.println(sb);
    }

    /**
     * @return The next {@link CoordinatorRequest} in the buffer, or {@code null} if the buffer is empty
     */
    synchronized public CoordinatorRequest getRequest() {
    	CoordinatorRequest request = null;
		if (!requests.isEmpty()){
		    request = requests.removeFirst();
		}

		return request;
    }
}
