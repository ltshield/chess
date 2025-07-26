package client;

import dataexception.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import service.*;

import static org.junit.jupiter.api.Assertions.*;


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
    public void clearTest() {

        try {
            serverFacade.register(new RegisterRequest("user", "pass", "email"));
            String authToke = server.db.authDataDAO.createAuth("user");
            serverFacade.createGame(new CreateGameRequest(authToke, "gameName"));
            serverFacade.createGame(new CreateGameRequest(authToke, "game2"));
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
    public void invalidCreateGame() {

        try {
            RegisterResult user = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            CreateGameRequest req = new CreateGameRequest("FAKEAUTH", "gameName");
            assertThrows(DataAccessException.class, () -> serverFacade.createGame(req));

        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void validRegisterTest() {

        try {
            RegisterRequest req = new RegisterRequest("user", "pass", "email");
            serverFacade.register(req);
            String statement = "SELECT * FROM user WHERE username = \"user\"";
            assertTrue(server.db.userDataDAO.checkIfIn(statement));
        } catch (Exception e) {
            System.out.println(e);
            fail();
        }

    }

    @Test
    public void invalidRegisterTest() {
        try {
            RegisterRequest req = new RegisterRequest("user", "pass", "email");
            serverFacade.register(req);
            assertThrows(DataAccessException.class, () -> serverFacade.register(req));

        } catch (Exception e) {
            System.out.println(e);
            fail();
        }
    }

    @Test
    public void validLoginTest() {
        try {
            RegisterRequest req = new RegisterRequest("user", "pass", "email");
            RegisterResult res = serverFacade.register(req);
            serverFacade.logout(new LogoutRequest(res.authToken()));
            LoginResult res2 = serverFacade.login(new LoginRequest("user", "pass"));
            String statement = "SELECT * FROM auth WHERE authToken = \"" + res2.authToken() + "\"";
            assertTrue(server.db.authDataDAO.checkIfIn(statement));
        } catch (Exception e) {
            System.out.println(e);
            fail();
        }
    }

    @Test
    public void invalidLoginTest() {
        assertThrows(DataAccessException.class, () -> serverFacade.login(new LoginRequest("us", "pa")));
    }

    @Test
    public void invalidLogoutTest() {
        try {
            RegisterResult res = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            assertThrows(DataAccessException.class, () -> serverFacade.logout(new LogoutRequest("FAKEAUTH")));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    public void validLogoutTest() {
        try {
            RegisterResult res = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            serverFacade.logout(new LogoutRequest(res.authToken()));
            String statement = "SELECT * FROM auth";
            assertTrue(server.db.authDataDAO.findNum(statement) == 0);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    public void validListGamesTest() {
        try {
            RegisterResult res = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            String auth = res.authToken();
            serverFacade.createGame(new CreateGameRequest(auth,"game"));
            serverFacade.createGame(new CreateGameRequest(auth,"game1"));
            serverFacade.joinGame(new JoinGameRequest(auth, "WHITE", 1));
            serverFacade.createGame(new CreateGameRequest(auth,"game2"));
            String statement = "SELECT * FROM game";
            assertTrue(server.db.gameDataDAO.findNum(statement) == 3);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    public void invalidListGamesTest() {
        try {
            RegisterResult authToken = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            assertThrows(DataAccessException.class, () -> serverFacade.createGame(new CreateGameRequest("FAKEAUTH","game")));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    public void validJoinGameTest() {
        try {
            RegisterResult authToken = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            serverFacade.createGame(new CreateGameRequest(auth,"game"));
            serverFacade.createGame(new CreateGameRequest(auth,"game1"));
            serverFacade.joinGame(new JoinGameRequest(auth,"WHITE", 1));
            String statement = "SELECT * FROM game WHERE whiteUsername = \"user\"";
            assertTrue(server.db.gameDataDAO.checkIfIn(statement));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    public void invalidJoinGameTest() {
        try {
            RegisterResult authToken = serverFacade.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            serverFacade.createGame(new CreateGameRequest(auth,"game"));
            serverFacade.createGame(new CreateGameRequest(auth,"game1"));
            assertThrows(DataAccessException.class, () -> serverFacade.joinGame(new JoinGameRequest("FAKEAUTH","WHITE", 1)));
        } catch (DataAccessException e) {
            fail();
        }
    }
}
