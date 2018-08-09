import java.net.InetAddress;

import com.google.gson.annotations.Expose;


public class Registration {

    @Expose
    String ipAddress;
    
    @Expose
    String friendlyName;


    public Registration(String ipAddress, String friendlyName) {
        this.ipAddress = ipAddress;
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

    public String getIPAddress() {
        return this.ipAddress;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}