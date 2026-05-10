package util;

import core.*;
import pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameSetup} utility.
 * Verifies that the game board initializes with the correct piece types,
 * positions, and team affiliations for both players.
 */
public class GameSetupTest {

    /** Tests that the White back-row units and frontline Warriors are placed in their starting squares. */
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

    /** Tests that the Black back-row units and frontline Warriors are placed in their starting squares. */
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

    /** Verifies that every piece in the White collection is correctly assigned the Color.WHITE attribute. */
    @Test
    void allWhitePiecesAreCorrectColor() {
        Board board = GameSetup.createStandardBoard();
        for (Piece p : board.getPieces(Color.WHITE))
            assertEquals(Color.WHITE, p.getColor());
    }

    /** Verifies that every piece in the Black collection is correctly assigned the Color.BLACK attribute. */
    @Test
    void allBlackPiecesAreCorrectColor() {
        Board board = GameSetup.createStandardBoard();
        for (Piece p : board.getPieces(Color.BLACK))
            assertEquals(Color.BLACK, p.getColor());
    }
}