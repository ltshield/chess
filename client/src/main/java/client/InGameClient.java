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
        Gson gson = new Gson();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame gameObj = gson.fromJson(rs.getString("game"), ChessGame.class);
                        BoardUI board = new BoardUI(gameObj);
                        board.drawBoard(client.playerColor);
                    } else {
                        throw new DataAccessException("Error.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}