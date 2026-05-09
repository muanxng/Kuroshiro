package util;

import core.*;
import pieces.*;
import core.Board;
import core.Color;
import core.Position;
import pieces.*;

public class GameSetup {

    public static Board createStandardBoard() {
        Board board = new Board();
        setupPieces(board, Color.BLACK, 0);
        setupPieces(board, Color.WHITE, 7);
        return board;
    }

    private static void setupPieces(Board board, Color color, int backRow) {
        int pawnRow = (color == Color.WHITE) ? backRow - 1 : backRow + 1;
        board.placePiece(new Archer(color,   new Position(backRow, 0)));
        board.placePiece(new Assassin(color, new Position(backRow, 1)));
        board.placePiece(new Mage(color, new Position(backRow, 2)));
        board.placePiece(new Dragon(color,  new Position(backRow, 3)));
        board.placePiece(new Archmage(color,   new Position(backRow, 4)));
        board.placePiece(new Mage(color, new Position(backRow, 5)));
        board.placePiece(new Assassin(color, new Position(backRow, 6)));
        board.placePiece(new Archer(color,   new Position(backRow, 7)));
        for (int col = 0; col < 8; col++)
            board.placePiece(new Warrior(color, new Position(pawnRow, col)));
    }
}
