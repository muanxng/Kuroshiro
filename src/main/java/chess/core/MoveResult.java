package chess.core;

public class MoveResult {

    public enum Status {
        SUCCESS, INVALID_MOVE, NO_PIECE_SELECTED, WRONG_TURN, KING_IN_CHECK, GAME_OVER
    }

    private final Status status;
    private final Piece capturedPiece;
    private final boolean isCheck;
    private final boolean isCheckmate;
    private final boolean isStalemate;

    public MoveResult(Status status, Piece capturedPiece,
                      boolean isCheck, boolean isCheckmate, boolean isStalemate) {
        this.status = status;
        this.capturedPiece = capturedPiece;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.isStalemate = isStalemate;
    }

    public static MoveResult success(Piece captured, boolean check, boolean checkmate, boolean stalemate) {
        return new MoveResult(Status.SUCCESS, captured, check, checkmate, stalemate);
    }

    public static MoveResult failure(Status status) {
        return new MoveResult(status, null, false, false, false);
    }

    public Status getStatus()       { return status; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public boolean isSuccess()      { return status == Status.SUCCESS; }
    public boolean isCheck()        { return isCheck; }
    public boolean isCheckmate()    { return isCheckmate; }
    public boolean isStalemate()    { return isStalemate; }
    public boolean isGameOver()     { return isCheckmate || isStalemate; }
}
