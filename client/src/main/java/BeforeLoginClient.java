import dataaccess.DataAccessException;
import server.ServerFacade;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;

import java.util.Arrays;

public class BeforeLoginClient {
    private final ServerFacade server;
    private final Client client;

    public BeforeLoginClient(ServerFacade serverFacade, Client OgClient) {
        server = serverFacade;
        client = OgClient;
    }
    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws DataAccessException {
        if (params.length >= 1) {
            RegisterResult res = server.register(new RegisterRequest(params[0], params[1], params[2]));
            client.authToken = res.authToken();
            client.switchState("LOGGEDIN");
            return String.format("You have successfully registered. Welcome to the server %s.", params[0]);
        }
        throw new DataAccessException("Expected: <username> <password> <email>.");
    }

    public String login(String... params) throws DataAccessException {
        if (params.length >= 1) {
            LoginResult res = server.login(new LoginRequest(params[0], params[1]));
            client.authToken = res.authToken();
            client.switchState("LOGGEDIN");
            return String.format("Welcome back %s!", params[0]);
        }
        throw new DataAccessException("Expected: <username> <password>.");
    }

}
