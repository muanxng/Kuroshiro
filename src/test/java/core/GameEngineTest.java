package core;

import pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameEngine} mechanics and win conditions.
 */
public class GameEngineTest {

    private Board board;
    private GameEngine engine;

    /** Resets the board and engine before each test. */
    @BeforeEach
    void setUp() {
        board = new Board();
        engine = new GameEngine(board);
    }

    /** Tests that White takes the first turn. */
    @Test
    void whiteFirstTest() {
        assertEquals(Color.WHITE, engine.getCurrentTurn());
    }

    /** Tests that turns alternate successfully after a valid move. */
    @Test
    void alternatingTurnTest() {
        board.placePiece(new Warrior(Color.WHITE, new Position(6,0)));
        board.placePiece(new Archmage(Color.BLACK, new Position(0,4)));
        engine.makeMove(new Position(6,0), new Position(5,0));
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    /** Tests that moving an opponent's piece is rejected. */
    @Test
    void opponentPieceTest() {
        board.placePiece(new Warrior(Color.BLACK, new Position(1,0)));
        assertEquals(MoveResult.Status.WRONG_TURN,
                engine.makeMove(new Position(1,0), new Position(2,0)).getStatus());
    }

    /** Tests that a successful melee stab returns the captured piece. */
    @Test
    void returnedCapturedPieceTest() {
        board.placePiece(new Dragon(Color.WHITE, new Position(4,0)));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,5));
        enemy.takeDamage();
        board.placePiece(enemy);
        MoveResult result = engine.stab(new Position(4,0), new Position(4,5));
        assertTrue(result.isSuccess());
        assertNotNull(result.getCapturedPiece());
    }

    /** Tests that the game ends and assigns a winner when all enemy pieces are captured. */
    @Test
    void gameEndTest() {
        board.placePiece(new Dragon(Color.WHITE, new Position(4,0)));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,5));
        enemy.takeDamage();
        board.placePiece(enemy);
        engine.stab(new Position(4,0), new Position(4,5));
        assertTrue(engine.isGameOver());
        assertEquals(Color.WHITE, engine.getWinner());
    }

    /** Tests that the total move counter increments correctly. */
    @Test
    void totalMovesTest() {
        board.placePiece(new Warrior(Color.WHITE, new Position(6,0)));
        board.placePiece(new Warrior(Color.BLACK, new Position(1,0)));
        engine.makeMove(new Position(6,0), new Position(5,0));
        assertEquals(1, engine.getTotalMoves());
        engine.makeMove(new Position(1,0), new Position(2,0));
        assertEquals(2, engine.getTotalMoves());
    }

    /** Tests the Mage's ranged attack functionality. */
    @Test
    void mageShootTest() {
        board.placePiece(new Mage(Color.WHITE, new Position(4,4)));
        board.placePiece(new Warrior(Color.BLACK, new Position(4,6)));
        board.placePiece(new Warrior(Color.WHITE, new Position(0,0)));
        MoveResult result = engine.shoot(new Position(4,4), new Position(4,6));
        assertTrue(result.isSuccess());
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    /** Tests the Archmage's infinite-range orthogonal attack. */
    @Test
    void archmageShootTest() {
        board.placePiece(new Archmage(Color.WHITE, new Position(7,4)));
        board.placePiece(new Warrior(Color.BLACK, new Position(0,4)));
        board.placePiece(new Warrior(Color.WHITE, new Position(0,0)));
        MoveResult result = engine.shoot(new Position(7,4), new Position(0,4));
        assertTrue(result.isSuccess());
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    /** Tests the Archer's diagonal ranged attack. */
    @Test
    void archerShootTest() {
        board.placePiece(new Archer(Color.WHITE, new Position(4,4)));
        board.placePiece(new Warrior(Color.BLACK, new Position(2,2)));
        board.placePiece(new Warrior(Color.WHITE, new Position(0,0)));
        MoveResult result = engine.shoot(new Position(4,4), new Position(2,2));
        assertTrue(result.isSuccess());
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }
}