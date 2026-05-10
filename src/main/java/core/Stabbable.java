package core;

/**
 * Defines the contract for any piece capable of executing close-range melee attacks
 * within the Kuroshiro engine.
 * <p>
 * Unlike ranged attacks (defined by {@link Shootable}), a successful stab action
 * typically involves the attacking piece moving to occupy the target's {@link Position}
 * after the capture is resolved.
 */
public interface Stabbable {

    /**
     * Executes a melee attack aimed at a specific board coordinate.
     * The exact targeting rules (e.g., adjacent squares, diagonal leaps like the Assassin)
     * and capture mechanics are determined by the specific implementing class.
     *
     * @param target the designated {@link Position} to attack
     * @param board the current state of the game {@link Board}
     * @return the {@link Piece} that was captured by the attack,
     * or {@code null} if the target position was invalid, out of range, or empty
     */
    Piece stab(Position target, Board board);
}