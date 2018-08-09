import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;


public class Peer {
    static final String CONTENT_TYPE_JSON = "application/json";
    
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String ipAddress;
    String friendlyName;
    String serverIPAddress;


    public static void main(String[] args) {
        Peer peer = new Peer("Matt's Lenovo", "http://localhost:4567/server/register");
        peer.registerWithServer();
    }
    
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

    public Peer(Registration registration) {
        this.ipAddress = registration.getIPAddress();
        this.friendlyName = registration.getFriendlyName();
    }

    private void registerWithServer() {
        // Make registration packet
        Registration registrationPacket = new Registration(this.ipAddress, this.friendlyName);
        String json = gson.toJson(registrationPacket);

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

    @Override
    public String toString() {
        return this.ipAddress + ", " + this.friendlyName;
    }
}