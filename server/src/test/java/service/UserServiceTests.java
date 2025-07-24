package service;

import dataaccess.DataAccessException;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @Test
    void registerTest() {
        Server server = new Server();
        RegisterRequest req = new RegisterRequest("username", "password", "email");
        UserData user = new UserData("username", "password", "email");
        UserService userService = new UserService(server);

        try {
            userService.clear();
            userService.register(req);
            assertThrows(DataAccessException.class, () -> server.db.userDataDAO.getUser(user.username()));
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
    }

    @Test
    void registerWithoutUsername() {
        Server server = new Server();
        RegisterRequest req = new RegisterRequest(null, "password", "email");
        UserService userService = new UserService(server);

        assertThrows(DataAccessException.class, () -> userService.register(req));
    }

    @Test
    void clearTest() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            userService.register(new RegisterRequest("user", "pass", "email"));
            String authToke = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToke);
            server.db.gameDataDAO.createGame("gameName2", authToke);
        } catch (Exception e) {
            fail();
        }

        try {
            userService.clear();
            String statement = "SELECT * FROM game";
            assertTrue(server.db.gameDataDAO.findNum(statement) == 0);
            statement = "SELECT * FROM user";
            assertTrue(server.db.userDataDAO.findNum(statement) == 0);
            statement = "SELECT * FROM auth";
            assertTrue(server.db.authDataDAO.findNum(statement) == 0);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void loginWithoutRegistering() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
        } catch (Exception e) {
            fail();
        }

        assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest("username", "password")));
    }

    @Test
    void loginAfterRegisteringAndLoggingOut() {
        Server server = new Server();
        UserService userService = new UserService(server);

        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            userService.logout(authToken.authToken());

            userService.login(new LoginRequest("user", "pass"));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void logoutWithoutHavingLoggedIn() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            userService.register(new RegisterRequest("user", "pass", "email"));
        } catch (Exception e) {
            fail();
        }
        assertThrows(DataAccessException.class, () -> userService.logout("fakeAuthToken"));
    }

    @Test
    void logoutDeletesAuthToken() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            userService.logout(authToken.authToken());
            String statement = "SELECT * FROM auth";
            assertTrue(server.db.authDataDAO.findNum(statement) == 0);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void createGameWithFakeToken() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            assertThrows(DataAccessException.class, ()->userService.createGame("FAKEAUTH", new CreateGameRequest(auth,"game")));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void createGameWithValidTokenTest() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            String statement = "SELECT * FROM game";
            assertTrue(server.db.gameDataDAO.findNum(statement) == 1);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void listGamesTest() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            userService.createGame(auth, new CreateGameRequest(auth,"game1"));
            userService.joinGame(auth, new JoinGameRequest("WHITE", 1));
            userService.createGame(auth, new CreateGameRequest(auth,"game2"));
            String statement = "SELECT * FROM game";
            assertTrue(server.db.gameDataDAO.findNum(statement) == 3);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void listGamesWithoutAuth() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            assertThrows(DataAccessException.class, () -> userService.createGame("FAKEAUTH", new CreateGameRequest("FAKEAUTH","game")));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void joinGameWithoutAuth() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            userService.createGame(auth, new CreateGameRequest(auth,"game1"));
            assertThrows(DataAccessException.class, () -> userService.joinGame("FAKEAUTH", new JoinGameRequest("WHITE", 1)));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void joinGameWithAuth() {
        Server server = new Server();
        UserService userService = new UserService(server);

        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            userService.createGame(auth, new CreateGameRequest(auth,"game1"));
            userService.joinGame(auth, new JoinGameRequest("WHITE", 1));
            boolean bool = false;

            for (GameData game : server.db.gameDataDAO.listGames(auth)) {
                if (game.whiteUsername().equals("user")) {
                    bool = true;
                    break;
                }
            }

            assertTrue(bool);

        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void loginLogoutloginAuthTokensDiff() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            userService.clear();
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.logout(auth);
            LoginResult loginResult = userService.login(new LoginRequest("user", "pass"));
            assertTrue(!auth.equals(loginResult.authToken()));
        } catch (DataAccessException e) {
            fail();
        }
    }
}
