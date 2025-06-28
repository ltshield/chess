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
        // get piece color
        ChessPiece self = board.getPiece(position);

        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;

        Collection<ChessMove> options = new ArrayList<ChessMove>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        for(int i = currRow+1, j = currCol+1; i <= 8 && j <= 8; i++, j++) {
            if (taken.contains(new ChessPosition(i, j))) {
                ChessPiece affil = board.getPiece(new ChessPosition(i, j));
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, new ChessPosition(i, j), null));
                break;
            }
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

        for(int i = currRow+1, j = currCol-1; i <= 8 && j >= 1; i++, j--) {
            if (taken.contains(new ChessPosition(i, j))) {
                ChessPiece affil = board.getPiece(new ChessPosition(i, j));
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, new ChessPosition(i, j), null));
                break;
            }
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

        for(int i = currRow-1, j = currCol+1; i >= 1 && j <= 8; i--, j++) {
            if (taken.contains(new ChessPosition(i, j))) {
                ChessPiece affil = board.getPiece(new ChessPosition(i, j));
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, new ChessPosition(i, j), null));
                break;
            }
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

        for(int i = currRow-1, j = currCol-1; i >= 1 && j >= 1; i--, j--) {
            if (taken.contains(new ChessPosition(i, j))) {
                ChessPiece affil = board.getPiece(new ChessPosition(i, j));
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, new ChessPosition(i, j), null));
                break;
            }
            options.add(new ChessMove(position, new ChessPosition(i, j), null));
        }

//        System.out.println("Returning options now.");
        return options;
    }

}
