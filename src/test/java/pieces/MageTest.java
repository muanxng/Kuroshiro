package pieces;

import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Mage} piece.
 * Verifies orthogonal movement, ranged attacks restricted to a 2-tile limit,
 * line-of-sight blocking, and damage application to tank units.
 */
public class MageTest {

    private Board board;

    /** Resets the board before each test to ensure a clean state. */
    @BeforeEach void setUp() { board = new Board(); }

    /** Tests that the Mage can move up to 2 squares orthogonally into empty spaces. */
    @Test
    void moveTest() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        board.placePiece(m);
        int[][] legalMoves = {{2,4},{3,4},{5,4},{6,4},{4,2},{4,3},{4,5},{4,6}};
        for (int[] first : legalMoves){
            assertTrue(m.getLegalMoves(board).contains(new Position(first[0],first[1])));
        }
        assertEquals(8, m.getLegalMoves(board).size());
    }

    /** Tests that the Mage can successfully target an enemy within its 2-square orthogonal range. */
    @Test void shootTest() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,4));
        board.placePiece(m);
        board.placePiece(enemy);
        assertTrue(m.getShootTargets(board).contains(new Position(2,4)));
    }

    /** Tests that the Mage cannot target enemies located beyond its maximum shooting range. */
    @Test void shootExceedTest() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(1,4));
        board.placePiece(m);
        board.placePiece(enemy);
        assertFalse(m.getShootTargets(board).contains(new Position(1,4)));
    }

    /** Tests that the Mage's orthogonal line of sight is correctly blocked by an intervening piece. */
    @Test void shotBlockedByAllyPiece() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(3,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,4));
        board.placePiece(m);
        board.placePiece(ally);
        board.placePiece(enemy);
        assertFalse(m.getShootTargets(board).contains(new Position(2,4)));
    }

    /** Tests that shooting a {@link Warrior} twice is required to capture it. */
    @Test
    void testShootWarrior() {
        Mage mage = new Mage(Color.WHITE, new Position(4, 4));
        Warrior warrior = new Warrior(Color.BLACK, new Position(4, 6));
        board.placePiece(mage);
        board.placePiece(warrior);
        mage.shoot(new Position(4, 6), board);
        assertNotNull(board.getPieceAt(new Position(4, 6)));
        assertEquals(1, warrior.getLives());
        mage.shoot(new Position(4, 6), board);
        assertNull(board.getPieceAt(new Position(4, 6)));
    }

    /** Tests that shooting a standard, non-tank piece results in its immediate capture and removal. */
    @Test
    void pieceRemovedFromBoardTest() {
        Mage mage = new Mage(Color.WHITE, new Position(4, 4));
        Assassin assassin = new Assassin(Color.BLACK, new Position(4, 6));
        board.placePiece(mage);
        board.placePiece(assassin);
        mage.shoot(new Position(4, 6), board);
        assertNull(board.getPieceAt(new Position(4, 6)));
    }
}