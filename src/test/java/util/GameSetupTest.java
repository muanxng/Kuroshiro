package util;

import core.*;
import pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link GameSetup} utility.
 * Verifies that the board initializes correctly with the proper layout,
 * piece types, and color assignments for both the White and Black players.
 */
public class GameSetupTest {

    @Test
    void whiteTest() {
        Board board = GameSetup.createStandardBoard();
        assertTrue(board.getPieceAt(new Position(7,0)) instanceof Assassin);
        assertTrue(board.getPieceAt(new Position(7,1)) instanceof Archer);
        assertTrue(board.getPieceAt(new Position(7,2)) instanceof Mage);
        assertTrue(board.getPieceAt(new Position(7,3)) instanceof Dragon);
        assertTrue(board.getPieceAt(new Position(7,4)) instanceof Archmage);
        assertTrue(board.getPieceAt(new Position(7,5)) instanceof Mage);
        assertTrue(board.getPieceAt(new Position(7,6)) instanceof Archer);
        assertTrue(board.getPieceAt(new Position(7,7)) instanceof Assassin);
        for (int col = 0; col < 8; col++)
            assertTrue(board.getPieceAt(new Position(6,col)) instanceof Warrior);
    }

    @Test
    void blackTest() {
        Board board = GameSetup.createStandardBoard();
        assertTrue(board.getPieceAt(new Position(0,0)) instanceof Assassin);
        assertTrue(board.getPieceAt(new Position(0,1)) instanceof Archer);
        assertTrue(board.getPieceAt(new Position(0,2)) instanceof Mage);
        assertTrue(board.getPieceAt(new Position(0,3)) instanceof Dragon);
        assertTrue(board.getPieceAt(new Position(0,4)) instanceof Archmage);
        assertTrue(board.getPieceAt(new Position(0,5)) instanceof Mage);
        assertTrue(board.getPieceAt(new Position(0,6)) instanceof Archer);
        assertTrue(board.getPieceAt(new Position(0,7)) instanceof Assassin);
        for (int col = 0; col < 8; col++)
            assertTrue(board.getPieceAt(new Position(1,col)) instanceof Warrior);
    }

    @Test
    void allWhitePiecesAreCorrectColor() {
        Board board = GameSetup.createStandardBoard();
        for (Piece p : board.getPieces(Color.WHITE))
            assertEquals(Color.WHITE, p.getColor());
    }

    @Test
    void allBlackPiecesAreCorrectColor() {
        Board board = GameSetup.createStandardBoard();
        for (Piece p : board.getPieces(Color.BLACK))
            assertEquals(Color.BLACK, p.getColor());
    }
}