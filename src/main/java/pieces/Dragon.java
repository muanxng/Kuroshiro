package pieces;

import core.*;
import java.util.*;

/**
 * Represents a highly powerful Dragon piece within the Kuroshiro engine.
 * The Dragon is a formidable melee unit that behaves similarly to a standard chess Queen,
 * capable of sliding in all 8 orthogonal and diagonal directions. However, its movement
 * and attack range are strictly capped at a maximum of 4 squares per turn.
 */
public class Dragon extends Piece implements Stabbable {

    /** The maximum number of squares the Dragon can traverse in a single sliding move. */
    private static final int MAX_SLIDE = 4;

    /** The 8 directional vectors (orthogonal and diagonal) used for sliding movement. */
    private static final int[][] DIRS = {
            {-1,0},{1,0},{0,-1},{0,1},   // Orthogonal (Up, Down, Left, Right)
            {-1,-1},{-1,1},{1,-1},{1,1}  // Diagonal
    };

    /**
     * Initializes a new Dragon piece.
     *
     * @param color the {@link Color} of the Dragon
     * @param position the initial starting {@link Position} on the board
     */
    public Dragon(Color color, Position position) {
        super(color, position);
    }

    /**
     * A specialized sliding mechanic that restricts the Dragon's movement range.
     * It evaluates squares along a specified vector until it hits the board edge,
     * encounters another piece, or reaches the {@link #MAX_SLIDE} limit.
     *
     * @param dr the row direction offset
     * @param dc the column direction offset
     * @param board the current state of the game {@link Board}
     * @return a list of valid {@link Position} coordinates along the limited path
     */
    private List<Position> slideLimited(int dr, int dc, Board board) {
        List<Position> moves = new ArrayList<>();
        Position current = position.offset(dr, dc);
        int steps = 0;

        while (current.isValid() && steps < MAX_SLIDE) {
            if (isEmpty(current, board)) {
                moves.add(current);
            } else {
                if (isEnemy(current, board)) moves.add(current);
                break;
            }
            current = current.offset(dr, dc);
            steps++;
        }
        return moves;
    }

    /**
     * Calculates all legal movement and attack destinations for the Dragon.
     * The Dragon evaluates all 8 directions, sliding continuously up to 4 squares
     * until its path is obstructed.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid {@link Position} coordinates the Dragon can move to or capture on
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        // Iterate through all 8 directions and use the constrained sliding method
        for (int[] dir : DIRS) {
            moves.addAll(slideLimited(dir[0], dir[1], board));
        }
        return moves;
    }

    /**
     * Executes a melee attack on a designated target position.
     * Because the Dragon implements {@link Stabbable}, it captures by physically moving
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
     * Returns the unique symbol used to represent the Dragon in text-based rendering.
     *
     * @return the string "D"
     */
    @Override
    public String getSymbol() { return "D"; }
}