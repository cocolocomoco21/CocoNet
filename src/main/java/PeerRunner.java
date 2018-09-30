public class PeerRunner {
    private static String friendlyName = "Matt's Lenovo";
    private static String ipAddress = "http://192.168.1.4:4567/server/register";

    public static void main(String[] args) {
        
        Peer peer = new Peer(friendlyName, ipAddress);        
    
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