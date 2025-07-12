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
            userService.register(req);
            assertTrue(server.db.userDataDAO.users.contains(user));
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

        server.db.gameDataDAO.currentGameData.add(new GameData(1, null, null, null, null));
        server.db.authDataDAO.currentUsers.add(new AuthData("auth", "user"));
        server.db.userDataDAO.users.add(new UserData("user", "pass", "email"));
        userService.clear();
        assertTrue(server.db.gameDataDAO.currentGameData.isEmpty());
        assertTrue(server.db.authDataDAO.currentUsers.isEmpty());
        assertTrue(server.db.userDataDAO.users.isEmpty());
    }

    @Test
    void loginWithoutRegistering() {
        Server server = new Server();
        UserService userService = new UserService(server);

        assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest("username", "password")));
    }

    @Test
    void loginAfterRegisteringAndLoggingOut() {
        Server server = new Server();
        UserService userService = new UserService(server);

        try {
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
        server.db.userDataDAO.users.add(new UserData("user", "pass", "email"));

        assertThrows(DataAccessException.class, () -> userService.logout("fakeAuthToken"));
    }

    @Test
    void logoutDeletesAuthToken() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            userService.logout(authToken.authToken());
            assertTrue(server.db.authDataDAO.currentUsers.isEmpty());
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void createGameWithSameName() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            assertThrows(DataAccessException.class, ()->userService.createGame(auth, new CreateGameRequest(auth,"game")));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void createGameWithValidTokenTest() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            assertTrue(server.db.gameDataDAO.currentGameData.size() == 1);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void listGamesTest() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            userService.createGame(auth, new CreateGameRequest(auth,"game1"));
            userService.joinGame(auth, new JoinGameRequest("WHITE", 0));
            userService.createGame(auth, new CreateGameRequest(auth,"game2"));
            assertTrue(server.db.gameDataDAO.currentGameData.size() == 3);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void listGamesWithoutAuth() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
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
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            userService.createGame(auth, new CreateGameRequest(auth,"game1"));
            assertThrows(DataAccessException.class, () -> userService.joinGame("FAKEAUTH", new JoinGameRequest("WHITE", 0)));
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void joinGameWithAuth() {
        Server server = new Server();
        UserService userService = new UserService(server);
        try {
            RegisterResult authToken = userService.register(new RegisterRequest("user", "pass", "email"));
            String auth = authToken.authToken();
            userService.createGame(auth, new CreateGameRequest(auth,"game"));
            userService.createGame(auth, new CreateGameRequest(auth,"game1"));
            userService.joinGame(auth, new JoinGameRequest("WHITE", 0));
            boolean bool = false;
            for (GameData game : server.db.gameDataDAO.currentGameData) {
                if (game.whiteUsername() == "user") {
                    bool = true;
                    break;
                }
            }
            assertTrue(bool);

        } catch (DataAccessException e) {
            fail();
        }
    }
}
