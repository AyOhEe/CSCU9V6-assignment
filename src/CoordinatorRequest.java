/**
 * Represents a request to the coordinator, storing where the response should be directed
 * @param host The hostname to send a response to
 * @param port The port to send a response to
 */
public record CoordinatorRequest(String host, int port) {
    @Override
    public String toString() {
        return host + ":" + port;
    }
}
