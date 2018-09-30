public class ServerRunner {
    private static String serverIPAddress = "192.168.1.3";

    public static void main(String[] args) {
        Server server = new Server(serverIPAddress);    
        System.out.println("Server running...");
    }
}