package server;

import dataaccess.DataAccessException;
import service.LogoutRequest;
import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.Map;

public class LogoutHandler implements Route {
    public Server server;
    public LogoutHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {
        var request = new Gson().fromJson(req.body(), LogoutRequest.class);
        UserService userService = new UserService(server);

        try {
            userService.logout(request);
            var body = new Gson();
            res.type("application/json");
            res.status(200);
            res.body();
            return body;
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
                res.type("application/json");
                res.status(401);
                res.body(body);
                return body;
            }
            else {
                var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
                res.type("application/json");
                res.status(500);
                res.body(body);
                return body;
            }
        }
    }
}