package client;

import chess.ChessMove;
import chess.ChessPiece;
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
//        websocket.messages.ServerMessage.ServerMessageType type =
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame();
        };
    }

    public void displayNotification(String message) {
        System.out.println(message);
//        return NOTIFICATION;
    }

    public void displayError(String message) {
        System.out.println(String.format("Error: %s", message));
//        return ERROR;
    }

    public void loadGame() {
        drawBoard();
//        return LOAD_GAME;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "draw" -> drawBoard();
                case "leave" -> exitGame();
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
        if (client.playerColor.equals("OBSERVING")) {
            System.out.println("Error: observers cannot resign.");
        }
        game.resigned = true;
        try {
            server.updateGame(new UpdateGameRequest(client.authToken, game, gameID, client.username, client.playerColor));
            webSocketFacade.resignGameClient(client.authToken, gameID);
        }
        catch (Exception e) {
            System.out.println("Something went wrong with resigning.");
        }
        return client.eval("help");
    }

    public String highlightPossibilities(String... params) {
        if (params.length >= 1) {
            String position = params[0];
            String[] positions = position.split("");
            ChessPosition pos = null;
            if (client.playerColor.equals("WHITE")) {
                positions = convertToIntegerWhite(positions);
            }
            if (client.playerColor.equals("BLACK")) {
                positions = convertToIntegerBlack(positions);
            }
            if (client.playerColor.equals("OBSERVING")) {
                positions = convertToIntegerWhite(positions);
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

    public String[] convertToIntegerWhite(String[] strings) {
        Collection<String> letters = new ArrayList<>();
        letters.add("a");
        letters.add("b");
        letters.add("c");
        letters.add("d");
        letters.add("e");
        letters.add("f");
        letters.add("g");
        letters.add("h");
        if (letters.contains(strings[1])) {
            if (strings[1].equals("a")) {
                strings[1] = String.valueOf(1);
            }
            if (strings[1].equals("b")) {
                strings[1] = String.valueOf(2);
            }
            if (strings[1].equals("c")) {
                strings[1] = String.valueOf(3);
            }
            if (strings[1].equals("d")) {
                strings[1] = String.valueOf(4);
            }
            if (strings[1].equals("e")) {
                strings[1] = String.valueOf(5);
            }
            if (strings[1].equals("f")) {
                strings[1] = String.valueOf(6);
            }
            if (strings[1].equals("g")) {
                strings[1] = String.valueOf(7);
            }
            if (strings[1].equals("h")) {
                strings[1] = String.valueOf(8);
            }
        }
        return strings;
    }

    public String[] convertToIntegerBlack(String[] strings) {
        Collection<String> letters = new ArrayList<>();
        letters.add("a");
        letters.add("b");
        letters.add("c");
        letters.add("d");
        letters.add("e");
        letters.add("f");
        letters.add("g");
        letters.add("h");
        if (letters.contains(strings[1])) {
            if (strings[1].equals("a")) {
                strings[1] = String.valueOf(8);
            }
            if (strings[1].equals("b")) {
                strings[1] = String.valueOf(7);
            }
            if (strings[1].equals("c")) {
                strings[1] = String.valueOf(6);
            }
            if (strings[1].equals("d")) {
                strings[1] = String.valueOf(5);
            }
            if (strings[1].equals("e")) {
                strings[1] = String.valueOf(4);
            }
            if (strings[1].equals("f")) {
                strings[1] = String.valueOf(3);
            }
            if (strings[1].equals("g")) {
                strings[1] = String.valueOf(2);
            }
            if (strings[1].equals("h")) {
                strings[1] = String.valueOf(1);
            }
        }
        return strings;
    }

    public String performMove(String... params) {
        if (game.resigned) {
            System.out.println("Error: game is over.");
            return "";
        }
        if (client.playerColor.equals("OBSERVING")) {
            System.out.println("Error: observers cannot make moves.");
        }
        ChessGame.TeamColor turn = game.getTeamTurn();
        if (turn.equals(ChessGame.TeamColor.WHITE)) {
            if (!client.playerColor.equals("WHITE")) {
                System.out.println("Error: not your turn.");
                return "";
            }
        }
        if (turn.equals(ChessGame.TeamColor.BLACK)) {
            if (!client.playerColor.equals("BLACK")) {
                System.out.println("Error: not your turn.");
                return "";
            }
        }

        ChessPosition startPosition = null;
        ChessPosition endPosition = null;
        if (client.playerColor.equals("WHITE")) {
            String startPos = params[0];
            String[] startPositions = startPos.split("");
            // convert startPositions[1] to integer
            startPositions = convertToIntegerWhite(startPositions);

            String endPos = params[1];
            String[] endPositions = endPos.split("");
            // convert endPositions[1] to integer
            endPositions = convertToIntegerWhite(endPositions);
            startPosition = new ChessPosition(Integer.parseInt(startPositions[0]), Integer.parseInt(startPositions[1]));
            endPosition = new ChessPosition(Integer.parseInt(endPositions[0]), Integer.parseInt(endPositions[1]));
        }
        if (client.playerColor.equals("BLACK")) {
            String startPos = params[0];
            String[] startPositions = startPos.split("");
            // convert startPositions[1] to integer (opposite indexing)
            startPositions = convertToIntegerBlack(startPositions);
            String endPos = params[1];
            String[] endPositions = endPos.split("");
            // convert endPositions[1] to integer (opposite indexing)
            endPositions = convertToIntegerBlack(endPositions);
            startPosition = new ChessPosition(Integer.parseInt(startPositions[0]), Integer.parseInt(startPositions[1]));
            endPosition = new ChessPosition(Integer.parseInt(endPositions[0]), Integer.parseInt(endPositions[1]));
        }

        if (game.board.getPiece(startPosition) == null) {
            System.out.println("Error: there is not a piece in that spot.");
            return "";
        }
        if (game.board.getPiece(startPosition) != null) {
            ChessPiece piece = game.board.getPiece(startPosition);
            ChessGame.TeamColor color = piece.getTeamColor();
            if (color.equals(ChessGame.TeamColor.WHITE) && client.playerColor.equals("BLACK")) {
                System.out.println("Error: that piece is not yours to move.");
                return "";
            }
            if (color.equals(ChessGame.TeamColor.BLACK) && client.playerColor.equals("WHITE")) {
                System.out.println("Error: that piece is not yours to move.");
                return "";
            }
            Collection<ChessMove> possibleMoves = game.validMoves(startPosition);
            Collection<ChessPosition> possibleSpaces = new ArrayList<>();
            for (ChessMove move: possibleMoves) {
                possibleSpaces.add(move.getEndPosition());
            }
            // make the move!
            if (possibleSpaces.contains(endPosition)) {
                try {
                    // TODO: implement pawn promotion logic too
                    game.makeMove(new ChessMove(startPosition, endPosition, null));
                    server.updateGame(new UpdateGameRequest(client.authToken, game, gameID, client.username, client.playerColor));
                    webSocketFacade.makeMoveClient(client.authToken, gameID, new ChessMove(startPosition, endPosition, null));
                    return "";
                    // TODO: how to send the move back to the server?
                } catch (Exception e) {
                    System.out.println("Error: invalid move.");
                    return "";
                }
            }
        }
        return "";
    }

    public String exitGame() {
        client.switchState("LOGGEDIN");
        String formatted = String.format("You have successfully exited the game.");
        System.out.println(formatted);
        try {
            webSocketFacade.leaveGameClient(client.authToken, gameID);
            webSocketFacade = null;
            server.updateGame(new UpdateGameRequest(client.authToken, game, gameID, null, client.playerColor));
        } catch (Exception e) {
//            webSocketFacade.throwErrorClient(e.getMessage());
            System.out.println("I am in the exitgame function for ingame client.");
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