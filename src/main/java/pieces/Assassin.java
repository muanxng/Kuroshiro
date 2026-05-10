package pieces;

import core.*;
import java.util.*;

/**
 * Represents an Assassin piece on the game board.
 * The Assassin is a highly mobile melee unit that can jump diagonally up to 4 squares
 * in any direction. Similar to a standard chess Knight, it leaps over any intervening
 * pieces along its path and captures by landing directly on an enemy piece.
 */
public class Assassin extends Piece implements Stabbable {

    /** The 4 diagonal directional offsets for movement and jumping. */
    private static final int[][] DIAGONAL_DIRS = {
            {-1,-1},{-1,1},{1,-1},{1,1}
    };

    /** The maximum number of squares the Assassin can leap in a single move. */
    private static final int JUMP_RANGE = 2;

    /**
     * Initializes a new Assassin piece.
     *
     * @param color the color of the Assassin
     * @param position the initial starting position
     */
    public Assassin(Color color, Position position) {
        super(color, position);
    }

    /**
     * Calculates all legal movement destinations for the Assassin.
     * The Assassin can jump to any square along a diagonal path up to 4 squares away,
     * completely ignoring (leaping over) any pieces in between. The landing square
     * must be either empty or occupied by an enemy piece.
     *
     * @param board the current game board
     * @return a list of valid positions the Assassin can land on
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : DIAGONAL_DIRS) {
            for (int i = 1; i <= JUMP_RANGE; i++) {
                Position candidate = position.offset(dir[0] * i, dir[1] * i);

                // Stop checking this direction if the jump goes off the board
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
     * Executes a stabbing melee attack on a designated target position.
     * Since the Assassin captures by landing on its target, this method
     * handles the immediate removal of the captured piece from the board.
     *
     * @param target the position being attacked and landed on
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
     * Retrieves the visual symbol representing the Assassin on the board.
     *
     * @return the string "S"
     */
    @Override
    public String getSymbol() { return "S"; }
}