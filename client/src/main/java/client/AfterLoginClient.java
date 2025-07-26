package client;

import DataAccessException.DataAccessException;
import dataaccess.DatabaseManager;
import service.*;

import java.util.Arrays;

public class AfterLoginClient {
    private final ServerFacade server;
    private final BaseClient client;

    public AfterLoginClient(ServerFacade serverFacade, BaseClient OgClient) {
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
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException e) {
            return e.getMessage();
        }
    }

    public String observe(String... params) throws DataAccessException {
        if (params.length >= 1) {
            Integer ID = null;
            try {
                ID = Integer.valueOf(params[0]);
            } catch (Exception e) {
                throw new DataAccessException("Expected integer value for ID.");
            }
            if (ID != null) {
                // if not a valid ID, throw error.
                if(!validIDCheck(ID)) {
                    throw new DataAccessException("Sorry, that is not a valid ID.");
                }
                client.playerColor = "OBSERVING";
                client.inGameClient.gameID = ID;
                client.switchState("INGAME");
                return "Successfully viewing game.";
            }
        }
        throw new DataAccessException("Error, something went wrong.");
    }

    public boolean validIDCheck(Integer ID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, ID);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public String create(String... params) throws DataAccessException {
        if (params.length >= 1) {
            CreateGameResponse res = server.createGame(new CreateGameRequest(client.authToken, params[0]));
            return String.format("Game %s created. If you would like to join use ID: %d", params[0], res.gameID());
        }
        throw new DataAccessException("Expected: <NAME>");
    }

    public String list() throws DataAccessException {
        System.out.println("Here are the current games:");
        ListGamesResponse res = server.listGames(new ListGamesRequest(client.authToken));
        String string = "";
        if (res.games() == null) {
            string = "No games yet.";
            return string;
        }
        for (ListGameResponse game : res.games()) {
            string = string + String.format("\n %d %s: %s | %s", game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername());
        }
        string += "\n";
        return string;
        }

    public String join(String... params) throws DataAccessException {
        try {
            if (params.length >= 1) {
                int ID = 100;
                try {
                    ID = Integer.parseInt(params[0]);
                } catch (Exception e) {
                    throw new DataAccessException("Game ID must be integer.");
                }
                String playerColor = params[1];
                playerColor = playerColor.toUpperCase();
                if(!validIDCheck(ID)) {
                    throw new DataAccessException("Sorry, that is not a valid ID.");
                }
                if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                    throw new DataAccessException("Sorry, that is not a valid player color.");
                }
                server.joinGame(new JoinGameRequest(client.authToken, playerColor, ID));
                client.playerColor = playerColor;
                client.inGameClient.gameID = ID;
                client.switchState("INGAME");
                return String.format("Successfully joined game! Good luck!");
            }
        } catch (Exception e) {
            if (e instanceof DataAccessException) {throw e;}
            if (e instanceof ArrayIndexOutOfBoundsException) {throw new DataAccessException("That is not a valid ID.");}
            else {throw new DataAccessException("Expected: <ID> <WHITE|BLACK>");}
        }
        return "";
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
