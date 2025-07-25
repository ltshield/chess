import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final Client client;
    public Integer gameID;

    public InGameClient(ServerFacade serverFacade, Client OgClient, Integer gameid) {
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
//        GameData game = null;
//        Gson gson = new Gson();
//        Object obj = null;
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT * FROM game WHERE id=?";
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, gameID);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        obj = rs.getObject("game");
//                        String json1 = obj.getAsJsonArray("board");
//                        ChessBoard boardToDraw = gson.fromJson(json1, ChessBoard.class);
//                        String turn = rs.getString("turn");
//                        BoardUI board = new BoardUI(boardToDraw);
//                        board.drawBoard(client.playerColor);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("error");
//        }
        BoardUI board = new BoardUI(new ChessGame());
        board.drawBoard(client.playerColor);
        return "";
    }
}