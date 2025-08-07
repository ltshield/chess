package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        new Repl(serverUrl).run();
//        ChessGame game = new ChessGame();
//        try {
//            game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
//        } catch (Exception e) {
//            System.out.println("Whoops.");
//        }
//        BoardUI boardUI = new BoardUI(game);
//        boardUI.drawBoard("WHITE", null);
//        boardUI.drawBoard("BLACK", null);
    }
}