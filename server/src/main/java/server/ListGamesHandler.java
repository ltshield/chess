package server;

import dataaccess.DataAccessException;
import model.GameData;
import service.ClearRequest;
import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ListGamesHandler implements Route {
    public Server server;
    public ListGamesHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        try {
            String authToken = req.headers("Authorization");
            Collection<GameData> games = userService.listGames(authToken);
            res.type("application/json");
            return new Gson().toJson(games);
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