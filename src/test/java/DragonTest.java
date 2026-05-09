import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Dragon;
import pieces.Warrior;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link Dragon} piece.
 * Verifies continuous sliding movement in all 8 directions, capturing mechanics,
 * and proper collision detection with blocking pieces.
 */
public class DragonTest {

    private Board board;

    @BeforeEach
    void setUp() { board = new Board(); }

    @Test
    void moveStraightTest() {
        Dragon d = new Dragon(Color.WHITE, new Position(4,4));
        board.placePiece(d);
        assertTrue(d.getLegalMoves(board).contains(new Position(0,4)));
        assertTrue(d.getLegalMoves(board).contains(new Position(7,4)));
        assertTrue(d.getLegalMoves(board).contains(new Position(4,0)));
        assertTrue(d.getLegalMoves(board).contains(new Position(4,7)));
    }

    @Test
    void moveDiagonalTest() {
        Dragon d = new Dragon(Color.WHITE, new Position(4,4));
        board.placePiece(d);
        assertTrue(d.getLegalMoves(board).contains(new Position(0,0)));
        assertTrue(d.getLegalMoves(board).contains(new Position(7,7)));
        assertTrue(d.getLegalMoves(board).contains(new Position(1,7)));
        assertTrue(d.getLegalMoves(board).contains(new Position(7,1)));
    }

    @Test
    void blockedTest() {
        Dragon d = new Dragon(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(4,6));
        board.placePiece(d);
        board.placePiece(ally);
        assertFalse(d.getLegalMoves(board).contains(new Position(4,6)));
        assertFalse(d.getLegalMoves(board).contains(new Position(4,7)));
    }

    @Test
    void captureTest() {
        Dragon d = new Dragon(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,6));
        board.placePiece(d);
        board.placePiece(enemy);
        assertTrue(d.getLegalMoves(board).contains(new Position(4,6)));
        assertFalse(d.getLegalMoves(board).contains(new Position(4,7)));
    }
}
