import core.*;
import pieces.*;
import util.GameSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    private Board board;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        board = new Board();
        engine = new GameEngine(board);
    }

    @Test
    void whiteFirstTest() {
        assertEquals(Color.WHITE, engine.getCurrentTurn());
    }

    @Test
    void alternatingTurnTest() {
        board.placePiece(new Warrior(Color.WHITE, new Position(6,0)));
        board.placePiece(new Archmage(Color.BLACK, new Position(0,4)));
        engine.makeMove(new Position(6,0), new Position(5,0));
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    @Test
    void opponentPieceTest() {
        board.placePiece(new Warrior(Color.BLACK, new Position(1,0)));
        assertEquals(MoveResult.Status.WRONG_TURN,
            engine.makeMove(new Position(1,0), new Position(2,0)).getStatus());
    }

    @Test
    void returnedCapturedPieceTest() {
        board.placePiece(new Dragon(Color.WHITE, new Position(4,0)));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,5));
        enemy.takeDamage();
        board.placePiece(enemy);
        MoveResult result = engine.makeMove(new Position(4,0), new Position(4,5));
        assertTrue(result.isSuccess());
        assertNotNull(result.getCapturedPiece());
    }

    @Test
    void gameEndTest() {
        board.placePiece(new Dragon(Color.WHITE, new Position(4,0)));
        Warrior enemy = new Warrior(Color.BLACK, new Position(4,5));
        enemy.takeDamage();
        board.placePiece(enemy);
        engine.makeMove(new Position(4,0), new Position(4,5));
        assertTrue(engine.isGameOver());
        assertEquals(Color.WHITE, engine.getWinner());
    }

    @Test
    void totalMovesTest() {
        board.placePiece(new Warrior(Color.WHITE, new Position(6,0)));
        board.placePiece(new Warrior(Color.BLACK, new Position(1,0)));
        engine.makeMove(new Position(6,0), new Position(5,0));
        assertEquals(1, engine.getTotalMoves());
        engine.makeMove(new Position(1,0), new Position(2,0));
        assertEquals(2, engine.getTotalMoves());
    }

    @Test
    void mageShootTest() {
        board.placePiece(new Mage(Color.WHITE, new Position(4,4)));
        board.placePiece(new Warrior(Color.BLACK, new Position(4,6)));
        board.placePiece(new Warrior(Color.WHITE, new Position(0,0))); // keep white alive
        MoveResult result = engine.shootMagic(new Position(4,4), new Position(4,6));
        assertTrue(result.isSuccess());
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    @Test
    void archmageShootTest() {
        board.placePiece(new Archmage(Color.WHITE, new Position(7,4)));
        board.placePiece(new Warrior(Color.BLACK, new Position(0,4)));
        board.placePiece(new Warrior(Color.WHITE, new Position(0,0)));
        MoveResult result = engine.archmageShoot(new Position(7,4), new Position(0,4));
        assertTrue(result.isSuccess());
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }

    @Test
    void archerShootTest() {
        board.placePiece(new Archer(Color.WHITE, new Position(4,4)));
        board.placePiece(new Warrior(Color.BLACK, new Position(2,2)));
        board.placePiece(new Warrior(Color.WHITE, new Position(0,0)));
        MoveResult result = engine.archerShoot(new Position(4,4), new Position(2,2));
        assertTrue(result.isSuccess());
        assertEquals(Color.BLACK, engine.getCurrentTurn());
    }
}
