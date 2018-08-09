import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;
import spark.RouteGroup;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;


public class Server {
    
    static final String CONTENT_TYPE_JSON = "application/json";
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Server server;


    private Server() {
        // TODO
    }

    public static void main(String[] args) {
        System.out.println("Starting server");

        Server server = new Server();
        path("/server", server.routes());

        System.out.println("Server running...");
    }

    private RouteGroup routes() {
        return () ->  {
            before("/*", (request, response) -> System.out.println("endpoint: " + request.pathInfo()));
            post("/register", this::registerPeer, gson::toJson);
            get("/", (request, response) -> "GET to fetch");
        };
    }

    private boolean registerPeer(Request request, Response response) {
        response.type(CONTENT_TYPE_JSON);

        Registration registration = gson.fromJson(request.body(), new TypeToken<Registration>() {}.getType());
        if (!registration.validate()) {
            return false;
        }

        return true;
    }

}
