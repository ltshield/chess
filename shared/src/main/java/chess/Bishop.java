package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop extends PieceMoveLogic{
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public Bishop(ChessBoard board, ChessPosition position){
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

        return diagonals(board, self, position);
    }

}
