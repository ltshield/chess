package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] grid = new ChessPiece[8+1][8+1];
    public Collection<ChessPosition> positions = new ArrayList<ChessPosition>();
    public ChessBoard() {
    }

    public void getPieces() {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                if (getPiece(pos) != null) {
                    positions.add(pos);
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
//        ChessPosition ne = new ChessPosition(8,8);
        grid[position.getRow()][position.getColumn()] = piece;
//        grid[ne.getRow()][ne.getColumn()] = piece;
//        System.out.println("Placed Piece on Board. " + piece.getPieceType());
//        System.out.println(piece)
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        // TODO: account for chessboards starting at (1,1) not (0,0)?
        return grid[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.equals(grid, that.grid) && Objects.equals(positions, that.positions);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(positions);
        result = 31 * result + Arrays.hashCode(grid);
        return result;
    }
}
