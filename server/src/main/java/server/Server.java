package server;
import dataaccess.SQLDao;
import spark.*;

public class Server {

//    public DataAccessObject db = new DataAccessObject(this);
    public SQLDao db;
    public boolean isSQL = false;
    public Server() {
        try {
            // if SQL database
            this.db = new SQLDao(this);
            this.isSQL = true;
            // if memory database
//            this.db = new DataAccessObject(this);
        } catch (Exception e) {
            System.out.println("Unable to start server.");
        }
    }

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
        Spark.put("/game", new JoinGameHandler(this));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
