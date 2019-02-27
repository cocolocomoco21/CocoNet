package util;

import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.StringUtil;


public class Utilities {

    static final String CONTENT_TYPE_JSON = "application/json";
    
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
    public static ContentResponse sendGETRequest(String url)  {
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

    public static ContentResponse sendPOSTRequest(String url, String json) {
        try {
            // Send registration to server
            HttpClient client = new HttpClient();
            client.start();
    
            Request request = client.POST(url)
                .header(HttpHeader.ACCEPT, CONTENT_TYPE_JSON)
                .header(HttpHeader.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .content(new StringContentProvider(json), CONTENT_TYPE_JSON);

            ContentResponse resp = request.send();

            System.out.println("Response: " + resp.toString());
            System.out.println("Content: " + resp.getContentAsString());
            
            return resp;
           
        } catch (Exception e) {
            // TODO this can be better
            System.out.println(e.getMessage());
        }

        return null;
    }
}
