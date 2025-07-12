package server;

import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.Map;

public class LoginHandler implements Route {
    public Server server;
    public LoginHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        var request = new Gson().fromJson(req.body(), LoginRequest.class);
        UserService userService = new UserService(server);

        try {
            LoginResult result = userService.login(request);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
                res.type("application/json");
                res.status(401);
                res.body(body);
                return body;
            }
            if (e.getMessage().equals("Error: bad request")) {
                var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
                res.type("application/json");
                res.status(400);
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
