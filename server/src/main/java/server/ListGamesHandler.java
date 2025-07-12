package server;

import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import model.GameData;
import service.ClearRequest;
import service.ListGameResponse;
import service.ListGamesResponse;
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
            Object games = userService.listGames(authToken);
            // need to use this method in order to serialize any null values (like blackUsername or whiteUsername)
            Gson gson = new GsonBuilder().serializeNulls().create();
            res.type("application/json");
            System.out.println(gson.toJson(games));
            return gson.toJson(games);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: not authorized")) {
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