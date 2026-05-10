package pieces;

import core.*;
import java.util.*;

/**
 * Represents an Archer piece in the Kuroshiro engine.
 * The Archer is a mobile ranged unit that moves diagonally up to two squares (empty squares only)
 * and performs ranged attacks diagonally up to 2 squares away.
 */
public class Archer extends Piece implements Shootable {

    /** The diagonal directional offsets for standard movement and shooting. */
    private static final int[][] MOVE_DIRS = {
            {-1,-1},{-1,1}, { 1,-1},{ 1,1}
    };

    /** The diagonal directional offsets used for ranged shooting attacks. */
    private static final int[][] SHOOT_DIRS = {
            {-1,-1},{-1,1}, {1,-1},{1,1}
    };

    /** The maximum distance in squares the Archer can shoot (updated to 2). */
    private static final int SHOOT_RANGE = 2;

    /**
     * Initializes a new Archer piece with a specified color and position.
     *
     * @param color the color of the Archer (WHITE or BLACK)
     * @param position the initial starting position on the board
     */
    public Archer(Color color, Position position) {
        super(color, position);
    }

    /**
     * Calculates all legal movement destinations for the Archer.
     * The Archer can move 1 or 2 squares diagonally, but only if the path
     * consists strictly of unoccupied squares.
     *
     * @param board the current game board
     * @return a list of valid, empty diagonal positions the Archer can move to
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : MOVE_DIRS) {
            for (int step = 1; step <= 2; step++) {
                Position candidate = position.offset(dir[0] * step, dir[1] * step);
                if (!candidate.isValid() || !isEmpty(candidate, board)) break;
                moves.add(candidate);
            }
        }
        return moves;
    }

    /**
     * Identifies all valid enemy targets within the Archer's diagonal range.
     * The Archer scans up to 2 squares away; line-of-sight is blocked by the first piece encountered.
     *
     * @param board the current game board
     * @return a list of positions containing enemy pieces within range
     */
    public List<Position> getShootTargets(Board board) {
        List<Position> targets = new ArrayList<>();
        for (int[] dir : SHOOT_DIRS) {
            for (int i = 1; i <= SHOOT_RANGE; i++) {
                Position current = position.offset(dir[0] * i, dir[1] * i);
                if (!current.isValid()) break;

                Piece target = board.getPieceAt(current);
                if (target != null) {
                    if (target.getColor() != this.color) targets.add(current);
                    break;
                }
            }
        }
        return targets;
    }

    /**
     * Executes a ranged attack on a target position.
     * If the target is a {@link Warrior}, it applies damage. For all other pieces,
     * the target is immediately captured.
     *
     * @param target the position to shoot at
     * @param board the current game board
     * @return the piece that was hit (damaged or captured)
     */
    public Piece shoot(Position target, Board board) {
        if (!getShootTargets(board).contains(target)) return null;

        Piece captured = board.getPieceAt(target);

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
     * Implementation of the {@link Shootable} interface to provide valid targets.
     *
     * @param board the current game board
     * @return a list of positions valid for a shooting attack
     */
    @Override
    public List<Position> getTargets(Board board) {
        return getShootTargets(board);
    }

    /**
     * Returns the unique symbol used to represent the Archer in text-based rendering.
     *
     * @return the character "A"
     */
    @Override
    public String getSymbol() { return "A"; }
}