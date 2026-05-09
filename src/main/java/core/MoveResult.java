package core;

/**
 * Encapsulates the outcome of an attempted move or action within the game.
 * This class provides details on whether the move was successful, if any piece
 * was captured, and whether the move resulted in a game-winning state.
 */
public class MoveResult {

    /**
     * Represents the specific status or reason for a move's outcome.
     */
    public enum Status {
        /** The move was executed successfully. */
        SUCCESS,

        /** The move is not allowed by the piece's movement rules. */
        INVALID_MOVE,

        /** No piece was found at the starting position. */
        NO_PIECE_SELECTED,

        /** An attempt was made to move a piece belonging to the opponent. */
        WRONG_TURN,

        /** The action cannot be performed because the game has already ended. */
        GAME_OVER
    }

    private final Status status;
    private final Piece capturedPiece;
    private final boolean isWon;

    /**
     * Constructs a new MoveResult with the specified details.
     *
     * @param status the outcome status of the move
     * @param capturedPiece the piece that was captured, or null if none
     * @param isWon true if this move won the game, false otherwise
     */
    public MoveResult(Status status, Piece capturedPiece, boolean isWon) {
        this.status = status;
        this.capturedPiece = capturedPiece;
        this.isWon = isWon;
    }

    /**
     * Creates a successful MoveResult.
     *
     * @param captured the piece captured during the move, or null if no capture occurred
     * @param won true if the move triggered a win condition
     * @return a MoveResult representing a successful action
     */
    public static MoveResult success(Piece captured, boolean won) {
        return new MoveResult(Status.SUCCESS, captured, won);
    }

    /**
     * Creates a failed MoveResult with a specific error status.
     *
     * @param status the reason for the failure (e.g., INVALID_MOVE, WRONG_TURN)
     * @return a MoveResult representing a failed action
     */
    public static MoveResult failure(Status status) {
        return new MoveResult(status, null, false);
    }

    /**
     * Retrieves the status of the move.
     *
     * @return the move's status enum
     */
    public Status getStatus()       { return status; }

    /**
     * Retrieves the piece that was captured during this move, if any.
     *
     * @return the captured Piece, or null if no piece was captured
     */
    public Piece getCapturedPiece() { return capturedPiece; }

    /**
     * Checks if the move was successful.
     *
     * @return true if the status is SUCCESS, false otherwise
     */
    public boolean isSuccess()      { return status == Status.SUCCESS; }

    /**
     * Checks if this move resulted in winning the game.
     *
     * @return true if the move won the game, false otherwise
     */
    public boolean isWon()          { return isWon; }
}