package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Timer;
import java.util.TimerTask;

import model.PeerConnectionType;
import model.Registration;
import peer.Peer;
import util.Utilities;
import util.Endpoint;

public class Server {
    
    static final String CONTENT_TYPE_JSON = "application/json";
    static final int HEARTBEAT_INTERVAL = 5000;
    static Gson gson = new Gson();

    private Map<String, Peer> ipToPeerMap;
    private String serverIPAddress = "";


    public Server(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
        this.ipToPeerMap = new HashMap<String, Peer>();
        initializeRouting(4567);

        // Timer to send hearbeats to peers
        Timer heartbeatTimer = new Timer();
        heartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkDroppedConnections();
            }
        }, 0, HEARTBEAT_INTERVAL);
    }

    private RouteGroup routes() {
        return () ->  {
            before("/*", (request, response) -> System.out.println("endpoint: " + request.pathInfo()));
            post("/register", this::registerPeer, gson::toJson);
            get("/", this::fetchPeers, gson::toJson);
            post("/disconnect", this::disconnectPeer, gson::toJson);
        };
    }

    private void initializeRouting(int port) {
        port(port);
        path("/server", this.routes());
    }

    /**
     * Attempt to register a Peer, handle the POST request to do so.  
     */
    private boolean registerPeer(Request request, Response response) {
        response.type(CONTENT_TYPE_JSON);

        Registration registration = gson.fromJson(request.body(), new TypeToken<Registration>() {}.getType());
        
        // If not REGISTRATION packet, don't handle
        if (registration.getPeerConnectionType() != PeerConnectionType.REGISTRATION) {
            return false;
        }
        
        if (!registration.validate()) {
            return false;
        }

        String registrationIp = registration.getIPAddress();
        String requestIp = request.ip();
        
        // Invalid attempted registration
        if (!registrationIp.equals(requestIp)) {
            return false;
        }

        // Already registered - don't accept re-registrations
        if (this.ipToPeerMap.containsKey(registrationIp)) {
            return false;
        }

        // Add peer
        Peer peer = new Peer(registration, serverIPAddress);
        addPeer(peer);

        System.out.println(this.ipToPeerMap);

        return true;
    }

    
    /**
     * Attempt to disconnect a Peer, handle the POST request to do so.  
     */
    private boolean disconnectPeer(Request request, Response response) {
        response.type(CONTENT_TYPE_JSON);

        Registration registration = gson.fromJson(request.body(), new TypeToken<Registration>() {}.getType());
        
        // If not DISCONNECTION packet, don't handle
        if (registration.getPeerConnectionType() != PeerConnectionType.DISCONNECTION) {
            return false;
        }        
        
        if (!registration.validate()) {
            return false;
        }

        String registrationIp = registration.getIPAddress();
        String requestIp = request.ip();
        
        // Invalid attempted registration
        if (!registrationIp.equals(requestIp)) {
            return false;
        }

        // Not already registered - can't remove
        if (!this.ipToPeerMap.containsKey(registrationIp)) {
            return false;
        }

        // Remove peer
        Peer peer = new Peer(registration, serverIPAddress);
        boolean isRemoved = removePeer(peer);
        if (!isRemoved) {
            // TODO error handle
            //throw new Exception("Peer was not correctly removed");
            System.out.println("Peer was not correctly removed");
        }

        System.out.println(this.ipToPeerMap);

        return isRemoved;
    }

    /**
     * Fetch list of currently connected Peers.
     */
    private List<Peer> fetchPeers(Request request, Response response) {
        response.type(CONTENT_TYPE_JSON);

        List<Peer> peers = ipToPeerMap.values().stream().collect(Collectors.toList());
        
        // If requester is not on list of peers (i.e. disconnected or not registered), don't return list
        String requestIp = request.ip();
        if (!peers.stream().anyMatch(i -> i.getIpAddress().equals(requestIp))) {
            // TODO should return 404
            return null;
        }

        System.out.println("Returning peers: " + peers);

        return peers;
    }

    private void checkDroppedConnections() {
        for (Map.Entry<String, Peer> entry : ipToPeerMap.entrySet()) {
            Peer peer = entry.getValue();
            
            // Send hearbeat
            boolean result = sendHeartbeat(peer);
            
            // If no heartbeat received, remove peer (dropped connection)
            if (!result) {
                boolean isRemoved = removePeer(peer);
                if (!isRemoved) {
                    // TODO error handle
                    //throw new Exception("Peer was not correctly removed");
                    System.out.println("Peer was not correctly removed");
                }
            }
        }
    }

    private boolean sendHeartbeat(Peer peer) {
        String url = Utilities.formURL(peer.getFullAddress(), Endpoint.PEER_HEARTBEAT);
                
        System.out.println("Sending heartbeat (" + url + ")...");
        ContentResponse resp = Utilities.sendGETRequest(url);

        boolean result = resp != null && Boolean.parseBoolean(resp.getContentAsString());
        return result;
    }

    /**
     * Add peer to list of currently registered peers.
     * @param peer - Peer to add
     * @return boolean - true if new peer, false if previously exising peer 
     * (as it should have been removed by heartbeat checking).
     */
    private boolean addPeer(Peer peer) {
        Peer previous = this.ipToPeerMap.put(peer.getIpAddress(), peer);
        return previous == null;
    }

    /**
     * Remove peer from list of currently registered peers.
     * @param peer - Peer to remove
     * @return boolean - if peer was removed.
     */
    private boolean removePeer(Peer peer) {
        Peer removed = this.ipToPeerMap.remove(peer.getIpAddress());
        return removed != null;
    }
    
}
