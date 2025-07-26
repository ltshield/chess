package server;

import DataAccessException.DataAccessException;
import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler extends GenericHandler implements Route {
    public Server server;
    public ClearHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        // no auth required to clear
        try {
            userService.clear();
        } catch (DataAccessException e) {
            return otherError(e, res);
        }
        res.type("application/json");
        Map<String, Object> objectMap = new HashMap<>();
        return new Gson().toJson(objectMap);
    }
}
