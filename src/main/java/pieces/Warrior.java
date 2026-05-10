package pieces;

import core.*;
import java.util.*;

/**
 * Represents a Warrior piece on the game board.
 * The Warrior moves similarly to a standard chess Pawn: moving forward one square,
 * with the option to move two squares on its first turn, and capturing diagonally.
 * Uniquely, the Warrior acts as a "tank" with a health pool of 2 lives, requiring
 * multiple ranged attacks to be defeated.
 */
public class Warrior extends Piece implements Stabbable {

    /** The current health/lives of the Warrior. Starts at 2. */
    private int lives;

    /**
     * Initializes a new Warrior piece with full health (2 lives).
     *
     * @param color the color of the Warrior
     * @param position the initial starting position
     */
    public Warrior(Color color, Position position) {
        super(color, position);
        this.lives = 2;
    }

    /**
     * Calculates all legal movement destinations for the Warrior.
     * Moves strictly forward one square (or two on its very first move) into empty spaces.
     * Captures are made strictly by moving one square diagonally forward.
     * The forward direction depends on the piece's color.
     *
     * @param board the current game board
     * @return a list of valid positions the Warrior can move to or capture on
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        // White moves "up" the board (-1), Black moves "down" (+1)
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
     * Inflicts one point of damage to the Warrior, reducing its lives by one.
     *
     * @return true if the damage was fatal (lives reached 0), false otherwise
     */
    public boolean takeDamage() {
        lives--;
        return !isAlive();
    }

    /**
     * Retrieves the current number of lives the Warrior has remaining.
     *
     * @return the remaining lives
     */
    public int getLives() { return lives; }

    /**
     * Checks if the Warrior is still alive.
     *
     * @return true if lives are greater than 0, false otherwise
     */
    public boolean isAlive() { return lives > 0; }

    /**
     * Executes a stabbing melee attack on a designated target position.
     * The Warrior captures by landing directly on the target.
     *
     * @param target the position being attacked
     * @param board the current game board
     * @return the captured piece that was removed from the board
     */
    @Override
    public Piece stab(Position target, Board board) {
        Piece captured = board.getPieceAt(target);
        board.removePiece(target);
        return captured;
    }

    /**
     * Retrieves the visual symbol representing the Warrior on the board.
     * The symbol dynamically changes based on the Warrior's current health.
     *
     * @return "W" if the Warrior is at full health, "w" if it has taken damage
     */
    @Override
    public String getSymbol() { return lives == 2 ? "W" : "w"; }
}