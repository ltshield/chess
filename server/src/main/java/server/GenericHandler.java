package server;

import com.google.gson.Gson;
import DataAccessException.DataAccessException;
import spark.Response;

import java.util.Map;

public class GenericHandler {

    public Object notAuthorized(DataAccessException e, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
        res.type("application/json");
        res.status(401);
        res.body(body);
        return body;
    }

    public Object badRequest(DataAccessException e, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
        res.type("application/json");
        res.status(400);
        res.body(body);
        return body;
    }

    public Object alreadyTaken(DataAccessException e, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
        res.type("application/json");
        res.status(403);
        res.body(body);
        return body;
    }

    public Object otherError(DataAccessException e, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
}
