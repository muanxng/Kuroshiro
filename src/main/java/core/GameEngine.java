package core;

import pieces.Archmage;
import pieces.Warrior;

import java.util.List;

public class GameEngine {

    private final Board board;
    private Color currentTurn;
    private boolean gameOver;
    private Color winner;
    private int totalMoves;

    public GameEngine(Board board) {
        this.board = board;
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        this.winner = null;
        this.totalMoves = 0;
    }

    public MoveResult makeMove(Position from, Position to) {
        if (gameOver) return MoveResult.failure(MoveResult.Status.GAME_OVER);

        Piece piece = board.getPieceAt(from);
        Piece target = board.getPieceAt(to);

        if (piece == null) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        List<Position> legalMoves = getSafeMoves(piece);
        if (!legalMoves.contains(to)) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

        Piece captured = board.movePiece(piece, to);

        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    public MoveResult stab(Position shooterPos, Position target) {

        if (gameOver) {
            return MoveResult.failure(MoveResult.Status.GAME_OVER);
        }

        Piece piece = board.getPieceAt(shooterPos);

        if (!(piece instanceof Stabbable stabbable)) {
            return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        }

        if (piece.getColor() != currentTurn) {
            return MoveResult.failure(MoveResult.Status.WRONG_TURN);
        }

        Piece captured = stabbable.stab(target, board);

        if (captured == null) {
            return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
        }

        board.movePiece(piece, target);
        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    public MoveResult shoot(Position shooterPos, Position target) {

        if (gameOver) {
            return MoveResult.failure(MoveResult.Status.GAME_OVER);
        }

        Piece piece = board.getPieceAt(shooterPos);

        if (!(piece instanceof Shootable shootable)) {
            return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        }

        if (piece.getColor() != currentTurn) {
            return MoveResult.failure(MoveResult.Status.WRONG_TURN);
        }

        Piece captured = shootable.shoot(target, board);

        if (captured == null) {
            return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
        }

        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    public List<Position> getSafeMoves(Piece piece) {
        return piece.getLegalMoves(board);
    }

    public Color getCurrentTurn() { return currentTurn; }
    public boolean isGameOver()   { return gameOver; }
    public Color getWinner()      { return winner; }
    public Board getBoard()       { return board; }
    public int getTotalMoves()    { return totalMoves; }

    private MoveResult finishTurn(Piece captured) {
        Color opponent = currentTurn.opposite();
        if (board.getPieces(opponent).isEmpty()) {
            gameOver = true;
            winner = currentTurn;
        }
        currentTurn = opponent;
        return MoveResult.success(captured, gameOver);
    }

    private void decrementCooldowns() {
        for (Piece p : board.getPieces(currentTurn)) {
            if (p instanceof Archmage archmage)
                archmage.decrementCooldown();
        }
    }
}