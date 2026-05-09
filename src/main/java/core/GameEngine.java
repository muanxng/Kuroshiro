package core;

import pieces.Mage;
import pieces.Archmage;
import pieces.Archer;
import pieces.Warrior;

import java.util.ArrayList;
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
        if (piece == null) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        List<Position> legalMoves = getSafeMoves(piece);
        if (!legalMoves.contains(to)) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

        Piece target = board.getPieceAt(to);
        if (target instanceof Warrior) {
            Warrior warrior = (Warrior) target;
            boolean died = warrior.takeDamage();
            if (!died) {
                // Warrior survived — end turn without moving the attacker
                decrementCooldowns();
                totalMoves++;
                return finishTurn(null);
            }
        }

        Piece captured = board.movePiece(piece, to);

        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    public MoveResult shootMagic(Position magePos, Position target) {
        if (gameOver) return MoveResult.failure(MoveResult.Status.GAME_OVER);

        Piece piece = board.getPieceAt(magePos);
        if (!(piece instanceof Mage)) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        Mage mage = (Mage) piece;
        Piece captured = mage.shootMagic(target, board);
        if (captured == null) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

        decrementCooldowns();
        totalMoves++;
        return finishTurn(captured);
    }

    public MoveResult archmageShoot(Position shooterPos, Position target) {
        if (gameOver) return MoveResult.failure(MoveResult.Status.GAME_OVER);

        Piece piece = board.getPieceAt(shooterPos);
        if (!(piece instanceof Archmage)) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        Archmage archmage = (Archmage) piece;
        Piece captured = archmage.shootMagic(target, board);
        if (captured == null) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

        decrementCooldowns();
        totalMoves++;
        return finishTurn(captured);
    }

    public MoveResult archerShoot(Position shooterPos, Position target) {
        if (gameOver) return MoveResult.failure(MoveResult.Status.GAME_OVER);

        Piece piece = board.getPieceAt(shooterPos);
        if (!(piece instanceof Archer)) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        Archer archer = (Archer) piece;
        Piece captured = archer.shoot(target, board);
        if (captured == null) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

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
            if (p instanceof Archmage)
                ((Archmage) p).decrementCooldown();
        }
    }
}