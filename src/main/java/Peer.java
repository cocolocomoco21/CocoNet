import java.net.InetAddress;


public class Peer {
    
    String ipAddress;
    String friendlyName;

    public static void main(String[] args) {
        Peer peer = new Peer("Matt's Lenovo");
    }
    
    private Peer(String friendlyName) {
        initialize(friendlyName);
    }

    public Peer(Registration registration) {
        this.ipAddress = registration.getIPAddress();
        this.friendlyName = registration.getFriendlyName();
    }

    private void initialize(String friendlyName) {
        this.friendlyName = friendlyName;

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

    @Override
    public String toString() {
        return this.ipAddress + ", " + this.friendlyName;
    }
}