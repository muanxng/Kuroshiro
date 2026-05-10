package core;

/**
 * Represents a specific spatial coordinate on the 8x8 Kuroshiro game board.
 * <p>
 * This class is strictly immutable; once a position is instantiated, its row
 * and column coordinates cannot be modified. Any calculations for movement
 * generate new {@code Position} instances. The internal coordinates are 0-indexed.
 */
public class Position {

    private final int row;
    private final int col;

    /**
     * Constructs a new {@code Position} with the specified array coordinates.
     *
     * @param row the 0-indexed row coordinate (0 represents the top row, rank 8)
     * @param col the 0-indexed column coordinate (0 represents the leftmost column, file 'a')
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Retrieves the internal row index of this position.
     *
     * @return the row index
     */
    public int getRow() { return row; }

    /**
     * Retrieves the internal column index of this position.
     *
     * @return the column index
     */
    public int getCol() { return col; }

    /**
     * Determines whether this position safely falls within the physical boundaries
     * of the standard 8x8 board.
     *
     * @return {@code true} if both the row and column fall between 0 and 7 (inclusive),
     * {@code false} if the position is out of bounds
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Generates a new {@code Position} offset by the specified directional vector.
     * This method is heavily utilized by pieces to calculate linear movement trajectories.
     * Note: This method does not check if the resulting position is valid on the board.
     *
     * @param dr the delta or change to apply to the row direction
     * @param dc the delta or change to apply to the column direction
     * @return a new {@code Position} instance with the vector offset applied
     */
    public Position offset(int dr, int dc) {
        return new Position(row + dr, col + dc);
    }

    /**
     * Evaluates equality between this position and another object.
     * Two positions are considered mathematically equal if they share the exact
     * same row and column indices.
     *
     * @param o the object to compare against this position
     * @return {@code true} if the given object represents the identical grid coordinate, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    /**
     * Computes a hash code for this position, optimizing it for use as keys
     * within hash-based data structures like {@code HashSet} or {@code HashMap}.
     *
     * @return the computed integer hash code based on the internal coordinates
     */
    @Override
    public int hashCode() { return 31 * row + col; }

    /**
     * Translates the internal 0-indexed integer coordinates into standard
     * algebraic chess notation (e.g., "a8", "e4", "h1").
     *
     * @return the algebraic string representation of the board square
     */
    @Override
    public String toString() {
        return "" + (char)('a' + col) + (8 - row);
    }
}