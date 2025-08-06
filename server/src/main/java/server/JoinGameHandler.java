package server;

import dataexception.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import spark.Route;

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
            JoinGameResponse resp = userService.joinGame(fullGameReq);
            res.type("application/json");
            res.status(200);
            return new Gson().toJson(resp);
        } catch (DataAccessException e) {
            return handleErrors(e, res);
        }
    }
}
