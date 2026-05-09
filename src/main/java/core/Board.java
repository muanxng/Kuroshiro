package core;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Piece[][] grid;

    public Board() { grid = new Piece[8][8]; }

    public void placePiece(Piece piece) {
        Position position = piece.getPosition();
        grid[position.getRow()][position.getCol()] = piece;
    }

    /**
     * Removes a piece from the board.
     *
     * @param position the position to clear
     */
    public void removePiece(Position position) {
        grid[position.getRow()][position.getCol()] = null;
    }

    /**
     * Returns the piece located at a position.
     *
     * @param position the target position
     * @return the piece at the position or null if empty
     */
    public Piece getPieceAt(Position position) {
        if (!position.isValid()) {
            return null;
        }

        return grid[position.getRow()][position.getCol()];
    }

    /**
     * Checks whether a board position is empty.
     *
     * @param position the position to check
     * @return true if empty
     */
    public boolean isEmpty(Position position) {
        return getPieceAt(position) == null;
    }

    /**
     * Moves a piece to a new destination.
     *
     * @param piece the piece to move
     * @param destination the destination position
     * @return the captured piece if one exists, otherwise null
     */
    public Piece movePiece(Piece piece, Position destination) {
        Piece capturedPiece = getPieceAt(destination);

        removePiece(piece.getPosition());

        piece.setPosition(destination);

        placePiece(piece);

        return capturedPiece;
    }

    /**
     * Retrieves all pieces belonging to a specific color.
     *
     * @param color the target color
     * @return list of matching pieces
     */
    public List<Piece> getPieces(Color color) {
        List<Piece> pieces = new ArrayList<>();

        for (Piece[] row : grid) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() == color) {
                    pieces.add(piece);
                }
            }
        }

        return pieces;
    }
}
