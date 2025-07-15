package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight {

    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public Knight(ChessBoard board, ChessPosition position) {
        super();
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
    }

    public Collection<ChessMove> checkIfInTaken(ChessPiece self, ChessBoard board, ChessPosition query, Collection<ChessMove> options) {
        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;

        if (taken.contains(query)) {
            ChessPiece affil = board.getPiece(query);
            if (affil.getTeamColor() != self.getTeamColor()) {
                options.add(new ChessMove(position, query, null));
            }
        } else {
            options.add(new ChessMove(position, query, null));
        }
        return options;
    }

    public Collection<ChessMove> calculateMoves() {

        ChessPiece self = board.getPiece(position);

        Collection<ChessMove> options = new ArrayList<ChessMove>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        // up 2 left 1
        if (currRow+2 <= 8 && currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow+2, currCol-1);
            checkIfInTaken(self, board, query, options);
        }

        // up 2 right 1
        if (currRow+2 <= 8 && currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow+2, currCol+1);
            checkIfInTaken(self, board, query, options);
        }

        // down 2 right 1
        if (currRow-2 >= 1 && currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow-2, currCol+1);
            checkIfInTaken(self, board, query, options);
        }

        // down 2 left 1
        if (currRow-2 >= 1 && currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow-2, currCol-1);
            checkIfInTaken(self, board, query, options);
        }

        // left 2 down 1
        if (currRow-1 >= 1 && currCol-2 >= 1) {
            ChessPosition query = new ChessPosition(currRow-1, currCol-2);
            checkIfInTaken(self, board, query, options);
        }

        // left 2 up 1
        if (currRow+1 <= 8 && currCol-2 >= 1) {
            ChessPosition query = new ChessPosition(currRow+1, currCol-2);
            checkIfInTaken(self, board, query, options);
        }

        // right 2 up 1
        if (currRow+1 <= 8 && currCol+2 <= 8) {
            ChessPosition query = new ChessPosition(currRow+1, currCol+2);
            checkIfInTaken(self, board, query, options);
        }

        // right 2 down 1
        if (currRow-1 >= 1 && currCol+2 <= 8) {
            ChessPosition query = new ChessPosition(currRow-1, currCol+2);
            checkIfInTaken(self, board, query, options);
        }

        return options;
    }
}
