package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen {
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public Queen(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
    }

    public Collection<ChessMove> calculateMoves() {
        ChessPiece self = board.getPiece(position);

        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;

        Collection<ChessMove> options = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        // To right
        for(int i = currRow+1; i <= 8; i++){
            ChessPosition tes = new ChessPosition(i,currCol);
            if(taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // To left
        for (int i = currRow-1; i >= 1; i--) {
            ChessPosition tes = new ChessPosition(i, currCol);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // Down
        for (int j = currCol-1; j >= 1; j--) {
            ChessPosition tes = new ChessPosition(currRow, j);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // Up
        for (int j = currCol+1; j <= 8; j++) {
            ChessPosition tes = new ChessPosition(currRow, j);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // Northeast
        for (int i = currRow+1, j = currCol+1; i <= 8 && j <= 8; i++, j++) {
            ChessPosition tes = new ChessPosition(i, j);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // Northwest
        for (int i = currRow-1, j = currCol+1; i >= 1 && j <= 8; i--, j++) {
            ChessPosition tes = new ChessPosition(i, j);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // Southeast
        for (int i = currRow+1, j = currCol-1; i <= 8 && j >= 1; i++, j--) {
            ChessPosition tes = new ChessPosition(i, j);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }

        // Southwest
        for (int i = currRow-1, j = currCol-1; i >= 1 && j >= 1; i--, j--) {
            ChessPosition tes = new ChessPosition(i, j);
            if (taken.contains(tes)) {
                ChessPiece affil = board.getPiece(tes);
                if (affil.getTeamColor() == self.getTeamColor()) {
                    break;
                }
                options.add(new ChessMove(position, tes, null));
                break;
            }
            options.add(new ChessMove(position, tes, null));
        }
        return options;
    }
}
