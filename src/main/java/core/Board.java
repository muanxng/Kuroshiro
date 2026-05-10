package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 8x8 game board for the Kuroshiro engine.
 * The board manages the spatial representation of all pieces, handling
 * placement, movement, and retrieval of pieces based on their {@link Position}.
 * The underlying coordinate system relies on a 2D grid array.
 */
public class Board {

    /** The 2D grid storing the layout of the pieces on the board. */
    private final Piece[][] grid;

    /**
     * Initializes a new, completely empty 8x8 game board.
     */
    public Board() {
        grid = new Piece[8][8];
    }

    /**
     * Places a given piece onto the board at its currently assigned position.
     *
     * @param piece the piece to place on the grid
     */
    public void placePiece(Piece piece) {
        Position position = piece.getPosition();
        grid[position.getRow()][position.getCol()] = piece;
    }

    /**
     * Removes a piece from the specified position on the board, leaving the square empty.
     *
     * @param position the board coordinate to clear
     */
    public void removePiece(Position position) {
        grid[position.getRow()][position.getCol()] = null;
    }

    /**
     * Retrieves the piece currently occupying the specified position.
     *
     * @param position the target board coordinate to check
     * @return the {@link Piece} at the given position, or {@code null} if the square is empty
     * or if the requested position falls outside the bounds of the board
     */
    public Piece getPieceAt(Position position) {
        if (!position.isValid()) {
            return null;
        }

        return grid[position.getRow()][position.getCol()];
    }

    /**
     * Checks whether a specific board position is currently unoccupied.
     *
     * @param position the board coordinate to evaluate
     * @return {@code true} if there is no piece at the given position, {@code false} otherwise
     */
    public boolean isEmpty(Position position) {
        return getPieceAt(position) == null;
    }

    /**
     * Moves a piece from its current location to a new destination on the board.
     * This method handles both updating the board's internal grid and updating the
     * piece's internal position state. If another piece occupies the destination,
     * it is overwritten (captured).
     *
     * @param piece the piece to be moved
     * @param destination the target position to move the piece to
     * @return the captured {@link Piece} if the destination was occupied, or {@code null} if it was empty
     */
    public Piece movePiece(Piece piece, Position destination) {
        Piece capturedPiece = getPieceAt(destination);

        removePiece(piece.getPosition());

        piece.setPosition(destination);

        placePiece(piece);

        return capturedPiece;
    }

    /**
     * Retrieves a list of all active pieces on the board belonging to a specific player.
     *
     * @param color the {@link Color} (team) to query
     * @return a list containing all currently active pieces of the specified color
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