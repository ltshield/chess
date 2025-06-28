package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece.PieceType type;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position, ChessPiece.PieceType type) {
        this.board = board;
        this.position = position;
        this.type = type;
    }

    public Collection<ChessMove> calculateMoves() {
//        if (type == ChessPiece.PieceType.BISHOP) {
//            Bishop bishop = new Bishop(board, position);
//            return bishop.calculateMoves();
//        }
//        if (type == ChessPiece.PieceType.ROOK) {
//            Rook rook = new Rook(board, position);
//            return rook.calculateMoves();
//        }
//        if (type == ChessPiece.PieceType.QUEEN) {
//            Queen queen = new Queen(board, position);
//            return queen.calculateMoves();
//        }
//        if (type == ChessPiece.PieceType.KING) {
            King king = new King(board, position);
            return king.calculateMoves();
//        }
    }
}