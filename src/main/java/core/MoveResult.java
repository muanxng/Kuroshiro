package core;

/**
 * Encapsulates the complete outcome of an attempted action (move, shoot, or stab)
 * within the Kuroshiro game engine.
 * This immutable class provides the UI and engine with precise details on whether the
 * action was successful, what piece (if any) was captured, and if the action triggered
 * a game-ending condition.
 */
public class MoveResult {

    /**
     * Represents the specific state or error code resulting from an action attempt.
     */
    public enum Status {
        /** The action was validated and executed successfully. */
        SUCCESS,

        /** The action violated the acting piece's movement or targeting rules. */
        INVALID_MOVE,

        /** No valid piece was found at the specified starting coordinate. */
        NO_PIECE_SELECTED,

        /** An attempt was made to manipulate a piece belonging to the inactive player. */
        WRONG_TURN,

        /** The action was rejected because the match has already concluded. */
        GAME_OVER
    }

    private final Status status;
    private final Piece capturedPiece;
    private final boolean isWon;

    /**
     * Internal constructor for creating a move result.
     * Use static factory methods {@link #success} and {@link #failure} for instantiation.
     *
     * @param status the outcome status of the action
     * @param capturedPiece the piece captured or damaged during the action, or {@code null} if none
     * @param isWon {@code true} if this action triggered a victory
     */
    private MoveResult(Status status, Piece capturedPiece, boolean isWon) {
        this.status = status;
        this.capturedPiece = capturedPiece;
        this.isWon = isWon;
    }

    /**
     * Static factory method to generate a successful move outcome.
     *
     * @param captured the {@link Piece} captured/damaged during the move, or {@code null} if no interaction occurred
     * @param won {@code true} if the move triggered the final win condition
     * @return a {@code MoveResult} representing a successful action
     */
    public static MoveResult success(Piece captured, boolean won) {
        return new MoveResult(Status.SUCCESS, captured, won);
    }

    /**
     * Static factory method to generate a failed move outcome with a specific error code.
     *
     * @param status the reason for the failure (e.g., {@link Status#INVALID_MOVE})
     * @return a {@code MoveResult} representing a rejected action
     */
    public static MoveResult failure(Status status) {
        return new MoveResult(status, null, false);
    }

    /**
     * Retrieves the execution status of the attempted action.
     *
     * @return the relevant {@link Status} enum
     */
    public Status getStatus()       { return status; }

    /**
     * Retrieves the piece that was affected (captured or damaged) during this action.
     *
     * @return the affected {@link Piece}, or {@code null} if the action targeted an empty square
     */
    public Piece getCapturedPiece() { return capturedPiece; }

    /**
     * Convenience method to check if the action was executed successfully.
     *
     * @return {@code true} if the status is {@link Status#SUCCESS}, {@code false} otherwise
     */
    public boolean isSuccess()      { return status == Status.SUCCESS; }

    /**
     * Checks if this action resulted in an immediate victory.
     *
     * @return {@code true} if the action won the game, {@code false} otherwise
     */
    public boolean isWon()          { return isWon; }
}