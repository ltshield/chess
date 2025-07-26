package server;

import dataexception.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import spark.Route;

import java.util.HashMap;

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
            JoinGameRequest fullGameReq = new JoinGameRequest(authToken, request.playerColor(), request.gameID());
            userService.joinGame(fullGameReq);
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
