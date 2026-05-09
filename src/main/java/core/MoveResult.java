package core;

public class MoveResult {

    public enum Status {
        SUCCESS, INVALID_MOVE, NO_PIECE_SELECTED, WRONG_TURN, GAME_OVER
    }

    private final Status status;
    private final Piece capturedPiece;
    private final boolean isWon;

    public MoveResult(Status status, Piece capturedPiece, boolean isWon) {
        this.status = status;
        this.capturedPiece = capturedPiece;
        this.isWon = isWon;
    }

    public static MoveResult success(Piece captured, boolean won) {
        return new MoveResult(Status.SUCCESS, captured, won);
    }

    public static MoveResult failure(Status status) {
        return new MoveResult(status, null, false);
    }

    public Status getStatus()       { return status; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public boolean isSuccess()      { return status == Status.SUCCESS; }
    public boolean isWon()          { return isWon; }
}