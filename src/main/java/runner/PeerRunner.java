package runner;

import peer.Peer;

public class PeerRunner {
    private static String friendlyName = "Matt's Lenovo";
    private static String serverIpAddress = "http://192.168.1.13:4567";

    public static void main(String[] args) {
        
        Peer peer = new Peer(friendlyName, serverIpAddress);        
    
        ///////////////////////////////
        // Below are scenarios that a peer could do. Comment/uncomment for testing as necessary
        ///////////////////////////////

        // Register with server
        peer.registerWithServer();
        System.out.println("Finished registering with server\n");        

        // Fetch peers
        peer.fetchPeers();
        System.out.println("Finished fetching peers\n");

        // Request to a peer
        peer.requestFromPeer("http://192.168.1.11:4568");
        System.out.println("Finished requesting from peer\n");

        // Disconnect from server
        peer.disconnectFromServer();
        System.out.println("Finished disconnecting from server\n");        

        System.out.println("Peer running...");

    }
}