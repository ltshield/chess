package server;

import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler implements Route {
    public Server server;
    public ClearHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        // no auth required to clear
        userService.clear();
        res.type("application/json");
        Map<String, Object> objectMap = new HashMap<>();
        return new Gson().toJson(objectMap);
    }
}
