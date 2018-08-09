public class Peer {
    
    String ipAddress;
    String friendlyName;

    
    public Peer(Registration registration) {
        this.ipAddress = registration.getIPAddress();
        this.friendlyName = registration.getFriendlyName();
    }

    @Override
    public String toString() {
        return this.ipAddress + ", " + this.friendlyName;
    }
}