package util;

import core.*;
import pieces.*;

/**
 * A utility class responsible for initializing the game board.
 * Provides factory methods to generate a fully populated board with pieces
 * arranged in their standard starting positions.
 */
public class GameSetup {

    /**
     * Creates a new game board and populates it with the standard starting
     * configuration for both the White and Black players.
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
     * Helper method that places a complete set of pieces for a single player.
     * It arranges the specialized back-row units (Archers, Assassins, Mages, Dragon, Archmage)
     * and generates a frontline of Warriors on the adjacent pawn row.
     *
     * @param board the game board being populated
     * @param color the color of the pieces being placed
     * @param backRow the row index (0 or 7) where the primary units are placed
     */
    private static void setupPieces(Board board, Color color, int backRow) {

        // Determine the frontline row based on the player's color
        int pawnRow = (color == Color.WHITE) ? backRow - 1 : backRow + 1;

        // Place the back-row specialized units
        board.placePiece(new Archer(color,   new Position(backRow, 0)));
        board.placePiece(new Assassin(color, new Position(backRow, 1)));
        board.placePiece(new Mage(color,     new Position(backRow, 2)));
        board.placePiece(new Dragon(color,   new Position(backRow, 3)));
        board.placePiece(new Archmage(color, new Position(backRow, 4)));
        board.placePiece(new Mage(color,     new Position(backRow, 5)));
        board.placePiece(new Assassin(color, new Position(backRow, 6)));
        board.placePiece(new Archer(color,   new Position(backRow, 7)));

        // Place the frontline of 8 Warriors
        for (int col = 0; col < 8; col++) {
            board.placePiece(new Warrior(color, new Position(pawnRow, col)));
        }
    }
}