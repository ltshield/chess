package server;

import dataexception.DataAccessException;
import spark.Request;
import spark.Response;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

public class LoginHandler extends GenericHandler implements Route {
    public Server server;
    public LoginHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {

        var request = new Gson().fromJson(req.body(), LoginRequest.class);
        UserService userService = new UserService(server);

        try {
            LoginResult result = userService.login(request);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: unauthorized")) {
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
