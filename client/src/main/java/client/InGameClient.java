package client;

import chess.ChessMove;
import chess.ChessPosition;
import dataexception.DataAccessException;
import service.*;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class InGameClient implements NotificationHandler {
    private final ServerFacade server;
    private final BaseClient client;
    public Integer gameID;
    public WebSocketFacade webSocketFacade;

    public ChessGame game;

    public InGameClient(ServerFacade serverFacade, BaseClient ogClient, Integer gameid, WebSocketFacade webSocketFac) {
        server = serverFacade;
        client = ogClient;
        gameID = gameid;
        webSocketFacade = webSocketFac;
    }

    public String help() {
        return """
                - help
                - draw: draws game board
                - move <Start Position> <End Position>: performs move if valid
                - resign: ends chess game
                - leave: leaves the game
                - highlight <row><column>: highlights legal moves of piece at position <row><column>
                - quit
                """;
    }

    public void notify(ServerMessage message) {
        websocket.messages.ServerMessage.ServerMessageType type =
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        };
    }

    public ServerMessage.ServerMessageType displayNotification(String message) {
        System.out.println(message);
        return NOTIFICATION;
    }

    public ServerMessage.ServerMessageType displayError(String message) {
        System.out.println(String.format("Error: %s", message));
        return ERROR;
    }

    public ServerMessage.ServerMessageType loadGame(ChessGame game) {
        drawBoard();
        return LOAD_GAME;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "draw" -> drawBoard();
                case "exit" -> exitGame();
                case "resign" -> resignGame();
                case "quit" -> "quit";
                case "highlight" -> highlightPossibilities(params);
                case "move" -> performMove(params);
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String resignGame() {
        // keep game in memory but no more moves allowed?
        return client.eval("list");
    }

    public String highlightPossibilities(String... params) {
        if (params.length >= 1) {
            String position = params[0];
            String[] positions = position.split("");
            ChessPosition pos = null;
            Collection<String> letters = new ArrayList<>();
            letters.add("a");
            letters.add("b");
            letters.add("c");
            letters.add("d");
            letters.add("e");
            letters.add("f");
            letters.add("g");
            letters.add("h");
            if (letters.contains(positions[1])) {
                if (positions[1].equals("a")) {
                    positions[1] = String.valueOf(1);
                }
                if (positions[1].equals("b")) {
                    positions[1] = String.valueOf(2);
                }
                if (positions[1].equals("c")) {
                    positions[1] = String.valueOf(3);
                }
                if (positions[1].equals("d")) {
                    positions[1] = String.valueOf(4);
                }
                if (positions[1].equals("e")) {
                    positions[1] = String.valueOf(5);
                }
                if (positions[1].equals("f")) {
                    positions[1] = String.valueOf(6);
                }
                if (positions[1].equals("g")) {
                    positions[1] = String.valueOf(7);
                }
                if (positions[1].equals("h")) {
                    positions[1] = String.valueOf(8);
                }
            } else {
                System.out.println("Not a valid position.");
                return "";
            }
            try {
                pos = new ChessPosition(Integer.parseInt(positions[0]), Integer.parseInt(positions[1]));
            } catch (Exception e) {
                System.out.println("Not a valid position.");
                return "";
            }
            if (pos == null) {
                System.out.println("Not a valid position.");
                return "";
            }

            if (game.board.getPiece(pos) == null) {
                System.out.println("There is not a piece there.");
                return "";
            }
            refreshGame(gameID);
            BoardUI board = new BoardUI(game);
            board.drawHighlightedBoard(client.playerColor, pos);
        } else {
            System.out.println("Expected highlight <position>.");
        }
        return "";
    }

    public String performMove(String... params) {
        return "";
    }

    public String exitGame() {
        client.switchState("LOGGEDIN");
        String formatted = String.format("You have successfully exited the game.");
        System.out.println(formatted);
        try {
            webSocketFacade.leaveGameClient(client.authToken, gameID);
            webSocketFacade = null;
        } catch (Exception e) {
            System.out.println("Seems like there was an error exiting the game.");
        }
        return client.eval("list");
    }

    public void refreshGame(int gameID) {
        try {
            JoinGameResponse resp = server.joinGame(new JoinGameRequest(client.authToken, client.playerColor, gameID));
            this.game = resp.game();
            System.out.println("Refreshed game!");
        } catch (Exception e) {
            System.out.println("Error refreshing game.");
        }
    }

    public String drawBoard() {
        refreshGame(gameID);
        BoardUI board = new BoardUI(game);
        board.drawBoard(client.playerColor, null);
        return "";
    }
}