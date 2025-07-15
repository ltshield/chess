package chess;
import java.util.ArrayList;
import java.util.Collection;
public class King {
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public King(ChessBoard board, ChessPosition position){
        super();
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
    }

    public void checkIfInTaken(ChessPiece self, ChessBoard board, ChessPosition query, Collection<ChessMove> options) {
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
//        return options;
    }

    public Collection<ChessMove> calculateMoves() {
        ChessPiece self = board.getPiece(position);

        Collection<ChessMove> options = new ArrayList<ChessMove>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        // Northeast
        if (currRow+1 <= 8 && currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow + 1, currCol + 1);
            checkIfInTaken(self, board, query, options);
        }

        // Northwest
        if (currRow+1 <= 8 && currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow + 1, currCol - 1);
            checkIfInTaken(self, board, query, options);
        }

        // Southwest
        if (currRow-1 >= 0 && currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow - 1, currCol - 1);
            checkIfInTaken(self, board, query, options);
        }

        // Southeast
        if (currRow-1 >= 1 && currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow - 1, currCol + 1);
            checkIfInTaken(self, board, query, options);
        }

        // Down
        if (currRow-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow-1, currCol);
            checkIfInTaken(self, board, query, options);
        }

        // Up
        if (currRow+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow+1, currCol);
            checkIfInTaken(self, board, query, options);
        }

        // Left
        if (currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow, currCol-1);
            checkIfInTaken(self, board, query, options);
        }

        // Right
        if (currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow, currCol+1);
            checkIfInTaken(self, board, query, options);
        }

        return options;
    }
}
