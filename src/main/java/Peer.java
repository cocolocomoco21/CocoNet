import java.net.InetAddress;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;

import spark.RouteGroup;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;


public class Peer {
    static final String CONTENT_TYPE_JSON = "application/json";
    static Gson gson = new Gson();

    @Expose
    String ipAddress;
    
    @Expose
    String friendlyName;

    String serverIPAddress = "";

    int port = 4568;

    /**
     * Contruct Peer, where the Peer's IP is fetched from the machine. 
     */
    Peer(String friendlyName, String serverIPAddress) {
        this.friendlyName = friendlyName;
        this.serverIPAddress = serverIPAddress;
        
        try {
            this.ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            // This should probably be safe and return failure, but for now just swallow the bad 
            // IP and fail silently. Validation is required later on
            // TODO
            //return false;
            System.out.println("Invalid IP");
        }

        initializeRouting(this.port);
    }

    /**
     * Construct Peer, where the Peer's IP is specified via the Registration object.
     */
    public Peer(Registration registration, String serverIPAddress) {
        this.ipAddress = registration.getIPAddress();
        this.friendlyName = registration.getFriendlyName();
        this.port = registration.getPort();
        this.serverIPAddress = serverIPAddress;
    }

    private RouteGroup routes() {
        return () ->  {
            before("/*", (request, response) -> System.out.println("endpoint: " + request.pathInfo()));
            //`post("/register", this::registerPeer, gson::toJson);
            get("/name", this::getName, gson::toJson);
            get("/heartbeat", (request, response) -> {
                System.out.println("Heartbeat received");
                return true;
            });
        };
    }

    private String getName(spark.Request request, spark.Response response) {
        return this.friendlyName;
    }

    private void initializeRouting(int port) {
        // Open peer on port 4568, since the default (4567) is used by server
        port(port);
        path("/peer", this.routes());
    }

    /**
     * Register Peer with server by POSTing to the server's registration endpoint.
     */
    public void registerWithServer() {
        // Make registration packet
        Registration registrationPacket = new Registration(this.ipAddress, this.port, this.friendlyName);
        String json = new Gson().toJson(registrationPacket);

        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();
    
            String url = Utilities.formURL(this.serverIPAddress, Endpoint.SERVER_REGISTER);
            System.out.println("Registering with server (" + url + ")...");

            Request request = client.POST(url)
                .header(HttpHeader.ACCEPT, CONTENT_TYPE_JSON)
                .header(HttpHeader.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .content(new StringContentProvider(json), CONTENT_TYPE_JSON);

            ContentResponse resp = request.send();

            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }
    }

    /**
     * Fetch the list of connected peers from the server. 
     */
    public void fetchPeers() {
        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();
    
            String url = Utilities.formURL(this.serverIPAddress, Endpoint.SERVER_FETCH_PEERS);
            System.out.println("Fetching peers (" + url + ")...");

            ContentResponse resp = client.GET(url);

            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }
    }

    /**
     * Make a request to a peer registered on a specified URI.
     */
    public void requestFromPeer(String peerUrl) {
        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();

            String url = Utilities.formURL(peerUrl, Endpoint.PEER_REQUEST);
            System.out.println("Requesting from peer (" + url + ")...");

            ContentResponse resp = client.GET(url);

            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get full address that allows for communication (i.e. http + IP + port).
     * @return String - http + IP + port
     */
    public String getFullAddress() {
        return "http://" + this.ipAddress + ":" + String.valueOf(this.port);
    }

    @Override
    public String toString() {
        return this.ipAddress + ", " + this.friendlyName;
    }
}