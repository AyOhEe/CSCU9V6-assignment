import java.util.*;

/**
 * Keeps track of all {@link CoordinatorRequest}s and releases them according to {@link CoordinatorRequest.Priority}
 */
public class CoordinatorBuffer {
	/** An {@link EnumMap} of {@link Queue}s containing the {@link CoordinatorRequest}s stored in this buffer */
    private final EnumMap<CoordinatorRequest.Priority, Queue<CoordinatorRequest>> requests = new EnumMap<>(CoordinatorRequest.Priority.class);
    /** Generates {@link CoordinatorRequest.Priority} values in such a way to prevent starvation */
    private PriorityGenerator priorityGenerator = new PriorityGenerator();

    /**
     * Configures a new {@link CoordinatorBuffer}
     */
    public CoordinatorBuffer() {
        // Create a queue for each priority ahead of time so we don't need to worry about if it exists or not
        for (CoordinatorRequest.Priority priority : CoordinatorRequest.Priority.values()) {
            requests.put(priority, new LinkedList<>());
        }
    }

    /**
     * @return The number of stored {@link CoordinatorRequest} objects
     */
    public int size(){
        int size = 0;
        for (Queue<CoordinatorRequest> queue : requests.values()) {
            size += queue.size();
        }

        return size;
    }

    /**
     * Saves a new request to the buffer
     * @param r The {@link CoordinatorRequest} to save
     */
    public synchronized void saveRequest(CoordinatorRequest r) {
        requests.get(r.priority()).add(r);
        notifyAll();
    }

    /**
     * Prints a string representation of every {@link CoordinatorRequest} stored in this buffer to {@link System#out}
     */
    public synchronized void show() {
        // Show nothing if the queue is empty.
        if (size() == 0) {
            return;
        }

        // Using a StringBuilder here means we don't constantly create new String objects.
        // It's technically better but doesn't really make a difference for us.
        StringBuilder sb = new StringBuilder();
        for (Queue<CoordinatorRequest> queue : requests.values()){
            for (CoordinatorRequest request : queue) {
                sb.append(request).append(", ");
            }
        }
        // We'd have an extraneous comma if we don't delete the last one that was added - another benefit of using a StringBuilder here
        sb.delete(sb.length() - 2, sb.length());

        System.out.println("<CoordinatorBuffer> Requests: " + sb);
    }

    /**
     * @return The next {@link CoordinatorRequest} in the buffer. If empty, blocks until a {@link CoordinatorRequest} is added.
     */
    public synchronized CoordinatorRequest getRequest() {
        CoordinatorRequest.Priority desiredPriority = priorityGenerator.next();
        CoordinatorRequest request;
        while ((request = getRequestOfPriority(desiredPriority)) == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                // Pass silently
            }
        }
        return request;
    }

    /**
     * Attempts to return a {@link CoordinatorRequest} with the specified priority. </br>
     * If that {@link Queue} is empty, checks in the next lowest priority queue. </br>
     * If {@link CoordinatorRequest.Priority#LOW}'s {@link Queue} is empty, but {@code size() != 0},
     * starts checking again from {@link CoordinatorRequest.Priority#CRITICAL}.
     * @return {@code null} if {@code size() == 0}. Otherwise, behaves as described
     */
    private CoordinatorRequest getRequestOfPriority(CoordinatorRequest.Priority priority) {
        // Guard clause: no point sifting through each priority queue if we know the whole thing is empty
        if (size() == 0) {
            return null;
        }

        // If there's a request of the desired priority, return that
        if (!requests.get(priority).isEmpty()) {
            return requests.get(priority).poll();
        }

        // Otherwise, keep going down the priorities until we find something.
        // If we reach LOW and it's empty, wrap around - there must be other, higher priority requests.
        // We confirmed in the guard clause that there is at least one request.
        return switch (priority) {
            case LOW -> getRequestOfPriority(CoordinatorRequest.Priority.CRITICAL);
            case MEDIUM -> getRequestOfPriority(CoordinatorRequest.Priority.LOW);
            case HIGH -> getRequestOfPriority(CoordinatorRequest.Priority.MEDIUM);
            case CRITICAL -> getRequestOfPriority(CoordinatorRequest.Priority.HIGH);
        };
    }

    /**
     * Generates a sequence of {@link CoordinatorRequest.Priority} values to prevent starvation. <br/>
     * Before repeating, each sequence will show 2^n of the highest priority, 2^(n-1) of the second highest,
     * 2^(n-2) of the third, etc... and exactly 1 of the lowest priority. These amounts are spread
     * evenly throughout the sequence to ensure critical nodes are still handled in a timely manner
     * without starving low priority nodes.
     * <br/> </br>
     * For example, assuming we have priorities of 0, 1, 2, and 3, the sequence would be as follows: 001001200100123
     */
    private static class PriorityGenerator implements Iterator<CoordinatorRequest.Priority> {
        /** Keeps track of how many times each {@link CoordinatorRequest.Priority} has been returned */
        int[] priorityCounters = new int[CoordinatorRequest.Priority.values().length - 1];

        // The sequence repeats ad infinitum - there's always a next element
        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public CoordinatorRequest.Priority next() {
            // Look for the first counter at 2, reset it, and return the *next* value
            for (int i = 0; i < priorityCounters.length; i++) {
                if (priorityCounters[i] == 2) {
                    priorityCounters[i] = 0;
                    return service(i + 1);
                }
            }

            // Otherwise default to the first value.
            return service(0);
        }

        /**
         * Returns the corresponding {@link CoordinatorRequest.Priority} for the provided value,
         * and increments its value in {@link PriorityGenerator#priorityCounters} if it exists.
         */
        private CoordinatorRequest.Priority service(int i) {
            if (i < priorityCounters.length) {
                priorityCounters[i]++;
            }
            return CoordinatorRequest.Priority.values()[i];
        }

        /**
         * Outputs the first 30 values returned by {@link PriorityGenerator} to {@link System#out}. <br/>
         * Demonstrates the generated sequence to confirm a good distribution.
         */
        public static void main(String[] args) {
            PriorityGenerator g = new PriorityGenerator();
            for (int i = 0; i < 30; i++) {
                System.out.println(g.next());
            }
        }
    }
}
