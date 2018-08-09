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
import spark.Spark;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;



public class Peer {
    static final String CONTENT_TYPE_JSON = "application/json";
    static Gson gson = new Gson();

    @Expose
    String ipAddress;
    
    @Expose
    String friendlyName;

    String serverIPAddress = "";


    public static void main(String[] args) {
        Peer peer = new Peer("Matt's Lenovo", "http://localhost:4567/server/register");
        
        // Register with server
        peer.registerWithServer();
        
        // Fetch peers
        peer.fetchPeers();

        // Open peer on port 4568, since the default (4567) is used by server
        port(4568);
        path("/peer", peer.routes());
    }

    private RouteGroup routes() {
        return () ->  {
            before("/*", (request, response) -> System.out.println("endpoint: " + request.pathInfo()));
            //post("/register", this::registerPeer, gson::toJson);
            get("/", this::test, gson::toJson);
        };
    }
    
    /**
     * Contruct Peer, where the Peer's IP is fetched from the machine. 
     */
    private Peer(String friendlyName, String serverIPAddress) {
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
    }

    /**
     * Construct Peer, where the Peer's IP is specified via the Registration object.
     */
    public Peer(Registration registration, String serverIPAddress) {
        this.ipAddress = registration.getIPAddress();
        this.friendlyName = registration.getFriendlyName();
    }


    private String test(spark.Request request, spark.Response response) {
        return "TEST";
    }


    /**
     * Register Peer with server by POSTing to the server's registration endpoint.
     */
    private void registerWithServer() {
        // Make registration packet
        Registration registrationPacket = new Registration(this.ipAddress, this.friendlyName);
        String json = new Gson().toJson(registrationPacket);

        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();
    
            Request request = client.POST(this.serverIPAddress)
                .header(HttpHeader.ACCEPT, CONTENT_TYPE_JSON)
                .header(HttpHeader.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .content(new StringContentProvider(json), CONTENT_TYPE_JSON);

            System.out.println("Registration sent to server");

            Response resp = request.send();

            System.out.println("Response: " + resp.toString());
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }
    }

    /**
     * Fetch the list of connected peers from the server. 
     */
    private void fetchPeers() {
        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();
    
            System.out.println("Fetching peers...");

            ContentResponse resp = client.GET("http://localhost:4567/server/");

            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }
    }

    private void requestFromPeer(String URI) {
        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();
    
            ContentResponse resp = client.GET(URI + "/peer/");

            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return this.ipAddress + ", " + this.friendlyName;
    }
}