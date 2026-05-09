package core;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Piece[][] grid;

    public Board() { grid = new Piece[8][8]; }

    public void placePiece(Piece piece) {
        Position pos = piece.getPosition();
        grid[pos.getRow()][pos.getCol()] = piece;
    }

    public void removePiece(Position pos) {
        grid[pos.getRow()][pos.getCol()] = null;
    }

    public Piece getPieceAt(Position pos) {
        if (!pos.isValid()) return null;
        return grid[pos.getRow()][pos.getCol()];
    }

    public boolean isEmpty(Position pos) { return getPieceAt(pos) == null; }

    public Piece movePiece(Piece piece, Position destination) {
        Piece captured = getPieceAt(destination);
        removePiece(piece.getPosition());
        piece.setPosition(destination);
        placePiece(piece);
        return captured;
    }

    public List<Piece> getPieces(Color color) {
        List<Piece> result = new ArrayList<>();
        for (Piece[] row : grid)
            for (Piece p : row)
                if (p != null && p.getColor() == color) result.add(p);
        return result;
    }
}
