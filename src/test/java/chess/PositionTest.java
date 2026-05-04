package chess;

import chess.core.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test void validPositionsAreAccepted() {
        assertTrue(new Position(0,0).isValid());
        assertTrue(new Position(7,7).isValid());
    }

    @Test void outOfBoundsAreInvalid() {
        assertFalse(new Position(-1,0).isValid());
        assertFalse(new Position(0,8).isValid());
    }

    @Test void offsetWorks() {
        assertEquals(new Position(4,5), new Position(3,3).offset(1,2));
    }

    @Test void equalityIsValueBased() {
        assertEquals(new Position(2,3), new Position(2,3));
        assertNotEquals(new Position(2,3), new Position(3,2));
    }

    @Test void toStringIsChessNotation() {
        assertEquals("a8", new Position(0,0).toString());
        assertEquals("e4", new Position(4,4).toString());
    }
}
