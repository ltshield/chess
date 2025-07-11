package server;

import java.util.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import dataaccess.GameDataDAO;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;
import spark.*;

public class Server {

    public DataAccessObject db = new DataAccessObject(this);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();
//        Spark.post("/user", this::registerHandler);
        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object registerHandler(Request req, Response res) {
        var registerRequest = getBody(req, RegisterRequest.class);
        UserService userService = new UserService(this);
        try {
            userService.register(registerRequest);
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }

        res.type("application/json");
        return new Gson().toJson(registerRequest);
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(400);
        res.body(body);
        return body;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
