import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

        String ipAddress = registration.getIPAddress();
        if (this.ipToPeerMap.containsKey(ipAddress)) {
            // Don't accept re-registrations
            return false;
        }

        // TODO check request IP vs param IP?

        // TODO Ping ip to verify connection?

        Peer peer = new Peer(registration, serverIPAddress);
        this.ipToPeerMap.put(ipAddress, peer);

        System.out.println(this.ipToPeerMap);

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
}
