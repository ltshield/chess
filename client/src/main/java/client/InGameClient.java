package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import service.*;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;
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
                - move <Start Position> <End Position> <PawnPromotion>: performs move if valid
                - resign: ends chess game
                - leave: leaves the game
                - highlight <row><column>: highlights legal moves of piece at position <row><column>
                - quit
                """;
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame();
        }
    }

    public void displayNotification(String message) {
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.println(message);
        System.out.print(RESET_TEXT_COLOR +"\n" + "[" + client.state  + "] >>> " + SET_TEXT_COLOR_BLUE);
    }

    public void displayError(String message) {
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(String.format("%s", message));
        System.out.print(RESET_TEXT_COLOR +"\n" + "[" + client.state  + "] >>> " + SET_TEXT_COLOR_BLUE);
    }

    public void loadGame() {
        System.out.println("\n");
        drawBoard();
        System.out.print(RESET_TEXT_COLOR +"\n" + "[" + client.state  + "] >>> " + SET_TEXT_COLOR_BLUE);
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
        try {
            System.out.println("Are you sure that you want to resign? [y/n]");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.contains("y")) {
                webSocketFacade.resignGameClient(client.authToken, gameID);
                return "";
            }
            if (line.contains("n")) {
                return "";
            }
            else {
                System.out.println("Did not understand that input.\n");
                resignGame();
            }
            return "";
        }
        catch (Exception e) {
            System.out.println("Something went wrong with resigning.");
            return "";
        }
    }

    public String highlightPossibilities(String... params) {
        if (params.length >= 1) {
            String position = params[0];
            String[] positions = position.split("");
            ChessPosition pos = null;
            if (client.playerColor.equals("WHITE")) {
                positions = convertToInteger(positions);
            }
            if (client.playerColor.equals("BLACK")) {
                positions = convertToInteger(positions);
            }
            if (client.playerColor.equals("OBSERVING")) {
                positions = convertToInteger(positions);
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
            board.drawHighlightedBoardBoth(client.playerColor, pos);
        } else {
            System.out.println("Expected highlight <position>.");
        }
        return "";
    }

    public String[] convertToInteger(String[] strings) {
        Collection<String> letters = new ArrayList<>();
        letters.add("a");
        letters.add("b");
        letters.add("c");
        letters.add("d");
        letters.add("e");
        letters.add("f");
        letters.add("g");
        letters.add("h");
        if (letters.contains(strings[0])) {
            if (strings[0].equals("a")) {
                strings[0] = String.valueOf(1);
            }
            if (strings[0].equals("b")) {
                strings[0] = String.valueOf(2);
            }
            if (strings[0].equals("c")) {
                strings[0] = String.valueOf(3);
            }
            if (strings[0].equals("d")) {
                strings[0] = String.valueOf(4);
            }
            if (strings[0].equals("e")) {
                strings[0] = String.valueOf(5);
            }
            if (strings[0].equals("f")) {
                strings[0] = String.valueOf(6);
            }
            if (strings[0].equals("g")) {
                strings[0] = String.valueOf(7);
            }
            if (strings[0].equals("h")) {
                strings[0] = String.valueOf(8);
            }
        }
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

    public ChessPiece.PieceType handlePromotionLogic(String input) {
        if (input.equalsIgnoreCase("QUEEN")) {
            return ChessPiece.PieceType.QUEEN;
        }
        if (input.equalsIgnoreCase("BISHOP")) {
            return ChessPiece.PieceType.BISHOP;
        }
        if (input.equalsIgnoreCase("KNIGHT")) {
            return ChessPiece.PieceType.KNIGHT;
        }
        if (input.equalsIgnoreCase("ROOK")) {
            return ChessPiece.PieceType.ROOK;
        }
        return null;
    }
    public String performMove(String... params) {
        if (params[0].length() < 2 || params[1].length() < 2) {
            System.out.println("Sorry, you did not provide a valid move.");
            return "";
        }
        if (game.resigned) {
            System.out.println("Sorry, game has been resigned. No more moves allowed.");
            return "";
        }

        ChessPosition startPosition = null;
        ChessPosition endPosition = null;
        ChessPiece.PieceType promotionPiece = null;
        if (client.playerColor.equals("WHITE")) {
            String startPos = params[0];
            String[] startPositions = startPos.split("");
            // convert startPositions[1] to integer
            startPositions = convertToInteger(startPositions);
            String endPos = params[1];
            String[] endPositions = endPos.split("");
            // convert endPositions[1] to integer
            endPositions = convertToInteger(endPositions);
            startPosition = new ChessPosition(Integer.parseInt(startPositions[0]), Integer.parseInt(startPositions[1]));
            endPosition = new ChessPosition(Integer.parseInt(endPositions[0]), Integer.parseInt(endPositions[1]));
            if (endPosition.getRow() == 8 && game.board.getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                String promPiece = params[2];
                promotionPiece = handlePromotionLogic(promPiece);
            }
        }
        if (client.playerColor.equals("BLACK")) {
            String startPos = params[0];
            String[] startPositions = startPos.split("");
            // convert startPositions[1] to integer (opposite indexing)
            startPositions = convertToInteger(startPositions);
            String endPos = params[1];
            String[] endPositions = endPos.split("");
            // convert endPositions[1] to integer (opposite indexing)
            endPositions = convertToInteger(endPositions);
            startPosition = new ChessPosition(Integer.parseInt(startPositions[0]), Integer.parseInt(startPositions[1]));
            endPosition = new ChessPosition(Integer.parseInt(endPositions[0]), Integer.parseInt(endPositions[1]));
            if (endPosition.getRow() == 1 && game.board.getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                String promPiece = params[2];
                promotionPiece = handlePromotionLogic(promPiece);
            }
        }
        try {
            // implement pawn promotion logic too?
            webSocketFacade.makeMoveClient(client.authToken, gameID, new ChessMove(startPosition, endPosition, promotionPiece));
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    // TODO: notify when in check
    // TODO: black highlighting
    // TODO: resigning has check before

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
//            System.out.println("I am in the exitgame function for ingame client.");
        }
        return client.eval("list");
    }

    public void refreshGame(int gameID) {
        try {
            JoinGameResponse resp = server.joinGame(new JoinGameRequest(client.authToken, client.playerColor, gameID));
            this.game = resp.game();
//            System.out.println("Refreshed game!");
        } catch (Exception e) {
//            System.out.println("Error refreshing game.");
        }
    }

    public String drawBoard() {
        refreshGame(gameID);
        BoardUI board = new BoardUI(game);
        board.drawBoard(client.playerColor, null);
        return "";
    }

    // highlight for black not working
    // observe bugs
    // gameboard edits result in edits to all games?
}