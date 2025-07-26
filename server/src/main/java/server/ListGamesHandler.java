package server;

import com.google.gson.GsonBuilder;
import dataexception.DataAccessException;
import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

public class ListGamesHandler extends GenericHandler implements Route {
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
//            System.out.println(gson.toJson(games));
            res.status(200);
            return gson.toJson(games);
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