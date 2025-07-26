package client;

import chess.ChessGame;
import com.google.gson.Gson;
import DataAccessException.DataAccessException;
import dataaccess.DatabaseManager;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final BaseClient client;
    public Integer gameID;


    public InGameClient(ServerFacade serverFacade, BaseClient OgClient, Integer gameid) {
        server = serverFacade;
        client = OgClient;
        gameID = gameid;
    }

    public String help() {
        return """
                - redraw
                """;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "redraw" -> drawBoard();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String drawBoard() {
        BoardUI board = new BoardUI(new ChessGame());
        board.drawBoard(client.playerColor);
        return "";
    }
}