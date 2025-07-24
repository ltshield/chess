package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.CreateGameRequest;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clearEach() {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clearTest() {

        try {
            serverFacade.register(new RegisterRequest("user", "pass", "email"));
            String authToke = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToke);
            server.db.gameDataDAO.createGame("gameName2", authToke);
            serverFacade.clear();
            String statement = "SELECT * FROM game";
            assertTrue(server.db.gameDataDAO.findNum(statement) == 0);
            statement = "SELECT * FROM user";
            assertTrue(server.db.userDataDAO.findNum(statement) == 0);
            statement = "SELECT * FROM auth";
            assertTrue(server.db.authDataDAO.findNum(statement) == 0);
        } catch (Exception e) {
            System.out.println(e);
            fail();
        }

    }

    @Test
    public void validCreateGame() {

        try {
            RegisterResult user = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            CreateGameRequest req = new CreateGameRequest(user.authToken(), "gameName");
            serverFacade.createGame(req);
            String statement = "SELECT * FROM game";
            assertTrue(server.db.gameDataDAO.findNum(statement) == 1);

        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void validRegisterTest() {

        try {
            RegisterRequest req = new RegisterRequest("user", "pass", "email");
            serverFacade.register(req);

        } catch (Exception e) {
            System.out.println(e);
            fail();
        }

    }

}
