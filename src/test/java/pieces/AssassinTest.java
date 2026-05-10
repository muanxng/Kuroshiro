package pieces;

import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Assassin} piece.
 * Verifies its diagonal leaping abilities, ensuring it can jump over obstacles
 * and correctly capture enemy pieces upon landing.
 */
public class AssassinTest {

    private Board board;

    /** Resets the board before each test to ensure a clean state. */
    @BeforeEach
    void setUp() { board = new Board(); }

    /** Tests that the Assassin can legally calculate all diagonal leaping destinations up to 2 squares away. */
    @Test
    void moveTest() {
        Assassin a = new Assassin(Color.WHITE, new Position(4,4));
        board.placePiece(a);
        int[][] legalMoves = {{2,2},{3,3},{5,5},{6,6},{2,6},{3,5},{5,3},{6,2}};
        for (int[] first : legalMoves){
            assertTrue(a.getLegalMoves(board).contains(new Position(first[0],first[1])));
        }
        assertEquals(8, a.getLegalMoves(board).size());
    }

    /** Tests that the Assassin can successfully leap over an intervening piece to reach its destination. */
    @Test
    void jumpTest() {
        Assassin a = new Assassin(Color.WHITE, new Position(4,4));
        Warrior blocker = new Warrior(Color.WHITE, new Position(3,3));
        board.placePiece(a);
        board.placePiece(blocker);
        assertTrue(a.getLegalMoves(board).contains(new Position(2,2)));
    }

    /** Tests that the Assassin correctly rejects landing on a square occupied by an allied piece. */
    @Test
    void landingTest() {
        Assassin a = new Assassin(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(3,3));
        board.placePiece(a);
        board.placePiece(ally);
        assertFalse(a.getLegalMoves(board).contains(new Position(3,3)));
    }

    /** Tests that the Assassin can legally land on and capture an enemy piece. */
    @Test
    void captureTest() {
        Assassin a = new Assassin(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(3,3));
        board.placePiece(a);
        board.placePiece(enemy);
        assertTrue(a.getLegalMoves(board).contains(new Position(3,3)));
    }
}