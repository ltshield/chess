import dataaccess.DataAccessException;
import model.GameData;
import server.ServerFacade;
import service.*;

import java.util.Arrays;

public class AfterLoginClient {
    private final ServerFacade server;
    private final Client client;

    public AfterLoginClient(ServerFacade serverFacade, Client OgClient) {
        server = serverFacade;
        client = OgClient;
    }
    public String help() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> <WHITE|BLACK> - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
//                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String create(String... params) throws DataAccessException {
        if (params.length >= 1) {
            CreateGameResponse res = server.createGame(new CreateGameRequest(client.authToken, params[0]));
            return String.format("Game %s created. If you would like to join use ID: %d", params[0], res.gameID());
        }
        throw new DataAccessException("Expected: <NAME>");
    }

    public String list(String... params) throws DataAccessException {
        ListGamesResponse res = server.listGames(new ListGamesRequest(client.authToken));
        String string = "";
        if (res.games() == null) {
            string = "No games yet.";
            return string;
        }
        for (ListGameResponse game : res.games()) {
            string = string + String.format("\n %d %s: %s | %s", game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername());
        }
        return string;
        }

    public String join(String... params) throws DataAccessException {
        if (params.length >= 1) {
            int ID = 100;
            try {ID = Integer.parseInt(params[0]);}
            catch (Exception e) {throw new DataAccessException("Game ID must be integer.");}
            server.joinGame(new JoinGameRequest(client.authToken, params[1].toUpperCase(), ID));
            client.switchState("INGAME");
            return String.format("Successfully joined game! Good luck!");
        }
        throw new DataAccessException("Expected: <ID>");
    }

    public String logout(String... params) throws DataAccessException {
        try {
            server.logout(new LogoutRequest(client.authToken));
            System.out.println("Successfully logged out.");
            client.switchState("LOGGEDOUT");
            return "";
        } catch (Exception e) {
            throw new DataAccessException("Bad request.");
        }
    }
}
