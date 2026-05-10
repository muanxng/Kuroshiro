package pieces;

import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Archer} piece.
 * Verifies the piece's diagonal movement logic, ranged shooting constraints,
 * line-of-sight blocking, and specific damage interactions with other pieces.
 */
public class ArcherTest {

    private Board board;

    /** Resets the board before each test to ensure a clean state. */
    @BeforeEach
    void setUp() { board = new Board(); }

    /** Tests that the Archer can legally move up to 2 squares diagonally to empty spaces. */
    @Test
    void moveTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        board.placePiece(a);
        int[][] legalMoves = {{2,2},{3,3},{5,5},{6,6},{2,6},{3,5},{5,3},{6,2}};
        for (int[] first : legalMoves){
            assertTrue(a.getLegalMoves(board).contains(new Position(first[0],first[1])));
        }
        assertEquals(8, a.getLegalMoves(board).size());
    }

    /** Tests that the Archer can successfully target an enemy within its 2-square diagonal range. */
    @Test
    void shootTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,2));
        board.placePiece(a);
        board.placePiece(enemy);
        assertTrue(a.getShootTargets(board).contains(new Position(2,2)));
    }

    /** Tests that the Archer cannot target enemies located beyond its maximum shooting range. */
    @Test
    void shootExceedTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(1,1));
        board.placePiece(a);
        board.placePiece(enemy);
        assertFalse(a.getShootTargets(board).contains(new Position(1,1)));
    }

    /** Tests that the Archer's diagonal line of sight is correctly blocked by an intervening piece. */
    @Test
    void shotBlockedTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(3,3));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,2));
        board.placePiece(a);
        board.placePiece(ally);
        board.placePiece(enemy);
        assertFalse(a.getShootTargets(board).contains(new Position(2,2)));
    }

    /** Tests that the Archer cannot lock onto or target allied pieces. */
    @Test
    void shootAllyTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(3,3));
        board.placePiece(a);
        board.placePiece(ally);
        assertFalse(a.getShootTargets(board).contains(new Position(3,3)));
    }

    /** Tests that shooting a {@link Warrior} reduces its health without instantly removing it. */
    @Test
    void testShootWarrior() {
        Archer archer = new Archer(Color.WHITE, new Position(4, 4));
        Warrior warrior = new Warrior(Color.BLACK, new Position(5, 5));
        board.placePiece(archer);
        board.placePiece(warrior);
        archer.shoot(new Position(5, 5), board);
        assertNotNull(board.getPieceAt(new Position(5, 5)));
        assertEquals(1, warrior.getLives());
        archer.shoot(new Position(5, 5), board);
        assertNull(board.getPieceAt(new Position(5, 5)));
    }

    /** Tests that shooting a standard, non-tank piece results in its immediate capture and removal. */
    @Test
    void pieceRemovedFromBoardTest() {
        Archer archer = new Archer(Color.WHITE, new Position(4, 4));
        Assassin assassin = new Assassin(Color.BLACK, new Position(5, 5));
        board.placePiece(archer);
        board.placePiece(assassin);
        archer.shoot(new Position(5, 5), board);
        assertNull(board.getPieceAt(new Position(5, 5)));
    }
}