import spark.RouteGroup;

import static spark.Spark.init;
import static spark.Spark.path;
import static spark.Spark.*;
import static spark.Spark.staticFiles;

public class Server {
    public static void main(String[] args) {
        System.out.println("Starting server");

        path("/p2p", routes());
    }

    public static RouteGroup routes() {
        return () ->  {
            before("/*", (request, response) -> System.out.println("endpoint: " + request.pathInfo()));
            post("/register", (request, response) -> "POST to register");
            get("/", (request, response) -> "GET to fetch");
        };
    }
}