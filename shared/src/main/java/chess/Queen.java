package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen extends PieceMoveLogic{
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

        Collection<ChessMove> options = toRightLeftUpDown(board, self, position);
        Collection<ChessMove> otherOps = diagonals(board, self, position);
        for (ChessMove mov : otherOps) {
            options.add(mov);
        }
        return options;
    }
}
