package pieces;

import core.*;
import java.util.*;

/**
 * Represents a Warrior piece within the Kuroshiro engine.
 * The Warrior serves as the frontline infantry. Its movement closely mirrors a standard
 * chess Pawn: advancing forward one square into empty spaces, with the option to move
 * two squares strictly on its first turn, and capturing enemy pieces one square diagonally forward.
 * <p>
 * Uniquely, the Warrior acts as a "tank" with a resilient health pool of 2 lives.
 * It can withstand one ranged attack without dying, requiring subsequent damage to be fully defeated.
 * Note: Melee attacks (stabs) and standard movement captures bypass this health pool
 * and capture the Warrior instantly.
 */
public class Warrior extends Piece implements Stabbable {

    /** The current health/lives of the Warrior. Always initializes at 2. */
    private int lives;

    /**
     * Initializes a new Warrior piece with full health.
     *
     * @param color the {@link Color} of the Warrior
     * @param position the initial starting {@link Position} on the board
     */
    public Warrior(Color color, Position position) {
        super(color, position);
        this.lives = 2;
    }

    /**
     * Calculates all legal movement and capture destinations for the Warrior.
     * The forward direction is determined by the piece's color (White moves "up" the board,
     * Black moves "down"). The Warrior can move straight forward to empty squares, and
     * diagonally forward to squares occupied by an enemy.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid {@link Position} coordinates the Warrior can step to or capture on
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        // White moves "up" the board (-1 rank), Black moves "down" (+1 rank)
        int dir = (color == Color.WHITE) ? -1 : 1;

        // Forward one square
        Position one = position.offset(dir, 0);
        if (one.isValid() && isEmpty(one, board)) {
            moves.add(one);
            // Forward two squares (only if it hasn't moved yet and the first square is also empty)
            if (!hasMoved) {
                Position two = position.offset(dir * 2, 0);
                if (isEmpty(two, board)) moves.add(two);
            }
        }

        // Diagonal captures
        for (int dc : new int[]{-1, 1}) {
            Position capture = position.offset(dir, dc);
            if (capture.isValid() && isEnemy(capture, board)) {
                moves.add(capture);
            }
        }

        return moves;
    }

    /**
     * Inflicts one point of damage to the Warrior, simulating a ranged attack hit.
     *
     * @return {@code true} if the damage was fatal (lives reached 0), {@code false} if the Warrior survived
     */
    public boolean takeDamage() {
        lives--;
        return !isAlive();
    }

    /**
     * Retrieves the current number of lives the Warrior has remaining.
     *
     * @return the remaining health value
     */
    public int getLives() { return lives; }

    /**
     * Evaluates if the Warrior is still alive and active on the board.
     *
     * @return {@code true} if lives are greater than 0, {@code false} otherwise
     */
    public boolean isAlive() { return lives > 0; }

    /**
     * Executes a melee attack on a designated target position.
     * Because the Warrior implements {@link Stabbable}, it captures by physically moving
     * to the target's square. This method handles the immediate removal of the enemy piece.
     *
     * @param target the {@link Position} being attacked and landed on
     * @param board the current state of the game {@link Board}
     * @return the captured {@link Piece} that was removed from the board, or {@code null} if empty
     */
    @Override
    public Piece stab(Position target, Board board) {
        Piece captured = board.getPieceAt(target);
        board.removePiece(target);
        return captured;
    }

    /**
     * Returns the unique symbol used to represent the Warrior in text-based rendering.
     * This symbol dynamically shifts based on the Warrior's current health pool.
     *
     * @return the string "W" if the Warrior is at full health, or "w" if it has taken damage
     */
    @Override
    public String getSymbol() { return lives == 2 ? "W" : "w"; }
}