package core;

import java.util.List;

/**
 * Defines the contract for any entity capable of movement within the Kuroshiro game engine.
 * Every standard game piece must implement this interface to interact with the {@link Board}
 * and be recognized by the {@link GameEngine} during the movement phase of a turn.
 */
public interface Moveable {

    /**
     * Calculates all valid, legal destination coordinates for this entity.
     * Implementations of this method should account for piece-specific movement rules,
     * board boundaries, and obstacle collisions based on the current board state.
     *
     * @param board the current state of the game board
     * @return a list of legal {@link Position} coordinates this entity is permitted to move to
     */
    List<Position> getLegalMoves(Board board);

    /**
     * Retrieves the visual identifier representing this specific entity type.
     * This symbol is primarily used for text-based rendering and debugging,
     * but can also serve as a lightweight identifier for UI components.
     *
     * @return a {@code String} symbol representing the entity (e.g., "A" for Archer)
     */
    String getSymbol();
}