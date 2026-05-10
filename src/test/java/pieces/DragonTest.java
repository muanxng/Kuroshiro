package pieces;

import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void moveTest() {
        Dragon d = new Dragon(Color.WHITE, new Position(4,4));
        board.placePiece(d);
        int[][] legalMoves = {{3,4},{2,4},{1,4},{0,4},{5,4},{6,4},{7,4},{4,3},{4,2},{4,1},{4,0},{4,5},{4,6},{4,7},
                              {3,3},{2,2},{1,1},{0,0},{5,5},{6,6},{7,7},{3,5},{2,6},{1,7},{5,3},{6,2},{7,1}};
        for (int[] first : legalMoves){
            assertTrue(d.getLegalMoves(board).contains(new Position(first[0],first[1])));
        }
        assertEquals(27,d.getLegalMoves(board).size());
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
