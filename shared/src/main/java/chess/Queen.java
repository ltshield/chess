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

        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;

        Collection<ChessMove> options = toRightLeftUpDown(board, self, taken, position);
        Collection<ChessMove> otherOps = diagonals(board, self, taken, position);
        for (ChessMove mov : otherOps) {
            options.add(mov);
        }
        return options;
    }
}
