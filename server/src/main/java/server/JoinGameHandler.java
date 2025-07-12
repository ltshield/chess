package server;

import dataaccess.DataAccessException;
import model.GameData;
import service.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import spark.Route;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JoinGameHandler implements Route {
    public Server server;
    public JoinGameHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        try {
            String authToken = req.headers("Authorization");
            var request = new Gson().fromJson(req.body(), JoinGameRequest.class);
            userService.joinGame(authToken, request);
            res.type("application/json");
            return new Gson().toJson(new HashMap<String, Object>());
        } catch (DataAccessException e) {
            System.out.println(e);
            if (e.getMessage().equals("Error: not authorized")) {
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
            if (e.getMessage().equals("Error: already taken")) {
                var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
                res.type("application/json");
                res.status(403);
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
