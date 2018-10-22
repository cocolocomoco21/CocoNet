import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.StringUtil;


public class Utilities {
    
    /**
     * Create endpoint.
     * 
     * Base url includes protocol (HTTP), domain/IP address, and port.
     * Endpoint should contain the full path, including resources and endpoint.
     */
    public static String formURL(String base, String endpoint) {
        return formURL(base, endpoint, null);
    }

    /**
     * Create endpoint.
     * 
     * Base url includes protocol (HTTP), domain/IP address, and port.
     * Endpoint should contain the full path, including resources and endpoint.
     * Query params should contain all query params to be appended to URL.
     */
    public static String formURL(String base, String endpoint, List<String> queryParams) {
        if (StringUtil.isBlank(base) || StringUtil.isBlank(endpoint)) {
            // TODO error handle?
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(base);
        builder.append(endpoint);

        if (queryParams != null) {
            queryParams.forEach(param -> builder.append(param));
        }

        return builder.toString();
    }

    /**
     * Send GET request to specified URL.
     */
    public static ContentResponse sendGetRequest(String url)  {
        ContentResponse resp;
        try {
            HttpClient client = new HttpClient();
            client.start();
            
            resp = client.GET(url);
            
            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());

            return resp;
        } catch (Exception e) {
            // TODO better error handle?
            e.printStackTrace();
        }
        
        return null;
    }
}
