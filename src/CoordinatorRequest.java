public record CoordinatorRequest(String host, int port) {
    @Override
    public String toString() {
        return host + ":" + port;
    }
}
