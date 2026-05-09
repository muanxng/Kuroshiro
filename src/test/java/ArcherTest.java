import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import pieces.Archer;
import pieces.Warrior;

import static org.junit.jupiter.api.Assertions.*;

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
    void shootEnemyTest() {
        Archer a = new Archer(Color.WHITE, new Position(4,4));
        Warrior enemy = new Warrior(Color.BLACK, new Position(2,2));
        board.placePiece(a);
        board.placePiece(enemy);
        a.shoot(new Position(2,2), board);
        assertNull(board.getPieceAt(new Position(2,2)));
    }
}
