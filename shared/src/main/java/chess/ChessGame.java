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
    private TeamColor turn = TeamColor.WHITE;
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
//        Collection<ChessMove> validOptions = new ArrayList<>();

        // if no piece at position, return null
        if (board.getPiece(startPosition) == null) {
            return null;
        };

        // if valid and would not put king in check or checkmate
        // therefore, king cannot move into danger AND other pieces cannot move to where king would be in danger
        Collection<ChessMove> validOptions = board.getPiece(startPosition).pieceMoves(board, startPosition);
        // king cannot move into danger
        if (board.getPiece(startPosition).getPieceType() == ChessPiece.PieceType.KING) {
            Collection<ChessMove> oppMoves = board.getMovesFromOpponent(board.getPiece(startPosition).getTeamColor());
            Collection<ChessMove> toDelete = new ArrayList<>();
            for (ChessMove move : validOptions) {
                ChessPosition finalPos = move.getEndPosition();
                for (ChessMove enemyMove : oppMoves) {
                    ChessPosition finalEnemyPos = enemyMove.getEndPosition();
                    if (finalPos.equals(finalEnemyPos)) {
                        if (!toDelete.contains(enemyMove)) {
                            toDelete.add(move);
                        }
                    }
                }
            }
            for (ChessMove move : toDelete) {
                validOptions.remove(move);
            }
            return validOptions;
        }

        // if other piece is in danger, and king would be in danger if it were to move, cannot move
        ChessPiece query = board.getPiece(startPosition);
        if (query.getTeamColor() == TeamColor.WHITE) {
            for (ChessMove enemyMove : board.getMovesFromOpponent(TeamColor.WHITE)) {
                if (enemyMove.getEndPosition().equals(startPosition)) {
                    ChessBoard newBoard = new ChessBoard();
                    for (int i=1; i<= 8; i++) {
                        for (int j=1; j<=8; j++) {
                            if (board.getPiece(new ChessPosition(i,j)) != null) {
                                newBoard.grid[i][j] = new ChessPiece(board.getPiece(new ChessPosition(i,j)).getTeamColor(), board.getPiece(new ChessPosition(i,j)).getPieceType());
                            } else {
                                newBoard.grid[i][j] = null;
                            }
                        }
                    }
                    newBoard.grid[startPosition.getRow()][startPosition.getColumn()] = null;
                    Collection<ChessMove> oppMoves = newBoard.getMovesFromOpponent(TeamColor.WHITE);
                    ChessPosition kingPosition = newBoard.findKingPosition(TeamColor.WHITE);
//                    Collection<ChessMove> toDelete = new ArrayList<>();
                    for (ChessMove move : oppMoves) {
                        ChessPosition endPos = move.getEndPosition();
                        if (kingPosition.equals(endPos)) {
                            validOptions = new ArrayList<>();
                            return validOptions;
                        }
                    }
                }
            }
        } else {
            for (ChessMove enemyMove : board.getMovesFromOpponent(TeamColor.BLACK)) {
                if (enemyMove.getEndPosition().equals(startPosition)) {
                    ChessBoard newBoard = new ChessBoard();
                    for (int i=1; i<= 8; i++) {
                        for (int j=1; j<=8; j++) {
                            if (board.getPiece(new ChessPosition(i,j)) != null) {
                                newBoard.grid[i][j] = new ChessPiece(board.getPiece(new ChessPosition(i,j)).getTeamColor(), board.getPiece(new ChessPosition(i,j)).getPieceType());
                            } else {
                                newBoard.grid[i][j] = null;
                            }
                        }
                    }
                    newBoard.grid[startPosition.getRow()][startPosition.getColumn()] = null;
                    Collection<ChessMove> oppMoves = newBoard.getMovesFromOpponent(TeamColor.BLACK);
                    ChessPosition kingPosition = newBoard.findKingPosition(TeamColor.BLACK);
                    for (ChessMove move : oppMoves) {
                        ChessPosition endPos = move.getEndPosition();
                        if (kingPosition.equals(endPos)) {
                            validOptions = new ArrayList<>();
                            return validOptions;
                        }
                    }
                }
            }
        }
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
        // if in check and cannot move king
        if (isInCheck(teamColor)) {
            ChessPosition kingPos = board.findKingPosition(teamColor);
            // cannot move king
            Collection<ChessMove> kingMoves = board.getPiece(kingPos).pieceMoves(board, kingPos);
            if (kingMoves.isEmpty()) {
                return true;
            }
            // can move king to escape check

            // can move other pieces in between king and opp

            // can move other piece to capture opp piece
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // gonna reuse the opponent moves function
        TeamColor oppColor = null;
        if (teamColor == TeamColor.WHITE) {
            oppColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK) {
            oppColor = TeamColor.WHITE;
        }
        if (oppColor != null) {
            Collection<ChessMove> myMoves = board.getMovesFromOpponent(oppColor);
            if (myMoves.isEmpty() && !isInCheck(teamColor)) {
                return true;
            } else {
                return false;
            }
        }
        throw new RuntimeException("Not a valid team color.");
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
