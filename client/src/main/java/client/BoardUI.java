package client;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class BoardUI {

    public BoardUI(ChessGame thisGame) {
        game = thisGame;
    }
    private static ChessGame game;
    private static final String EMPTY = " ";
    public static String convertToChar(ChessPiece.PieceType type) {
        String stri = "";
        if (type== ChessPiece.PieceType.BISHOP) {
            stri = "B";
        }
        if (type== ChessPiece.PieceType.KING) {
            stri = "K";
        }
        if (type== ChessPiece.PieceType.QUEEN) {
            stri = "Q";
        }
        if (type== ChessPiece.PieceType.PAWN) {
            stri = "P";
        }
        if (type== ChessPiece.PieceType.ROOK) {
            stri = "R";
        }
        if (type== ChessPiece.PieceType.KNIGHT) {
            stri = "N";
        }
        return stri;
    }
    public static String convertToColor(ChessGame.TeamColor color) {
        if (color==WHITE) {
            return SET_TEXT_COLOR_BLUE;
        } else {
            return SET_TEXT_COLOR_RED;
        }
    }
    public static Collection<Character> defineAndPrintHeaderWhite() {
        Collection<Character> headerItems = new ArrayList<>();
        headerItems.add('a');
        headerItems.add('b');
        headerItems.add('c');
        headerItems.add('d');
        headerItems.add('e');
        headerItems.add('f');
        headerItems.add('g');
        headerItems.add('h');
        return headerItems;
    }

    public static Collection<Character> defineAndPrintHeaderBlack() {
        Collection<Character> headerItems = new ArrayList<>();
        headerItems.add('h');
        headerItems.add('g');
        headerItems.add('f');
        headerItems.add('e');
        headerItems.add('d');
        headerItems.add('c');
        headerItems.add('b');
        headerItems.add('a');
        return headerItems;
    }
    public static void printWhiteBoard(Collection<ChessPosition> toHighlight) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(RESET_TEXT_COLOR);

        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_MAGENTA);

        // HEADER
        Collection<Character> headerItems = defineAndPrintHeaderWhite();

        drawHeader(out, headerItems);

        // 8 x 8 grid, black on bottom left
        ChessBoard grid = game.board;
        for (int i=8; i >= 1; i--) {
            handleBoardDrawing(out, i, grid, toHighlight);
        }

        // FOOTER
        out.print(SET_BG_COLOR_MAGENTA);
        drawHeader(out, headerItems);
    }

    public static void handleBoardDrawing(PrintStream out, int i, ChessBoard grid, Collection<ChessPosition> toHighlight) {

        out.print(SET_BG_COLOR_MAGENTA);
        out.print(EMPTY);
        out.print(i);
        out.print(EMPTY);
        for (int j = 1; j <= 8; j++) {
            ChessPosition testPos = new ChessPosition(i, j);
            ChessPiece piece = grid.getPiece(new ChessPosition(i,j));
            if (j % 2 != 0 && i % 2 == 0) {
                if (toHighlight != null && toHighlight.contains(testPos)) {
                    out.print(SET_BG_COLOR_YELLOW);
                    printPieces(out, piece);
                } else {
                    out.print(SET_BG_COLOR_WHITE);
                    printPieces(out, piece);
                }
            }
            if (j % 2 == 0 && i % 2 == 0) {
                if (toHighlight != null && toHighlight.contains(testPos)) {
                    out.print(SET_BG_COLOR_YELLOW);
                    printPieces(out, piece);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                    printPieces(out, piece);
                }
            }
            if (j % 2 == 0 && i % 2 != 0) {
                if (toHighlight != null && toHighlight.contains(testPos)) {
                    out.print(SET_BG_COLOR_YELLOW);
                    printPieces(out, piece);
                } else {
                    out.print(SET_BG_COLOR_WHITE);
                    printPieces(out, piece);
                }
            }
            if (j % 2 != 0 && i % 2 != 0) {
                if (toHighlight != null && toHighlight.contains(testPos)) {
                    out.print(SET_BG_COLOR_YELLOW);
                    printPieces(out, piece);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                    printPieces(out, piece);
                }
            }
        }
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(EMPTY);
        out.print(RESET_TEXT_COLOR);
        out.print(i);
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.print("\n");
    }

    public static void printPieces(PrintStream out, ChessPiece piece) {
        if (piece != null) {
            ChessPiece.PieceType type = piece.getPieceType();
            ChessGame.TeamColor color = piece.getTeamColor();
            out.print(EMPTY);
            out.print(convertToColor(color));
            out.print(convertToChar(type));
            out.print(EMPTY);
        } else {
            out.print(EMPTY.repeat(3));
        }
    }

    public static void printBlackBoard(Collection<ChessPosition> toHighlight) {
        Collection<ChessPosition> highlightThese = toHighlight;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(RESET_TEXT_COLOR);

        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_MAGENTA);

        // HEADER
        Collection<Character> headerItems = defineAndPrintHeaderBlack();

        drawHeader(out, headerItems);

        // 8 x 8 grid, black on bottom left
        ChessBoard grid = game.board;
        for (int i=1; i <= 8; i++) {
            handleBoardDrawing(out, i, grid, highlightThese);
        }

        // FOOTER
        out.print(SET_BG_COLOR_MAGENTA);
        drawHeader(out, headerItems);
    }

    public static void drawHeader(PrintStream out, Collection<Character> headerItems) {
        out.print(EMPTY.repeat(3));
        for (char item : headerItems) {
            out.print(EMPTY);
            out.print(item);
            out.print(EMPTY);
        }
        out.print(EMPTY.repeat(3));

        out.print(RESET_BG_COLOR);
        out.print("\n");
    }

    public static void drawHighlightedBoard(String playerColor, ChessPosition position) {
        Collection<ChessMove> possibleMoves = game.validMoves(position);
        Collection<ChessPosition> possibleSpaces = new ArrayList<>();
        for (ChessMove move: possibleMoves) {
            possibleSpaces.add(move.getEndPosition());
        }
        drawBoard(playerColor, possibleSpaces);
    }

    public static void drawBoard(String playerColor, Collection<ChessPosition> toHighlight) {
        if (playerColor.equals("WHITE")) {
            printWhiteBoard(toHighlight);
        }
        if (playerColor.equals("BLACK")) {
            printBlackBoard(toHighlight);
        }
        if (playerColor.equals("OBSERVING")) {
            printWhiteBoard(toHighlight);
        }
    }
}
