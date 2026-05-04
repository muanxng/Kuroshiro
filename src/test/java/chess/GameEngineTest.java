package chess;

import chess.core.*;
import chess.pieces.*;
import chess.util.GameSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    private Board board;
    private GameEngine engine;

    @BeforeEach void setUp() { board = new Board(); engine = new GameEngine(board); }

    @Test void whiteMovesFirst() {
        assertEquals(Color.WHITE, engine.getCurrentTurn());
    }

    @Test void turnAlternates() {
        board.placePiece(new King(Color.WHITE, new Position(7,4)));
        board.placePiece(new King(Color.BLACK, new Position(0,4)));
        board.placePiece(new Pawn(Color.WHITE, new Position(6,0)));
        engine.makeMove(new Position(6,0), new Position(5,0));
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    @Test void cannotMoveOpponentPiece() {
        board.placePiece(new King(Color.WHITE, new Position(7,4)));
        board.placePiece(new King(Color.BLACK, new Position(0,4)));
        board.placePiece(new Pawn(Color.BLACK, new Position(1,0)));
        assertEquals(MoveResult.Status.WRONG_TURN,
            engine.makeMove(new Position(1,0), new Position(2,0)).getStatus());
    }

    @Test void captureReturnsPiece() {
        board.placePiece(new King(Color.WHITE, new Position(7,4)));
        board.placePiece(new King(Color.BLACK, new Position(0,4)));
        board.placePiece(new Rook(Color.WHITE, new Position(4,0)));
        board.placePiece(new Pawn(Color.BLACK, new Position(4,5)));
        MoveResult result = engine.makeMove(new Position(4,0), new Position(4,5));
        assertTrue(result.isSuccess());
        assertNotNull(result.getCapturedPiece());
    }

    @Test void checkIsDetected() {
        board.placePiece(new King(Color.WHITE, new Position(7,4)));
        board.placePiece(new King(Color.BLACK, new Position(0,0)));
        board.placePiece(new Rook(Color.BLACK, new Position(7,0)));
        assertTrue(engine.isInCheck(Color.WHITE));
        assertFalse(engine.isInCheck(Color.BLACK));
    }

    @Test void cannotMoveIntoCheck() {
        board.placePiece(new King(Color.WHITE, new Position(7,4)));
        board.placePiece(new King(Color.BLACK, new Position(0,0)));
        board.placePiece(new Rook(Color.BLACK, new Position(0,5)));
        assertFalse(engine.makeMove(new Position(7,4), new Position(7,5)).isSuccess());
    }

    @Test void scholarsMateIsCheckmate() {
        board = GameSetup.createStandardBoard();
        engine = new GameEngine(board);
        engine.makeMove(new Position(6,4), new Position(4,4)); // e4
        engine.makeMove(new Position(1,4), new Position(3,4)); // e5
        engine.makeMove(new Position(7,5), new Position(4,2)); // Bc4
        engine.makeMove(new Position(0,1), new Position(2,2)); // Nc6
        engine.makeMove(new Position(7,3), new Position(3,7)); // Qh5
        engine.makeMove(new Position(0,6), new Position(2,5)); // Nf6
        MoveResult result = engine.makeMove(new Position(3,7), new Position(1,5)); // Qxf7#
        assertTrue(result.isCheckmate());
        assertEquals(Color.WHITE, engine.getWinner());
    }
}
