package server;

import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.Map;

public class RegisterHandler implements Route {
    public Server server;
    public RegisterHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService userService = new UserService(server);

        try {
            RegisterResult result = userService.register(request);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: already taken")) {
                var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
                res.type("application/json");
                res.status(403);
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
