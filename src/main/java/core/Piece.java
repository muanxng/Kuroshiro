package core;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract foundation for all game pieces within the Kuroshiro engine.
 * This class provides the base state (such as {@link Color} and {@link Position})
 * and highly reusable utility methods that subclasses rely on to calculate board
 * interactions and movement mechanics.
 * <p>
 * Any specific piece (e.g., Warrior, Mage) must extend this class and implement
 * the abstract methods inherited from {@link Moveable}.
 */
public abstract class Piece implements Moveable {

    /** The team color to which this piece belongs. */
    protected Color color;

    /** The current coordinate of the piece on the board. */
    protected Position position;

    /** Tracks whether the piece has moved from its initial starting square. */
    protected boolean hasMoved;

    /**
     * Initializes a new piece with a specific color and starting position.
     * By default, a newly instantiated piece is marked as not having moved.
     *
     * @param color the {@link Color} of the piece
     * @param position the initial {@link Position} on the board
     */
    public Piece(Color color, Position position) {
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    /**
     * Retrieves the color affiliation of this piece.
     *
     * @return the piece's {@link Color}
     */
    public Color getColor() { return color; }

    /**
     * Retrieves the current board coordinate of this piece.
     *
     * @return the piece's current {@link Position}
     */
    public Position getPosition() { return position; }

    /**
     * Checks whether this piece has been moved from its original starting position.
     *
     * @return {@code true} if the piece has moved at least once, {@code false} otherwise
     */
    public boolean hasMoved() { return hasMoved; }

    /**
     * Updates the piece's coordinate and permanently flags it as having moved.
     *
     * @param position the new {@link Position} to assign to the piece
     */
    public void setPosition(Position position) {
        this.position = position;
        this.hasMoved = true;
    }

    /**
     * Determines if a generic movement to the specified position is legal.
     * A standard move is valid if the target position is within the board boundaries
     * and is either completely empty or occupied by an enemy piece.
     *
     * @param pos the target {@link Position} to evaluate
     * @param board the current {@link Board} state
     * @return {@code true} if the position is a valid movement destination, {@code false} otherwise
     */
    protected boolean canMoveTo(Position pos, Board board) {
        if (!pos.isValid()) return false;
        Piece occupant = board.getPieceAt(pos);
        return occupant == null || occupant.getColor() != this.color;
    }

    /**
     * Checks if a specific board position contains an enemy unit.
     *
     * @param pos the {@link Position} to check
     * @param board the current {@link Board} state
     * @return {@code true} if an enemy piece occupies the position, {@code false} if empty or friendly
     */
    protected boolean isEnemy(Position pos, Board board) {
        Piece occupant = board.getPieceAt(pos);
        return occupant != null && occupant.getColor() != this.color;
    }

    /**
     * Verifies if a specific board position is strictly empty.
     *
     * @param pos the {@link Position} to evaluate
     * @param board the current {@link Board} state
     * @return {@code true} if the position is within board limits and contains no piece
     */
    protected boolean isEmpty(Position pos, Board board) {
        return pos.isValid() && board.getPieceAt(pos) == null;
    }

    /**
     * Calculates a trajectory of valid moves by sliding continuously in a specified vector.
     * The slide terminates when it encounters the board's edge, a friendly piece,
     * or after capturing an enemy piece.
     *
     * @param dr the row direction offset (e.g., -1 for "up", 1 for "down")
     * @param dc the column direction offset (e.g., -1 for "left", 1 for "right")
     * @param board the current {@link Board} state
     * @return a list of all valid {@link Position}s along the sliding path
     */
    protected List<Position> slide(int dr, int dc, Board board) {
        List<Position> moves = new ArrayList<>();
        Position current = position.offset(dr, dc);
        while (current.isValid()) {
            if (isEmpty(current, board)) {
                moves.add(current);
            } else {
                if (isEnemy(current, board)) moves.add(current);
                break;
            }
            current = current.offset(dr, dc);
        }
        return moves;
    }

    /**
     * Manually overrides the movement status flag of the piece.
     * This is particularly useful for engine rollbacks, undoing moves, or specific game resets.
     *
     * @param value {@code true} to flag as moved, {@code false} to flag as unmoved
     */
    public void resetHasMoved(boolean value) {
        this.hasMoved = value;
    }

    @Override
    public abstract List<Position> getLegalMoves(Board board);

    @Override
    public abstract String getSymbol();

    /**
     * Returns a string representation of the piece, primarily used for console output
     * and system debugging.
     *
     * @return a string formatted as "[Color] [ClassName] at [Position]"
     */
    @Override
    public String toString() {
        return color + " " + getClass().getSimpleName() + " at " + position;
    }
}