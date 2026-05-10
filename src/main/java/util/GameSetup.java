package util;

import core.*;
import pieces.*;

/**
 * A utility class responsible for initializing the Kuroshiro game board.
 * This class provides static factory methods to generate a fully populated {@link Board}
 * with all custom pieces arranged in their specific starting positions.
 */
public class GameSetup {

    /**
     * Creates a newly instantiated game board and populates it with the standard
     * starting configuration for both the {@link Color#WHITE} and {@link Color#BLACK} players.
     * Black pieces are initialized at the top (rows 0 and 1), and White pieces
     * are initialized at the bottom (rows 6 and 7).
     *
     * @return a fully initialized {@link Board} ready for gameplay
     */
    public static Board createStandardBoard() {
        Board board = new Board();

        // Setup Black pieces on the top of the board (row 0)
        setupPieces(board, Color.BLACK, 0);

        // Setup White pieces on the bottom of the board (row 7)
        setupPieces(board, Color.WHITE, 7);

        return board;
    }

    /**
     * A helper method that places a complete set of pieces for a single player.
     * It arranges the specialized back-row units and generates a protective frontline
     * of {@link Warrior}s on the adjacent pawn row.
     * <p>
     * The specific back-row layout from left to right (columns 0 to 7) is:
     * {@link Assassin}, {@link Archer}, {@link Mage}, {@link Dragon},
     * {@link Archmage}, {@link Mage}, {@link Archer}, {@link Assassin}.
     *
     * @param board the game {@link Board} being populated
     * @param color the {@link Color} of the pieces being placed
     * @param backRow the row index (0 for Black, 7 for White) where the primary units are placed
     */
    private static void setupPieces(Board board, Color color, int backRow) {

        // Determine the frontline row based on the player's color
        int pawnRow = (color == Color.WHITE) ? backRow - 1 : backRow + 1;

        // Place the back-row specialized units
        board.placePiece(new Assassin(color, new Position(backRow, 0)));
        board.placePiece(new Archer(color,   new Position(backRow, 1)));
        board.placePiece(new Mage(color,     new Position(backRow, 2)));
        board.placePiece(new Dragon(color,   new Position(backRow, 3)));
        board.placePiece(new Archmage(color, new Position(backRow, 4)));
        board.placePiece(new Mage(color,     new Position(backRow, 5)));
        board.placePiece(new Archer(color,   new Position(backRow, 6)));
        board.placePiece(new Assassin(color, new Position(backRow, 7)));

        // Place the frontline of 8 Warriors
        for (int col = 0; col < 8; col++) {
            board.placePiece(new Warrior(color, new Position(pawnRow, col)));
        }
    }
}