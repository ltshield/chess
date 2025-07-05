package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

//    private TeamColor starter_color = TeamColor.WHITE;
    private TeamColor turn;
    public ChessBoard board = new ChessBoard();
    public Collection<ChessPosition> whitePieces = new ArrayList<>();
    public Collection<ChessPosition> blackPieces = new ArrayList<>();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validOptions = new ArrayList<>();

        // if no piece at position, return null
        if (board.getPiece(startPosition) == null) {
            return null;
        };

        // if valid and would not put king in check or checkmate
        return validOptions;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // TODO: implement pawn logic for upgrading
        // perform the move
        // TODO: shouldn't moves that make it here always be valid??
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPos);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessPiece.PieceType promPiece = move.getPromotionPiece();
            piece = new ChessPiece(turn, promPiece);
        }
//        if (board.getPiece(endPos).getTeamColor() != turn)
        board.grid[endPos.getRow()][endPos.getColumn()] = piece;
        board.grid[startPos.getRow()][startPos.getColumn()] = null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> oppMoves = board.getMovesFromOpponent(teamColor);
        ChessPosition kingPosition = board.findKingPosition(teamColor);
        for (ChessMove move : oppMoves) {
            ChessPosition endPos = move.getEndPosition();
            if (kingPosition == endPos) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not yet implemented.");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.deepEquals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
