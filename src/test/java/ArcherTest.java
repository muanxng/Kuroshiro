import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import pieces.Archer;
import pieces.Assassin;
import pieces.Mage;
import pieces.Warrior;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link Archer} piece.
 * Verifies the piece's movement logic, ranged shooting constraints,
 * line-of-sight blocking, and interactions with other pieces (e.g., damaging Warriors).
 */
public class ArcherTest {

    private Board board;

    @BeforeEach
    void setUp() { board = new Board(); }

    @Test
    void moveTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        board.placePiece(a);
        assertEquals(8, a.getLegalMoves(board).size());
    }

    @Test
    void shootTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,2));
        board.placePiece(a);
        board.placePiece(enemy);
        assertTrue(a.getShootTargets(board).contains(new Position(2,2)));
    }

    @Test
    void shootExceedTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(0,0));
        board.placePiece(a);
        board.placePiece(enemy);
        assertFalse(a.getShootTargets(board).contains(new Position(0,0)));
    }

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

    @Test
    void shootAllyTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(3,3));
        board.placePiece(a);
        board.placePiece(ally);
        assertFalse(a.getShootTargets(board).contains(new Position(3,3)));
    }

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
