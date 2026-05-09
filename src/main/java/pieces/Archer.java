package pieces;

import core.*;
import java.util.*;

/**
 * Represents an Archer piece on the game board.
 * The Archer can move one square in any direction (to empty squares only)
 * and can perform ranged shooting attacks diagonally up to 3 squares away.
 */
public class Archer extends Piece implements Shootable {

    /** The 8 adjacent directional offsets for standard movement. */
    private static final int[][] MOVE_DIRS = {
            {-1,-1},{-1,0},{-1,1},
            { 0,-1},        { 0,1},
            { 1,-1},{ 1,0},{ 1,1}
    };

    /** The 4 diagonal directional offsets used for ranged shooting attacks. */
    private static final int[][] SHOOT_DIRS = {
            {-1,-1},{-1,1},{1,-1},{1,1}
    };

    /** The maximum distance in squares the Archer can shoot. */
    private static final int SHOOT_RANGE = 3;

    /**
     * Initializes a new Archer piece.
     *
     * @param color the color of the Archer
     * @param position the initial starting position
     */
    public Archer(Color color, Position position) {
        super(color, position);
    }

    /**
     * Calculates all legal movement destinations for the Archer.
     * The Archer can move one square in any of the 8 directions, but
     * strictly to unoccupied squares (it cannot capture via movement).
     *
     * @param board the current game board
     * @return a list of valid, empty positions the Archer can move to
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
     * Calculates all valid targets the Archer can currently shoot.
     * The Archer scans diagonally up to its maximum range, stopping at the first
     * piece it encounters. If that piece is an enemy, it becomes a valid target.
     *
     * @param board the current game board
     * @return a list of valid target positions
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
     * If the target is a {@link Warrior}, it applies damage and only removes the
     * Warrior if the damage is fatal. For all other pieces, the target is
     * immediately captured and removed from the board.
     *
     * @param target the position to shoot at
     * @param board the current game board
     * @return the captured piece if it was removed, or the damaged piece if it survived;
     * returns null if the target was invalid
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
     * @param board the current game board
     * @return a list of valid target positions
     */
    @Override
    public List<Position> getTargets(Board board) {
        return getShootTargets(board);
    }

    /**
     * Retrieves the visual symbol representing the Archer on the board.
     *
     * @return the string "A"
     */
    @Override
    public String getSymbol() { return "A"; }
}