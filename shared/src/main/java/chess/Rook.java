package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook extends PieceMoveLogic{
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public Rook(ChessBoard board, ChessPosition position) {
        super();
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
    }

    public Collection<ChessMove> calculateMoves() {
        ChessPiece self = board.getPiece(position);

        return toRightLeftUpDown(board, self, position);
    }
}
