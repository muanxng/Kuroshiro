package core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Position} data class.
 * Verifies grid boundary validation, coordinate offset calculations,
 * value-based equality checking, and algebraic chess notation conversion.
 */
public class PositionTest {

    /** Tests that coordinates within the 8x8 grid bounds are marked as valid. */
    @Test
    void validPositionsAreAccepted() {
        assertTrue(new Position(0,0).isValid());
        assertTrue(new Position(7,7).isValid());
    }

    /** Tests that coordinates outside the board boundaries are correctly rejected. */
    @Test
    void outOfBoundsAreInvalid() {
        assertFalse(new Position(-1,0).isValid());
        assertFalse(new Position(0,8).isValid());
    }

    /** Tests that applying a directional offset produces the correct new coordinate. */
    @Test
    void offsetWorks() {
        assertEquals(new Position(4,5), new Position(3,3).offset(1,2));
    }

    /** Tests that two independent instances with identical coordinates are considered equal. */
    @Test
    void equalityIsValueBased() {
        assertEquals(new Position(2,3), new Position(2,3));
        assertNotEquals(new Position(2,3), new Position(3,2));
    }

    /** Tests the conversion of 0-indexed internal coordinates into algebraic notation. */
    @Test
    void toStringIsChessNotation() {
        assertEquals("a8", new Position(0,0).toString());
        assertEquals("e4", new Position(4,4).toString());
    }
}