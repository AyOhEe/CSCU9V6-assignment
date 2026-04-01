/**
 * Represents a request to the coordinator, storing where the response should be directed
 * @param host The hostname to send a response to
 * @param port The port to send a response to
 * @param priority How important is this request?
 */
public record CoordinatorRequest(String host, int port, Priority priority) {
    @Override
    public String toString() {
        return priority + "--" + host + ":" + port;
    }

    /**
     * Represents how important a {@link CoordinatorRequest} is and how quickly it should be handled in a queue.
     */
    public enum Priority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW;
    }
}
