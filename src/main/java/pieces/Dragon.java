package pieces;

import core.*;
import java.util.*;

/**
 * Represents a highly powerful Dragon piece on the game board.
 * The Dragon behaves similarly to a Queen in standard chess, capable of sliding
 * any number of unoccupied squares in any of the 8 orthogonal or diagonal directions.
 * It is a formidable melee unit that captures enemies by landing on them.
 */
public class Dragon extends Piece implements Stabbable {

    /** The 8 directional offsets (orthogonal and diagonal) used for sliding movement. */
    private static final int[][] DIRS = {
            {-1,0},{1,0},{0,-1},{0,1},   // Orthogonal (Up, Down, Left, Right)
            {-1,-1},{-1,1},{1,-1},{1,1}  // Diagonal
    };

    /**
     * Initializes a new Dragon piece.
     *
     * @param color the color of the Dragon
     * @param position the initial starting position
     */
    public Dragon(Color color, Position position) {
        super(color, position);
    }

    /**
     * Calculates all legal movement destinations for the Dragon.
     * The Dragon slides continuously in all 8 directions until it reaches the edge of the board,
     * an allied piece, or an enemy piece (which it can capture).
     *
     * @param board the current game board
     * @return a list of valid positions the Dragon can move to or capture on
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        // Iterate through all 8 directions and use the inherited slide() method
        for (int[] dir : DIRS) {
            moves.addAll(slide(dir[0], dir[1], board));
        }
        return moves;
    }

    /**
     * Executes a stabbing melee attack on a designated target position.
     * The Dragon captures by landing directly on the target, so this method
     * handles the immediate removal of the captured piece.
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
     * Retrieves the visual symbol representing the Dragon on the board.
     *
     * @return the string "D"
     */
    @Override
    public String getSymbol() { return "D"; }
}