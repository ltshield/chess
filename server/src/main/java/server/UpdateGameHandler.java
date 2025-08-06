package server;

import com.google.gson.Gson;
import dataexception.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateGameHandler extends GenericHandler implements Route {
    public Server server;
    public UpdateGameHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        try {
            String authToken = req.headers("Authorization");
            var request = new Gson().fromJson(req.body(), UpdateGameRequest.class);
            UpdateGameRequest fullGameReq = new UpdateGameRequest(authToken, request.game(), request.gameID(), request.username(), request.playerColor());
            UpdateGameResponse resp = userService.updateGame(fullGameReq);
            res.type("application/json");
            return new Gson().toJson(resp, UpdateGameResponse.class);
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