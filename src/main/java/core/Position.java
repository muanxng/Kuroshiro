package core;

/**
 * Represents a specific coordinate on the 8x8 game board.
 * This class is immutable; once a position is created, its coordinates cannot change.
 */
public class Position {

    private final int row;
    private final int col;

    /**
     * Constructs a new Position with the specified row and column.
     *
     * @param row the zero-indexed row coordinate
     * @param col the zero-indexed column coordinate
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Retrieves the row coordinate of this position.
     *
     * @return the row index
     */
    public int getRow() { return row; }

    /**
     * Retrieves the column coordinate of this position.
     *
     * @return the column index
     */
    public int getCol() { return col; }

    /**
     * Determines whether the position falls within the boundaries of an 8x8 board.
     *
     * @return true if both the row and column are between 0 and 7 (inclusive), false otherwise
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Creates a new Position offset by the given directional values.
     * This is highly useful for calculating piece movement trajectories.
     *
     * @param dr the change in the row direction
     * @param dc the change in the column direction
     * @return a new Position instance with the offset applied
     */
    public Position offset(int dr, int dc) {
        return new Position(row + dr, col + dc);
    }

    /**
     * Compares this position to the specified object.
     * Two positions are considered equal if they share the exact same row and column.
     *
     * @param o the object to compare against
     * @return true if the given object represents the same grid coordinate, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    /**
     * Generates a hash code for this position, allowing it to be used effectively
     * in hash-based collections like HashSet or HashMap.
     *
     * @return the hash code value based on the row and column
     */
    @Override
    public int hashCode() { return 31 * row + col; }

    /**
     * Converts the internal 0-indexed integer coordinates into standard
     * algebraic game notation (e.g., "a8", "e4", "h1").
     *
     * @return the string representation of the position in algebraic notation
     */
    @Override
    public String toString() {
        return "" + (char)('a' + col) + (8 - row);
    }
}