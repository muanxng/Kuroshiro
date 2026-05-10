package pieces;

import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Warrior} piece.
 * Verifies pawn-like directional movement, first-turn double steps, diagonal captures,
 * forward collision blocking, and the unique multi-life "tank" system.
 */
public class WarriorTest {

    private Board board;

    /** Resets the board before each test to ensure a clean state. */
    @BeforeEach
    void setUp() { board = new Board(); }

    /** Verifies that a new Warrior correctly starts with 2 life points. */
    @Test
    void startsWithTwoLives() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,0));
        assertEquals(2, w.getLives());
    }

    /** Verifies that a Warrior survives the first instance of ranged damage. */
    @Test
    void survivesFirstHit() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,0));
        boolean died = w.takeDamage();
        assertFalse(died);
        assertEquals(1, w.getLives());
    }

    /** Verifies that a Warrior is destroyed only after taking a second point of damage. */
    @Test
    void diesOnSecondHit() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,0));
        w.takeDamage();
        boolean died = w.takeDamage();
        assertTrue(died);
        assertEquals(0, w.getLives());
    }

    /** Tests the standard forward movement of the White Warrior. */
    @Test
    void movesForwardOneSquare() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,3));
        board.placePiece(w);
        assertTrue(w.getLegalMoves(board).contains(new Position(5,3)));
    }

    /** Tests the initial double-step movement available on the piece's first turn. */
    @Test
    void movesTwoSquaresOnFirstMove() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,3));
        board.placePiece(w);
        assertTrue(w.getLegalMoves(board).contains(new Position(4,3)));
    }

    /** Verifies that the double-step option is disabled once the piece has moved. */
    @Test
    void cannotMoveTwoSquaresAfterFirstMove() {
        Warrior w = new Warrior(Color.WHITE, new Position(5,3));
        w.setPosition(new Position(5,3)); // mark as moved
        board.placePiece(w);
        assertFalse(w.getLegalMoves(board).contains(new Position(3,3)));
    }

    /** Tests that Warriors capture enemy units strictly via diagonal-forward moves. */
    @Test
    void capturesDiagonally() {
        Warrior w = new Warrior(Color.WHITE, new Position(4,3));
        board.placePiece(w);
        board.placePiece(new Warrior(Color.BLACK, new Position(3,2)));
        board.placePiece(new Warrior(Color.BLACK, new Position(3,4)));
        assertTrue(w.getLegalMoves(board).contains(new Position(3,2)));
        assertTrue(w.getLegalMoves(board).contains(new Position(3,4)));
    }

    /** Verifies that forward movement is blocked by enemy pieces (no forward capture). */
    @Test
    void cannotCaptureForward() {
        Warrior w = new Warrior(Color.WHITE, new Position(4,3));
        board.placePiece(w);
        board.placePiece(new Warrior(Color.BLACK, new Position(3,3)));
        assertFalse(w.getLegalMoves(board).contains(new Position(3,3)));
    }

    /** Tests that Black Warriors move in the opposite vertical direction (down the board). */
    @Test
    void blackWarriorMovesDown() {
        Warrior w = new Warrior(Color.BLACK, new Position(1,3));
        board.placePiece(w);
        assertTrue(w.getLegalMoves(board).contains(new Position(2,3)));
        assertTrue(w.getLegalMoves(board).contains(new Position(3,3)));
    }
}