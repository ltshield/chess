package server;

import dataaccess.DataAccessException;
import model.GameData;
import service.CreateGameRequest;
import service.CreateGameResponse;
import service.LoginRequest;
import spark.Request;
import spark.Response;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.Collection;
import java.util.Map;

public class CreateGameHandler extends GenericHandler implements Route {
    public Server server;
    public CreateGameHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        try {
            String authToken = req.headers("Authorization");
            var request = new Gson().fromJson(req.body(), CreateGameRequest.class);
            CreateGameResponse result = userService.createGame(authToken, request);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: not authorized")) {
                return notAuthorized(e, res);
            }
            if (e.getMessage().equals("Error: bad request")) {
                return badRequest(e, res);
            }
            else {
                return otherError(new DataAccessException("Error: internal error"), res);
            }
        }
    }
}
