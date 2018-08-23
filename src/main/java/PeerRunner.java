import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;


public class PeerRunner {
    public static void main(String[] args) {
        Peer peer = new Peer("Matt's Lenovo", "http://localhost:4567/server/register");        
    
        ///////////////////////////////
        // Below are scenarios that a peer could do. Comment/uncomment for testing as necessary
        ///////////////////////////////

        // Register with server
        peer.registerWithServer();
                
        // Fetch peers
        peer.fetchPeers();

        // Request to a peer
        peer.requestFromPeer("http://192.168.1.11:4568");

        System.out.println("done");

    }
}