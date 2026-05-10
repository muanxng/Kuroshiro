package pieces;

import core.*;
import java.util.*;

/**
 * Represents a Mage piece within the Kuroshiro engine.
 * The Mage is a mobile ranged unit that moves orthogonally (up, down, left, right)
 * up to 2 squares to empty spaces only, and performs ranged magic attacks orthogonally
 * up to 2 squares away.
 */
public class Mage extends Piece implements Shootable {

    /** The 4 orthogonal directional offsets for standard movement. */
    private static final int[][] MOVE_DIRS = {
            {-1,0},
            { 0,-1},        { 0,1},
            { 1,0}
    };

    /** The 4 orthogonal directional offsets used for ranged shooting attacks. */
    private static final int[][] SHOOT_DIRS = {
            {-1, 0}, {1, 0}, {0,-1}, {0, 1}
    };

    /** The maximum distance in squares the Mage can shoot. */
    private static final int SHOOT_RANGE = 2;

    /**
     * Initializes a new Mage piece.
     *
     * @param color the {@link Color} of the Mage
     * @param position the initial starting {@link Position} on the board
     */
    public Mage(Color color, Position position) {
        super(color, position);
    }

    /**
     * Calculates all legal movement destinations for the Mage.
     * The Mage can move 1 or 2 squares in any of the 4 orthogonal directions, but
     * strictly to unoccupied squares (it cannot capture via physical movement).
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid, empty {@link Position} coordinates the Mage can move to
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
     * Identifies all valid enemy targets the Mage can currently shoot.
     * The Mage scans orthogonally up to its maximum range of 2 squares, stopping at the first
     * piece it encounters. If that piece is an enemy, it becomes a valid target.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of {@link Position} coordinates containing valid enemy targets
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
                    break; // Line of sight is blocked by the first piece hit
                }
            }
        }
        return targets;
    }

    /**
     * Executes a ranged attack on a designated target position.
     * If the target is a {@link Warrior}, it applies a damage state. For all other pieces,
     * the target is immediately captured and removed from the board.
     *
     * @param target the {@link Position} to shoot at
     * @param board the current state of the game {@link Board}
     * @return the {@link Piece} that was captured or damaged, or {@code null} if the target was invalid
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
     * Fulfills the {@link Shootable} interface requirement by providing
     * the list of valid shooting targets.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid target {@link Position} coordinates
     */
    @Override
    public List<Position> getTargets(Board board) {
        return getShootTargets(board);
    }

    /**
     * Returns the unique symbol used to represent the Mage in text-based rendering.
     *
     * @return the string "M"
     */
    @Override
    public String getSymbol() { return "M"; }
}