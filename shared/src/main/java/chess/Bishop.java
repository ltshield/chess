package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop{
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public Bishop(ChessBoard board, ChessPosition position) {
        super();
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
    }

    public Collection<ChessMove> calculateMoves() {
//        ArrayList<String> taken;
//        board.getPieces();
//        taken = board.pieces;
//        System.out.println("Starting now.");
        Collection<ChessMove> options = new ArrayList<ChessMove>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        for(int i = currRow+1, j = currCol+1; i <= 8 && j <= 8; i++, j++) {
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
            }

        for(int i = currRow-1, j = currCol-1; i > 0 && j > 0; i--, j--) {
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

        for(int i = currRow+1, j = currCol-1; i <= 8 && j > 0; i++, j--) {
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

        for(int i = currRow-1, j = currCol+1; i > 0 && j <= 8; i--, j++) {
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

//        System.out.println("Returning options now.");
        return options;
    }

}
