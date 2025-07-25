import dataaccess.DataAccessException;
import server.ServerFacade;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final Client client;

    public InGameClient(ServerFacade serverFacade, Client OgClient) {
        server = serverFacade;
        client = OgClient;
    }

    public String help() {
        return """
                - redraw
                """;
    }

    public String eval(String input) {
//        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> drawBoard();
                case "quit" -> "quit";
                default -> help();
            };
//        } catch (DataAccessException e) {
//            return e.getMessage();
//        }
    }

    public String drawBoard() {
        return "";
    }

}
