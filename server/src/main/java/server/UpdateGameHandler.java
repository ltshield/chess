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
            var ree = new Gson().fromJson(req.body(), UpdateGameRequest.class);
            UpdateGameRequest fullGameReq = new UpdateGameRequest(authToken, ree.game(), ree.gameID(), ree.username(), ree.playerColor());
            UpdateGameResponse resp = userService.updateGame(fullGameReq);
            res.type("application/json");
            return new Gson().toJson(resp, UpdateGameResponse.class);
        } catch (DataAccessException e) {
            return handleErrors(e, res);
        }
    }
}