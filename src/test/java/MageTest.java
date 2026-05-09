import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Mage;
import pieces.Warrior;

import static org.junit.jupiter.api.Assertions.*;

public class MageTest {

    private Board board;

    @BeforeEach void setUp() { board = new Board(); }

    @Test void movesOneSquareInAnyDirection() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        board.placePiece(m);
        assertEquals(8, m.getLegalMoves(board).size());
    }

    @Test void shootsStraightUpTo4Tiles() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(1,4));
        board.placePiece(m);
        board.placePiece(enemy);
        assertTrue(m.getMagicTargets(board).contains(new Position(1,4)));
    }

    @Test void cannotShootBeyond4Tiles() {
        Mage m = new Mage(Color.WHITE, new Position(7,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(0,4));
        board.placePiece(m);
        board.placePiece(enemy);
        assertFalse(m.getMagicTargets(board).contains(new Position(0,4)));
    }

    @Test void shotBlockedByAllyPiece() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior ally = new Warrior(Color.WHITE, new Position(3,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,4));
        board.placePiece(m);
        board.placePiece(ally);
        board.placePiece(enemy);
        assertFalse(m.getMagicTargets(board).contains(new Position(2,4)));
    }

    @Test void chargesDecreaseAfterShooting() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,6));
        board.placePiece(m);
        board.placePiece(enemy);
        assertEquals(3, m.getMagicCharges());
        m.shootMagic(new Position(4,6), board);
        assertEquals(2, m.getMagicCharges());
    }

    @Test void cannotShootWithNoCharges() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        board.placePiece(m);
        for (int col = 5; col <= 7; col++) {
            Warrior enemy = new Warrior(Color.BLACK, new Position(4, col));
            board.placePiece(enemy);
            m.shootMagic(new Position(4, col), board);
        }
        assertEquals(0, m.getMagicCharges());
        assertFalse(m.canUseMagic());
        assertTrue(m.getMagicTargets(board).isEmpty());
    }

    @Test void shootRemovesEnemyFromBoard() {
        Mage m = new Mage(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,6));
        board.placePiece(m);
        board.placePiece(enemy);
        m.shootMagic(new Position(4,6), board);
        assertNull(board.getPieceAt(new Position(4,6)));
    }
}
