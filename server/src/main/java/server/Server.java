package server;
import com.google.gson.Gson;
import dataaccess.DataAccessObject;
import spark.*;

public class Server {

    public DataAccessObject db = new DataAccessObject(this);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.delete("/db", new ClearHandler(this));
        Spark.post("/user", new RegisterHandler(this));
        Spark.post("/session", new LoginHandler(this));
        Spark.delete("/session", new LogoutHandler(this));
        Spark.get("/game", new ListGamesHandler(this));

        Spark.post("/game", new CreateGameHandler(this));
//        Spark.put("/game", new JoinGameHandler(this));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
