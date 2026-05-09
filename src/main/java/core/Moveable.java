package core;

import java.util.List;

/**
 * Represents an entity that can move across the game board.
 * Any piece or object that can change its position must implement this interface.
 */
public interface Moveable {

    /**
     * Calculates all valid destination positions for this entity based on the current board state.
     *
     * @param board the current state of the game board
     * @return a list of legal positions this entity can move to
     */
    List<Position> getLegalMoves(Board board);

    /**
     * Retrieves the visual symbol or string representing this entity.
     *
     * @return the symbol representing the entity
     */
    String getSymbol();
}