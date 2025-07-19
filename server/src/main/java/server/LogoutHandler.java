package server;

import dataaccess.DataAccessException;
import service.LogoutRequest;
import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class LogoutHandler extends GenericHandler implements Route {
    public Server server;
    public LogoutHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        try {
            // authorization is hidden in the header now
            String authToken = req.headers("Authorization");
            userService.logout(authToken);
            res.type("application/json");
            return new Gson().toJson(new HashMap<String, Object>());
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: not authorized")) {
                return notAuthorized(e, res);
            }
            else {
                return otherError(new DataAccessException("Error: internal error"), res);
            }
        }
    }
}