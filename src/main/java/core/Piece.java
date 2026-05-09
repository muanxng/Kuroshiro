package core;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract representation of a game piece.
 * Provides base attributes such as color and position, as well as common
 * utility methods for determining movement rules and board interactions.
 */
public abstract class Piece implements Moveable {

    protected Color color;
    protected Position position;
    protected boolean hasMoved;

    /**
     * Initializes a new piece with a specific color and starting position.
     * By default, a newly created piece is marked as not having moved.
     *
     * @param color the color of the piece
     * @param position the initial position on the board
     */
    public Piece(Color color, Position position) {
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    /**
     * Retrieves the color of this piece.
     *
     * @return the piece's color
     */
    public Color getColor() { return color; }

    /**
     * Retrieves the current position of this piece on the board.
     *
     * @return the piece's position
     */
    public Position getPosition() { return position; }

    /**
     * Checks whether this piece has moved from its initial starting position.
     *
     * @return true if the piece has moved, false otherwise
     */
    public boolean hasMoved() { return hasMoved; }

    /**
     * Updates the piece's position and marks it as having moved.
     *
     * @param position the new position to assign to the piece
     */
    public void setPosition(Position position) {
        this.position = position;
        this.hasMoved = true;
    }

    /**
     * Determines if the piece can legally move to the specified position.
     * A move is generally considered valid if the target position is within bounds
     * and is either empty or occupied by an enemy piece.
     *
     * @param pos the target position to check
     * @param board the current game board
     * @return true if the position is a valid destination, false otherwise
     */
    protected boolean canMoveTo(Position pos, Board board) {
        if (!pos.isValid()) return false;
        Piece occupant = board.getPieceAt(pos);
        return occupant == null || occupant.getColor() != this.color;
    }

    /**
     * Checks if a given position is occupied by an enemy piece.
     *
     * @param pos the position to check
     * @param board the current game board
     * @return true if an enemy piece occupies the position, false otherwise
     */
    protected boolean isEnemy(Position pos, Board board) {
        Piece occupant = board.getPieceAt(pos);
        return occupant != null && occupant.getColor() != this.color;
    }

    /**
     * Checks if a given position on the board is currently empty.
     *
     * @param pos the position to check
     * @param board the current game board
     * @return true if the position is within bounds and unoccupied
     */
    protected boolean isEmpty(Position pos, Board board) {
        return pos.isValid() && board.getPieceAt(pos) == null;
    }

    /**
     * Calculates a list of valid moves by sliding continuously in a specific direction.
     * The slide stops when it hits an invalid position, a friendly piece, or after
     * capturing an enemy piece.
     *
     * @param dr the row direction offset (e.g., -1 for up, 1 for down)
     * @param dc the column direction offset (e.g., -1 for left, 1 for right)
     * @param board the current game board
     * @return a list of valid positions along the sliding path
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
     * Manually overrides the movement status of the piece.
     * Useful for undoing moves or specific game resets.
     *
     * @param value the boolean value to set the hasMoved flag to
     */
    public void resetHasMoved(boolean value) {
        this.hasMoved = value;
    }

    @Override
    public abstract List<Position> getLegalMoves(Board board);

    @Override
    public abstract String getSymbol();

    /**
     * Returns a string representation of the piece for debugging purposes.
     *
     * @return a string containing the color, class name, and current position
     */
    @Override
    public String toString() {
        return color + " " + getClass().getSimpleName() + " at " + position;
    }
}