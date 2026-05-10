package pieces;

import core.Board;
import core.Color;
import core.Piece;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Archmage} piece.
 * Verifies movement, infinite-range orthogonal shooting, line-of-sight validation,
 * and the specific turn-based cooldown mechanics of its magic attacks.
 */
public class ArchmageTest {

    private Board board;

    /** Resets the board before each test to ensure a clean state. */
    @BeforeEach
    void setUp() { board = new Board(); }

    /** Tests that the Archmage can legally move one square in any of the 8 directions to empty spaces. */
    @Test
    void moveTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(4,4));
        board.placePiece(a);
        int[][] legalMoves = {{3,5},{4,5},{5,5},{3,4},{5,4},{3,3},{4,3},{5,3}};
        for (int[] first : legalMoves){
            assertTrue(a.getLegalMoves(board).contains(new Position(first[0],first[1])));
        }
        assertEquals(8, a.getLegalMoves(board).size());
    }

    /** Tests that the Archmage can successfully target an enemy at infinite orthogonal range. */
    @Test
    void shootTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(7,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(0,4));
        board.placePiece(a);
        board.placePiece(enemy);
        assertTrue(a.getShootTargets(board).contains(new Position(0,4)));
    }

    /** Tests that the Archmage's orthogonal line of sight is correctly blocked by an intervening piece. */
    @Test
    void shotBlockedTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(7,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(5,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(0,4));
        board.placePiece(a);
        board.placePiece(ally);
        board.placePiece(enemy);
        assertFalse(a.getShootTargets(board).contains(new Position(0,4)));
    }

    /** Tests that executing a successful magic attack correctly triggers a 4-turn cooldown. */
    @Test
    void cooldownTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,6));
        board.placePiece(a);
        board.placePiece(enemy);
        assertTrue(a.canUseMagic());
        a.shoot(new Position(4,6), board);
        assertFalse(a.canUseMagic());
        assertEquals(2, a.getMagicCooldown());
    }

    /** Tests the step-by-step decrement logic of the magic cooldown timer. */
    @Test
    void cooldownCountTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,6));
        board.placePiece(a);
        board.placePiece(enemy);
        a.shoot(new Position(4,6), board);

        a.decrementCooldown();
        assertEquals(1, a.getMagicCooldown());

        a.decrementCooldown();
        assertEquals(0, a.getMagicCooldown());
        assertTrue(a.canUseMagic());
    }

    /** Tests that the Archmage cannot execute another magic attack while on cooldown. */
    @Test
    void downtimeTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(4,4));
        board.placePiece(a);
        Warrior enemy1 = new Warrior(Color.BLACK, new Position(4,6));
        Warrior enemy2 = new Warrior(Color.BLACK, new Position(4,7));
        board.placePiece(enemy1);
        board.placePiece(enemy2);

        a.shoot(new Position(4,6), board);
        assertNull(a.shoot(new Position(4,7), board));
    }

    /** Tests that shooting a {@link Warrior} twice is required to capture it, respecting cooldowns. */
    @Test
    void testShootWarrior() {
        Archmage archmage = new Archmage(Color.WHITE, new Position(4, 4));
        Warrior warrior = new Warrior(Color.BLACK, new Position(4, 6));
        board.placePiece(archmage);
        board.placePiece(warrior);

        archmage.shoot(new Position(4, 6), board);
        assertNotNull(board.getPieceAt(new Position(4, 6)));
        assertEquals(1, warrior.getLives());

        // Fast-forward cooldown
        archmage.decrementCooldown();
        archmage.decrementCooldown();
        archmage.decrementCooldown();
        archmage.decrementCooldown();

        archmage.shoot(new Position(4, 6), board);
        assertNull(board.getPieceAt(new Position(4, 6)));
    }

    /** Tests that shooting a standard, non-tank piece results in its immediate capture and removal. */
    @Test
    void pieceRemovedFromBoardTest() {
        Archmage archmage = new Archmage(Color.WHITE, new Position(4, 4));
        Assassin assassin = new Assassin(Color.BLACK, new Position(4, 6));
        board.placePiece(archmage);
        board.placePiece(assassin);

        archmage.shoot(new Position(4, 6), board);
        assertNull(board.getPieceAt(new Position(4, 6)));
    }
}