import java.net.InetAddress;

import com.google.gson.annotations.Expose;


public class Registration {

    @Expose
    String ipAddress;

    @Expose
    int port;

    @Expose
    String friendlyName;


    public Registration(String ipAddress, int port, String friendlyName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.friendlyName = friendlyName;
    }

    public boolean validate() {
        // Validate IP
        if (ipAddress == null || ipAddress.equals("")) {
            System.out.println("Registration not validated - invalid IP: " + ipAddress);
            return false;
        }

        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
        } catch (Exception e) {
            System.out.println("Registration not validated - invalid IP: " + ipAddress);
            return false;
        }

        // Validate friendly name
        if (friendlyName == null || friendlyName.equals("")) {
            System.out.println("Registration not validated - invalid friendly name: " + friendlyName);
            return false;
        }

        System.out.println("Registration validated");
        return true;
    }

    /**
     * Get full address that allows for communication (i.e. IP + port).
     * @return String - IP + port
     */
    public String getFullAddress() {
        return this.ipAddress + ":" + String.valueOf(this.port);
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public int getPort() {
        return this.port; 
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}