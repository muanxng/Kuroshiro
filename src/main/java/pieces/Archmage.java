package pieces;

import core.*;
import java.util.*;

/**
 * Represents an Archmage piece within the Kuroshiro engine.
 * The Archmage is an upgraded magic caster with limited mobility (moving one square
 * in any direction to empty spaces only) but possessing a devastating ranged attack.
 * Its magic strikes in straight orthogonal lines over an infinite distance, but is
 * balanced by a strict cooldown system.
 */
public class Archmage extends Piece implements Shootable {

    /** The 8 directional offsets (horizontal, vertical, diagonal) for standard movement. */
    private static final int[][] MOVE_DIRS = {
            {-1,-1},{-1,0},{-1,1},
            { 0,-1},        { 0,1},
            { 1,-1},{ 1,0},{ 1,1}
    };

    /** The 4 orthogonal directional offsets (up, down, left, right) used for the magic attack. */
    private static final int[][] SHOOT_DIRS = {
            {-1, 0}, { 1, 0}, { 0,-1}, { 0, 1}
    };

    /** The number of turns remaining before the Archmage can cast its magic attack again. */
    private int magicCooldown;

    /**
     * Initializes a new Archmage piece.
     * The magic attack is fully charged and available immediately upon creation
     * (the initial cooldown is set to 0).
     *
     * @param color the {@link Color} of the Archmage
     * @param position the initial starting {@link Position} on the board
     */
    public Archmage(Color color, Position position) {
        super(color, position);
        this.magicCooldown = 0;
    }

    /**
     * Calculates all legal movement destinations for the Archmage.
     * The Archmage moves similarly to a standard King (one square in any of the 8 directions),
     * but strictly to unoccupied squares. It cannot capture enemies via physical movement.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid, empty {@link Position} coordinates the Archmage can step to
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : MOVE_DIRS) {
            Position candidate = position.offset(dir[0], dir[1]);
            if (candidate.isValid() && isEmpty(candidate, board)) moves.add(candidate);
        }
        return moves;
    }

    /**
     * Identifies all valid enemy targets the Archmage can currently strike with magic.
     * If the ability is on cooldown, this immediately returns an empty list. Otherwise,
     * it casts rays orthogonally outward until it hits the first piece in each direction.
     * If that piece belongs to the opponent, it is added as a valid target.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of {@link Position} coordinates containing valid enemy targets
     */
    public List<Position> getShootTargets(Board board) {
        List<Position> targets = new ArrayList<>();
        if (!canUseMagic()) return targets;

        for (int[] dir : SHOOT_DIRS) {
            Position current = position.offset(dir[0], dir[1]);
            while (current.isValid()) {
                Piece target = board.getPieceAt(current);
                if (target != null) {
                    if (target.getColor() != this.color) targets.add(current);
                    break;
                }
                current = current.offset(dir[0], dir[1]);
            }
        }
        return targets;
    }

    /**
     * Executes the infinite-range magic attack on a designated target position.
     * Successfully firing the magic puts the ability on a 2-turn cooldown.
     * If the target is a {@link Warrior}, it applies a damage state. For all other piece types,
     * the target is immediately captured and removed from the board.
     *
     * @param target the {@link Position} to shoot at
     * @param board the current state of the game {@link Board}
     * @return the {@link Piece} that was captured or damaged, or {@code null} if the target
     * was invalid or the magic is currently on cooldown
     */
    public Piece shoot(Position target, Board board) {
        if (!canUseMagic()) return null;
        if (!getShootTargets(board).contains(target)) return null;

        Piece captured = board.getPieceAt(target);
        magicCooldown = 2; // Trigger cooldown upon successful cast

        if (captured instanceof Warrior warrior) {
            if (warrior.takeDamage()) {
                board.removePiece(target);
            }
        } else {
            board.removePiece(target);
        }

        return captured;
    }

    /**
     * Fulfills the {@link Shootable} interface requirement by providing
     * the list of valid magic targets.
     *
     * @param board the current state of the game {@link Board}
     * @return a list of valid target {@link Position} coordinates
     */
    @Override
    public List<Position> getTargets(Board board) {
        return getShootTargets(board);
    }

    /**
     * Evaluates if the Archmage's magic attack is fully recharged and ready to fire.
     *
     * @return {@code true} if the cooldown is at 0, {@code false} otherwise
     */
    public boolean canUseMagic()      { return magicCooldown == 0; }

    /**
     * Retrieves the current number of turns remaining on the magic cooldown timer.
     *
     * @return the current cooldown integer value
     */
    public int getMagicCooldown()     { return magicCooldown; }

    /**
     * Decrements the magic cooldown timer by one turn, clamping at a minimum of 0.
     * This method is typically invoked by the engine at the end of a turn cycle.
     */
    public void decrementCooldown() {
        if (magicCooldown > 0) magicCooldown--;
    }

    /**
     * Returns the unique symbol used to represent the Archmage in text-based rendering.
     *
     * @return the string "X"
     */
    @Override
    public String getSymbol() { return "X"; }
}