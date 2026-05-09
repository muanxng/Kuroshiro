import core.Board;
import core.Color;
import core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Warrior;

import static org.junit.jupiter.api.Assertions.*;

public class WarriorTest {

    private Board board;

    @BeforeEach
    void setUp() { board = new Board(); }

    @Test
    void startsWithTwoLives() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,0));
        assertEquals(2, w.getLives());
    }

    @Test
    void survivesFirstHit() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,0));
        boolean died = w.takeDamage();
        assertFalse(died);
        assertEquals(1, w.getLives());
    }

    @Test
    void diesOnSecondHit() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,0));
        w.takeDamage();
        boolean died = w.takeDamage();
        assertTrue(died);
        assertEquals(0, w.getLives());
    }

    @Test
    void movesForwardOneSquare() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,3));
        board.placePiece(w);
        assertTrue(w.getLegalMoves(board).contains(new Position(5,3)));
    }

    @Test
    void movesTwoSquaresOnFirstMove() {
        Warrior w = new Warrior(Color.WHITE, new Position(6,3));
        board.placePiece(w);
        assertTrue(w.getLegalMoves(board).contains(new Position(4,3)));
    }

    @Test
    void cannotMoveTwoSquaresAfterFirstMove() {
        Warrior w = new Warrior(Color.WHITE, new Position(5,3));
        w.setPosition(new Position(5,3)); // mark as moved
        board.placePiece(w);
        assertFalse(w.getLegalMoves(board).contains(new Position(3,3)));
    }

    @Test
    void capturesDiagonally() {
        Warrior w = new Warrior(Color.WHITE, new Position(4,3));
        board.placePiece(w);
        board.placePiece(new Warrior(Color.BLACK, new Position(3,2)));
        board.placePiece(new Warrior(Color.BLACK, new Position(3,4)));
        assertTrue(w.getLegalMoves(board).contains(new Position(3,2)));
        assertTrue(w.getLegalMoves(board).contains(new Position(3,4)));
    }

    @Test
    void cannotCaptureForward() {
        Warrior w = new Warrior(Color.WHITE, new Position(4,3));
        board.placePiece(w);
        board.placePiece(new Warrior(Color.BLACK, new Position(3,3)));
        assertFalse(w.getLegalMoves(board).contains(new Position(3,3)));
    }

    @Test
    void blackWarriorMovesDown() {
        Warrior w = new Warrior(Color.BLACK, new Position(1,3));
        board.placePiece(w);
        assertTrue(w.getLegalMoves(board).contains(new Position(2,3)));
        assertTrue(w.getLegalMoves(board).contains(new Position(3,3)));
    }
}
