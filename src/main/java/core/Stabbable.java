package core;

/**
 * Represents an entity capable of performing close-range melee attacks on the game board.
 * Any piece that can attack an adjacent or nearby square—typically moving into that
 * square to capture the target—should implement this interface.
 */
public interface Stabbable {

    /**
     * Executes a stabbing or melee attack against a specific target position.
     *
     * @param target the designated position to attack
     * @param board the current state of the game board
     * @return the piece that was captured by the attack,
     * or null if the attack was invalid or hit an empty square
     */
    Piece stab(Position target, Board board);
}