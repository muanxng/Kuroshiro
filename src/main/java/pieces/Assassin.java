package pieces;

import core.*;
import java.util.*;

/**
 * Represents an Assassin piece within the Kuroshiro engine.
 * The Assassin is a highly mobile melee unit that moves and attacks diagonally
 * up to 2 squares away. Similar to a standard chess Knight, its movement is a "leap",
 * meaning it completely ignores any intervening pieces along its path and captures
 * by landing directly on an enemy square.
 */
public class Assassin extends Piece implements Stabbable {

    /** The 4 diagonal directional vectors (offsets) for movement and jumping. */
    private static final int[][] DIAGONAL_DIRS = {
            {-1,-1},{-1,1},{1,-1},{1,1}
    };

    /** The maximum number of squares the Assassin can leap in a single trajectory. */
    private static final int JUMP_RANGE = 2;

    /**
     * Initializes a new Assassin piece.
     *
     * @param color the {@link Color} of the Assassin
     * @param position the initial starting {@link Position} on the board
     */
    public Assassin(Color color, Position position) {
        super(color, position);
    }

    /**
     * Calculates all legal movement and attack destinations for the Assassin.
     * The Assassin evaluates squares along a diagonal path up to its maximum jump range.
     * Because it leaps, it does not check for line-of-sight blockages. A landing square
     * is valid if it remains on the board and is either empty or occupied by an enemy.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid {@link Position} coordinates the Assassin can land on
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : DIAGONAL_DIRS) {
            for (int i = 1; i <= JUMP_RANGE; i++) {
                Position candidate = position.offset(dir[0] * i, dir[1] * i);

                // Stop checking this direction if the jump lands off the board
                if (!candidate.isValid()) break;

                // Can land on empty or enemy squares (jumps over everything in between)
                if (canMoveTo(candidate, board)) {
                    moves.add(candidate);
                }
            }
        }
        return moves;
    }

    /**
     * Executes a melee attack on a designated target position.
     * Because the Assassin implements {@link Stabbable}, this method handles the
     * immediate removal of the captured piece. The engine will handle moving the
     * Assassin to this landing square afterward.
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
     * Returns the unique symbol used to represent the Assassin in text-based rendering.
     *
     * @return the string "S"
     */
    @Override
    public String getSymbol() { return "S"; }
}