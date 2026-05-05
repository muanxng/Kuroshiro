package chess.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all chess pieces.
 *
 * WHY INHERITANCE: Every piece shares color, position, hasMoved state,
 * and helper methods like slide(), canMoveTo(). Subclasses only override
 * getLegalMoves() with their unique movement rules.
 */
public abstract class Piece implements Moveable {

    protected Color color;
    protected Position position;
    protected boolean hasMoved;

    public Piece(Color color, Position position) {
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    public Color getColor() { return color; }
    public Position getPosition() { return position; }
    public boolean hasMoved() { return hasMoved; }

    public void setPosition(Position position) {
        this.position = position;
        this.hasMoved = true;
    }

    // ── Helpers for subclasses ───────────────────────────────────────────────

    protected boolean canMoveTo(Position pos, Board board) {
        if (!pos.isValid()) return false;
        Piece occupant = board.getPieceAt(pos);
        return occupant == null || occupant.getColor() != this.color;
    }

    protected boolean isEnemy(Position pos, Board board) {
        Piece occupant = board.getPieceAt(pos);
        return occupant != null && occupant.getColor() != this.color;
    }

    protected boolean isEmpty(Position pos, Board board) {
        return pos.isValid() && board.getPieceAt(pos) == null;
    }

    /**
     * Slides in direction (dr, dc) until blocked — used by Rook, Bishop, Queen
     * and any custom sliding pieces you create.
     */
    protected List<Position> slide(int dr, int dc, Board board) {
        List<Position> moves = new ArrayList<>();
        Position current = position.offset(dr, dc);
        while (current.isValid()) {
            if (isEmpty(current, board)) {
                moves.add(current);
            } else {
                if (isEnemy(current, board)) moves.add(current);
                break;
            }
            current = current.offset(dr, dc);
        }
        return moves;
    }

    public void resetHasMoved(boolean value) {
        this.hasMoved = value;
    }

    @Override
    public abstract List<Position> getLegalMoves(Board board);

    @Override
    public abstract String getSymbol();

    @Override
    public String toString() {
        return color + " " + getClass().getSimpleName() + " at " + position;
    }
}
