import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Archmage;
import pieces.Assassin;
import pieces.Warrior;

import static org.junit.jupiter.api.Assertions.*;

public class ArchmageTest {

    private Board board;

    @BeforeEach
    void setUp() { board = new Board(); }

    @Test
    void moveTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(4,4));
        board.placePiece(a);
        assertEquals(8, a.getLegalMoves(board).size());
    }

    @Test
    void shootTest() {
        Archmage a = new Archmage(Color.WHITE, new Position(7,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(0,4));
        board.placePiece(a);
        board.placePiece(enemy);
        assertTrue(a.getShootTargets(board).contains(new Position(0,4)));
    }

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

    @Test
    void testShootWarrior() {
        Archmage archmage = new Archmage(Color.WHITE, new Position(4, 4));
        Warrior warrior = new Warrior(Color.BLACK, new Position(4, 6));
        board.placePiece(archmage);
        board.placePiece(warrior);
        archmage.shoot(new Position(4, 6), board);
        assertNotNull(board.getPieceAt(new Position(4, 6)));
        assertEquals(1, warrior.getLives());
        archmage.decrementCooldown();
        archmage.decrementCooldown();
        archmage.shoot(new Position(4, 6), board);
        assertNull(board.getPieceAt(new Position(4, 6)));
    }

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
