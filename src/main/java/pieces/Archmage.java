package pieces;

import core.*;
import java.util.*;

/**
 * Represents an Archmage piece on the game board.
 * The Archmage can move one square in any direction to an empty space.
 * It possesses a powerful ranged magic attack that strikes in straight orthogonal lines
 * (up, down, left, right) over any distance, but this ability is constrained by a cooldown system.
 */
public class Archmage extends Piece implements Shootable {

    /** The 8 adjacent directional offsets for standard movement. */
    private static final int[][] MOVE_DIRS = {
            {-1,-1},{-1,0},{-1,1},
            { 0,-1},        { 0,1},
            { 1,-1},{ 1,0},{ 1,1}
    };

    /** The 4 orthogonal directional offsets used for the magic shooting attack. */
    private static final int[][] SHOOT_DIRS = {
            {-1, 0},
            { 1, 0},
            { 0,-1},
            { 0, 1}
    };

    /** The number of turns remaining before the Archmage can use its magic attack again. */
    private int magicCooldown;

    /**
     * Initializes a new Archmage piece.
     * The magic attack is available immediately upon creation (cooldown is 0).
     *
     * @param color the color of the Archmage
     * @param position the initial starting position
     */
    public Archmage(Color color, Position position) {
        super(color, position);
        this.magicCooldown = 0;
    }

    /**
     * Calculates all legal movement destinations for the Archmage.
     * The Archmage can move one square in any of the 8 directions, but
     * strictly to unoccupied squares (it cannot capture via movement).
     *
     * @param board the current game board
     * @return a list of valid, empty positions the Archmage can move to
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : MOVE_DIRS) {
            Position candidate = position.offset(dir[0], dir[1]);
            if (candidate.isValid() && isEmpty(candidate, board)) moves.add(candidate);
        }
        return moves;
    }

    /**
     * Calculates all valid targets the Archmage can currently strike with magic.
     * If the magic is on cooldown, this returns an empty list. Otherwise, it scans
     * orthogonally outward until it hits the first piece in each direction.
     * If that piece is an enemy, it becomes a valid target.
     *
     * @param board the current game board
     * @return a list of valid target positions
     */
    public List<Position> getShootTargets(Board board) {
        List<Position> targets = new ArrayList<>();
        if (!canUseMagic()) return targets;

        for (int[] dir : SHOOT_DIRS) {
            Position current = position.offset(dir[0], dir[1]);
            while (current.isValid()) {
                Piece target = board.getPieceAt(current);
                if (target != null) {
                    if (target.getColor() != this.color) targets.add(current);
                    break; // Line of sight is blocked by the first piece hit
                }
                current = current.offset(dir[0], dir[1]);
            }
        }
        return targets;
    }

    /**
     * Executes the ranged magic attack on a designated target position.
     * Successfully firing the magic puts the ability on a 4-turn cooldown.
     * If the target is a {@link Warrior}, it applies damage. For all other pieces,
     * the target is immediately captured and removed from the board.
     *
     * @param target the position to shoot at
     * @param board the current game board
     * @return the captured piece if it was removed, or the damaged piece if it survived;
     * returns null if the target was invalid or magic is on cooldown
     */
    public Piece shoot(Position target, Board board) {
        if (!canUseMagic()) return null;
        if (!getShootTargets(board).contains(target)) return null;

        Piece captured = board.getPieceAt(target);
        magicCooldown = 4; // Trigger cooldown upon successful cast

        if (captured instanceof Warrior warrior) {
            if (warrior.takeDamage()) {
                board.removePiece(target);
            }
        } else {
            board.removePiece(target);
        }

        return captured;
    }

    /**
     * Fulfills the {@link Shootable} interface requirement by providing
     * the list of valid magic targets.
     *
     * @param board the current game board
     * @return a list of valid target positions
     */
    @Override
    public List<Position> getTargets(Board board) {
        return getShootTargets(board);
    }

    /**
     * Checks if the Archmage's magic attack is ready to use.
     *
     * @return true if the cooldown is 0, false otherwise
     */
    public boolean canUseMagic()      { return magicCooldown == 0; }

    /**
     * Retrieves the current number of turns remaining on the magic cooldown.
     *
     * @return the current cooldown value
     */
    public int getMagicCooldown()     { return magicCooldown; }

    /**
     * Reduces the magic cooldown by one turn, stopping at 0.
     * This should typically be called at the end of a turn cycle.
     */
    public void decrementCooldown() {
        if (magicCooldown > 0) magicCooldown--;
    }

    /**
     * Retrieves the visual symbol representing the Archmage on the board.
     *
     * @return the string "X"
     */
    @Override
    public String getSymbol() { return "X"; }
}