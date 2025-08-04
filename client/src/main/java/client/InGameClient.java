package client;

import dataexception.DataAccessException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;
import chess.ChessGame;

import java.util.Arrays;

public class InGameClient implements NotificationHandler {
    private final ServerFacade server;
    private final BaseClient client;
    public Integer gameID;

    public WebSocketFacade webSocketFacade;


    public InGameClient(ServerFacade serverFacade, BaseClient ogClient, Integer gameid, WebSocketFacade webSocketFac) {
        server = serverFacade;
        client = ogClient;
        gameID = gameid;
        webSocketFacade = webSocketFac;
    }

    public String help() {
        return """
                - draw: draws game board
                - exit
                - quit
                """;
    }

    public void notify(ServerMessage message) {
//        websocket.messages.ServerMessage.ServerMessageType type =
//        switch (message.getServerMessageType()) {
//            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
//            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
//            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
//        };
        System.out.println(message);
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
        try {
            webSocketFacade.leaveGameClient(client.authToken, gameID);
        } catch (Exception e) {
            System.out.println("Seems like there was an error exiting the game.");
        }
        return client.eval("list");
    }

    public String drawBoard() {
        BoardUI board = new BoardUI(new ChessGame());
        board.drawBoard(client.playerColor);
        return "";
    }
}