package core;

import java.util.List;

/**
 * Represents an entity capable of performing ranged attacks on the game board.
 * Any piece that can attack from a distance without moving its own position
 * should implement this interface.
 */
public interface Shootable {

    /**
     * Executes a shooting action aimed at a specific target position.
     *
     * @param target the designated position to shoot at
     * @param board the current state of the game board
     * @return the piece that was hit and captured by the shot,
     * or null if the shot was invalid or hit an empty square
     */
    Piece shoot(Position target, Board board);

    /**
     * Calculates all valid target positions that this entity can currently shoot at
     * based on its range and the current board state.
     *
     * @param board the current state of the game board
     * @return a list of positions representing valid targets within range
     */
    List<Position> getTargets(Board board);
}