package client;

import chess.ChessGame;
import dataexception.DataAccessException;
import service.LoginRequest;
import service.LoginResult;

import java.util.Arrays;

public class InGameClient {
    private final ServerFacade server;
    private final BaseClient client;
    public Integer gameID;


    public InGameClient(ServerFacade serverFacade, BaseClient ogClient, Integer gameid) {
        server = serverFacade;
        client = ogClient;
        gameID = gameid;
    }

    public String help() {
        return """
                - draw: draws game board
                - exit
                - quit
                """;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "draw" -> drawBoard();
            case "exit" -> exitGame();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String exitGame() {
        client.switchState("LOGGEDIN");
        String formatted = String.format("You have successfully exited the game.");
        System.out.println(formatted);
        return client.eval("list");
    }
    public String drawBoard() {
        BoardUI board = new BoardUI(new ChessGame());
        board.drawBoard(client.playerColor);
        return "";
    }
}