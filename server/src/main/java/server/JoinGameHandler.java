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

public class JoinGameHandler extends GenericHandler implements Route  {
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
            if (e.getMessage().equals("Error: not authorized")) {
                return notAuthorized(e, res);
            }
            if (e.getMessage().equals("Error: bad request")) {
                return badRequest(e, res);
            }
            if (e.getMessage().equals("Error: already taken")) {
                return alreadyTaken(e, res);
            }
            else {
                return otherError(new DataAccessException("Error: internal error"), res);
            }
        }
    }
}
