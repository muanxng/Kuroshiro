package core;

import java.util.List;

/**
 * Defines the contract for any piece capable of executing ranged attacks within the Kuroshiro engine.
 * Implementing this interface allows a piece to damage or capture enemy units from a distance
 * without altering its own {@link Position} on the {@link Board}.
 */
public interface Shootable {

    /**
     * Executes a ranged attack aimed at a specific board coordinate.
     * The exact mechanics (such as damage calculation or immediate capture) are determined
     * by the implementing class and the type of piece being targeted.
     *
     * @param target the designated {@link Position} to shoot at
     * @param board the current state of the game board
     * @return the {@link Piece} that was hit (damaged or captured) by the shot,
     * or {@code null} if the target position was invalid, out of range, or empty
     */
    Piece shoot(Position target, Board board);

    /**
     * Calculates all valid enemy coordinates that this piece can currently target.
     * Implementations must account for the piece's specific maximum range,
     * directional shooting rules, and line-of-sight blockage based on the current board state.
     *
     * @param board the current state of the game board
     * @return a list of {@link Position} coordinates representing valid enemy targets within range
     */
    List<Position> getTargets(Board board);
}