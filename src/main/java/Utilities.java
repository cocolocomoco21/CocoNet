import java.util.List;
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
}
