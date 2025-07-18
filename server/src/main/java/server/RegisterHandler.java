package server;

import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;
import com.google.gson.Gson;
import spark.Route;

import java.util.Map;

public class RegisterHandler extends GenericHandler implements Route {
    public Server server;
    public RegisterHandler(Server server) {
        this.server = server;
    }

    public Object handle(Request req, Response res) {
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService userService = new UserService(server);

        try {
            RegisterResult result = userService.register(request);
            res.type("application/json");
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: already taken")) {
                return alreadyTaken(e, res);
            }
            if (e.getMessage().equals("Error: bad request")) {
                return badRequest(e, res);
            }
            else {
                return otherError(e, res);
            }
        }
    }
}
