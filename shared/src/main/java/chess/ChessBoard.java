package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;
import static java.util.Arrays.deepToString;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ChessPiece[][] grid = new ChessPiece[8+1][8+1];
    public Collection<ChessPosition> positions = new ArrayList<>();

    public ChessBoard() {
    }

    public Collection<ChessPosition> getColorPositions(ChessGame.TeamColor color) {
        Collection<ChessPosition> colorPos = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                if (getPiece(pos) != null && !colorPos.contains(pos)) {
                    if (getPiece(pos).getTeamColor() == color) {
                        colorPos.add(pos);
                    }
                }
            }
        }
        return colorPos;
    }

    public ChessPosition findKingPosition(ChessGame.TeamColor color, ChessBoard board) {
        board.getPieces();
        Collection<ChessPosition> posits = board.positions;
        for (ChessPosition pos : posits) {
            ChessPiece piece = getPiece(pos);
            if (piece.getPieceType() == KING && piece.getTeamColor() == color) {
                return pos;
            }
        }
        throw new RuntimeException("No king?");
    }

    public Collection<ChessMove> getMovesFromOpponent(ChessGame.TeamColor color) {
        Collection<ChessMove> allOppMoves = new ArrayList<>();
        if (color == WHITE) {
            Collection<ChessPosition> oppPos = getColorPositions(BLACK);
            for (ChessPosition pos : oppPos) {
                ChessPiece piece = getPiece(pos);
                // TODO: will this only pass in valid moves??
                Collection<ChessMove> validMovesForPiece = piece.pieceMoves(this, pos);
                for (ChessMove move : validMovesForPiece) {
                    allOppMoves.add(move);
                }
            }
            return allOppMoves;
        }
        if (color == BLACK) {
            Collection<ChessPosition> oppPos = getColorPositions(WHITE);
            for (ChessPosition pos : oppPos) {
                ChessPiece piece = getPiece(pos);
                Collection<ChessMove> validMovesForPiece = piece.pieceMoves(this, pos);
                for (ChessMove move : validMovesForPiece) {
                    allOppMoves.add(move);
                }
            }
            return allOppMoves;
        } else {
            throw new RuntimeException("Not a valid color.");
        }
    }

    public void getPieces() {
        // Note: you have GOT TO re-initialize this array each time
        // otherwise stuff will carry over
        positions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                if (getPiece(pos) != null && !positions.contains(pos)) {
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
        grid[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        // white rooks
        Collection<ChessPosition> positions = new ArrayList<>();
        positions.add(new ChessPosition(1,1));
        positions.add(new ChessPosition(1,8));
        addPieces(ChessPiece.PieceType.ROOK, positions, WHITE);

        // black rooks
        positions = new ArrayList<>();
        positions.add(new ChessPosition(8,8));
        positions.add(new ChessPosition(8,1));
        addPieces(ChessPiece.PieceType.ROOK, positions, BLACK);

        // white knights
        positions = new ArrayList<>();
        positions.add(new ChessPosition(1,2));
        positions.add(new ChessPosition(1,7));
        addPieces(ChessPiece.PieceType.KNIGHT, positions, WHITE);

        // black knights
        positions = new ArrayList<>();
        positions.add(new ChessPosition(8,7));
        positions.add(new ChessPosition(8,2));
        addPieces(ChessPiece.PieceType.KNIGHT, positions, BLACK);

        // black bishops
        positions = new ArrayList<>();
        positions.add(new ChessPosition(8,6));
        positions.add(new ChessPosition(8,3));
        addPieces(ChessPiece.PieceType.BISHOP, positions, BLACK);

        // white bishops
        positions = new ArrayList<>();
        positions.add(new ChessPosition(1,3));
        positions.add(new ChessPosition(1,6));
        addPieces(ChessPiece.PieceType.BISHOP, positions, WHITE);

        // white pawns
        positions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            positions.add(new ChessPosition(2,i));
        }
        addPieces(ChessPiece.PieceType.PAWN, positions, WHITE);

        // black pawns
        positions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            positions.add(new ChessPosition(7,i));
        }
        addPieces(ChessPiece.PieceType.PAWN, positions, BLACK);

        // queens
        positions = new ArrayList<>();
        positions.add(new ChessPosition(1,4));
        addPieces(ChessPiece.PieceType.QUEEN, positions, WHITE);

        positions = new ArrayList<>();
        positions.add(new ChessPosition(8,4));
        addPieces(ChessPiece.PieceType.QUEEN, positions, BLACK);

        // kings
        positions = new ArrayList<>();
        positions.add(new ChessPosition(1,5));
        addPieces(KING, positions, WHITE);

        positions = new ArrayList<>();
        positions.add(new ChessPosition(8,5));
        addPieces(KING, positions, BLACK);
    }

    public void addPieces(ChessPiece.PieceType type, Collection<ChessPosition> positions, ChessGame.TeamColor color) {
        for(ChessPosition pos : positions) {
            ChessPiece piece = new ChessPiece(color, type);
            addPiece(pos, piece);
        }
    }

    public String toString() {
        return deepToString(grid);
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
        return Arrays.deepEquals(grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
