package util;

public class Endpoint {

    // Server constants
    private final static String SERVER_RESOURCE_PATH = "/server";
    public final static String SERVER_REGISTER = SERVER_RESOURCE_PATH + "/register";
    public final static String SERVER_FETCH_PEERS = SERVER_RESOURCE_PATH + "/";

    // Peer constants
    private final static String PEER_RESOURCE_PATH = "/peer";
    public final static String PEER_REQUEST = PEER_RESOURCE_PATH + "/"; //TODO clarify what fetching from peer will look like
    public final static String PEER_HEARTBEAT = PEER_RESOURCE_PATH + "/heartbeat";

}
