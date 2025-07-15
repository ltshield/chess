package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveLogic {

    public Collection<ChessMove> diagonals(ChessBoard board, ChessPiece piece, ChessPosition pos) {

        board.getPieces();

        Collection<ChessPosition> taken = board.positions;

        ChessPosition position = pos;
        ChessPiece self = piece;

        Collection<ChessMove> options = new ArrayList<>();
        int currRow = position.getRow();
        int currCol = position.getColumn();

        // Northeast
        for (int i = currRow+1, j = currCol+1; i <= 8 && j <= 8; i++, j++) {
            if (checkTaken(board, position, self, options, i, j)) {
                break;
            }
        }

        // Northwest
        for (int i = currRow-1, j = currCol+1; i >= 1 && j <= 8; i--, j++) {
            if (checkTaken(board, position, self, options, i, j)) {
                break;
            }
        }

        // Southeast
        for (int i = currRow+1, j = currCol-1; i <= 8 && j >= 1; i++, j--) {
            if (checkTaken(board, position, self, options, i, j)) {
                break;
            }
        }

        // Southwest
        for (int i = currRow-1, j = currCol-1; i >= 1 && j >= 1; i--, j--) {
            if (checkTaken(board, position, self, options, i, j)) {
                break;
            }
        }
        return options;
    }

    public boolean checkTaken(ChessBoard board, ChessPosition position, ChessPiece self, Collection<ChessMove> options, int i, int j) {
        ChessPosition tes = new ChessPosition(i, j);
        Collection<ChessPosition> taken = board.positions;
        if (taken.contains(tes)) {
            ChessPiece affil = board.getPiece(tes);
            if (affil.getTeamColor() == self.getTeamColor()) {
                return true;
            }
            options.add(new ChessMove(position, tes, null));
            return true;
        }
        options.add(new ChessMove(position, tes, null));
        return false;
    }

    public Collection<ChessMove> toRightLeftUpDown(ChessBoard board, ChessPiece piece, ChessPosition pos) {

        ChessPosition position = pos;
        ChessPiece self = piece;

        Collection<ChessMove> options = new ArrayList<>();

        board.getPieces();

        Collection<ChessPosition> taken = board.positions;

        int currRow = position.getRow();
        int currCol = position.getColumn();

        // Down
        for (int j = currCol-1; j >= 1; j--) {
            if (checkTaken(board, position, self, options, currRow, j)) {
                break;
            }
        }

        // Up
        for (int j = currCol+1; j <= 8; j++) {
            if (checkTaken(board, position, self, options, currRow, j)) {
                break;
            }
        }

        // To right
        for(int i = currRow+1; i <= 8; i++){
            if (checkTaken(board, position, piece, options, i, currCol)) {
                break;
            }
        }

        // To left
        for (int i = currRow-1; i >= 1; i--) {
            if (checkTaken(board, position, piece, options, i, currCol)) {
                break;
            }
        }
        return options;
    }
}
