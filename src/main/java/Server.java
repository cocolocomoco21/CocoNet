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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Server {
    
    static final String CONTENT_TYPE_JSON = "application/json";
    static Gson gson = new Gson();

    private Map<String, Peer> ipToPeerMap;
    private String serverIPAddress = "";


    Server(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
        this.ipToPeerMap = new HashMap<String, Peer>();
        initializeRouting(4567);
    }

    private RouteGroup routes() {
        return () ->  {
            before("/*", (request, response) -> System.out.println("endpoint: " + request.pathInfo()));
            post("/register", this::registerPeer, gson::toJson);
            get("/", this::fetchPeers, gson::toJson);
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

        // TODO Ping ip to verify connection?

        Peer peer = new Peer(registration, serverIPAddress);
        this.ipToPeerMap.put(registrationIp, peer);

        System.out.println(this.ipToPeerMap);

        // TODO here for testing. Move this into thread and delete this function call
        boolean val = sendHeartbeat();
        
        return true;
    }

    /**
     * Fetch list of currently connected Peers.
     */
    private List<Peer> fetchPeers(Request request, Response response) {
        response.type(CONTENT_TYPE_JSON);

        List<Peer> peers = ipToPeerMap.values().stream().collect(Collectors.toList());
        System.out.println("Returning peers: " + peers);

        return peers;
    }

    // TODO this is going to have to be multi-threaded. Create a thread on instantiation
    // to handle heartbeat polling (every second? 5 seconds? minute?)
    // For now, left as a standalone method for testing and simplicity
    private boolean sendHeartbeat() {
        boolean result = true;
        try {
            for (Map.Entry<String, Peer> entry : ipToPeerMap.entrySet()) {
                Peer peer = entry.getValue(); 
                String url = Utilities.formURL(peer.getFullAddress(), Endpoint.PEER_HEARTBEAT);
                
                System.out.println("Sending heartbeat (" + url + ")...");
                ContentResponse resp = Utilities.sendGetRequest(url);

                boolean res = Boolean.parseBoolean(resp.getContentAsString());
                if (res == false) {
                    result = false;
                }
            }
        } catch (Exception ee) {
            // TODO better
            ee.printStackTrace();
        }

        return result;
    }
    
}
