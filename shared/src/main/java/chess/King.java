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

    public Collection<ChessMove> calculateMoves() {
        ChessPiece self = board.getPiece(position);

        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;

        Collection<ChessMove> options = new ArrayList<ChessMove>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        // Northeast
        if (currRow+1 <= 8 && currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow + 1, currCol + 1);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            } else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Northwest
        if (currRow+1 <= 8 && currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow + 1, currCol - 1);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            } else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Southwest
        if (currRow-1 >= 0 && currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow - 1, currCol - 1);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            } else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Southeast
        if (currRow-1 >= 1 && currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow - 1, currCol + 1);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            } else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Down
        if (currRow-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow-1, currCol);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            }
            else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Up
        if (currRow+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow+1, currCol);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            }
            else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Left
        if (currCol-1 >= 1) {
            ChessPosition query = new ChessPosition(currRow, currCol-1);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            }
            else {
                options.add(new ChessMove(position, query, null));
            }
        }

        // Right
        if (currCol+1 <= 8) {
            ChessPosition query = new ChessPosition(currRow, currCol+1);
            if (taken.contains(query)) {
                ChessPiece affil = board.getPiece(query);
                if (affil.getTeamColor() != self.getTeamColor()) {
                    options.add(new ChessMove(position, query, null));
                }
            }
            else {
                options.add(new ChessMove(position, query, null));
            }
        }

        return options;
    }
}
