package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataexception.DataAccessException;
import service.ChessGameRequest;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetGameHandler extends GenericHandler implements Route {

    public Server server;

    public GetGameHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        UserService userService = new UserService(server);

        try {
            String authToken = req.headers("Authorization");
            var request = new Gson().fromJson(req.body(), ChessGameRequest.class);
            ChessGameRequest fullGameReq = new ChessGameRequest(authToken, request.gameID());
            ChessGame game = userService.getGameBoard(fullGameReq.gameID());
            res.type("application/json");
            return new Gson().toJson(game);
        } catch (DataAccessException e) {
            return handleErrors(e, res);
        }
    }
}