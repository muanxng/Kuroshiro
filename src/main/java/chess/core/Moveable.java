package chess.core;

import java.util.List;

/**
 * Interface that every piece must implement.
 * Defines the contract for movement — each piece type computes
 * its own legal moves differently (this is where Polymorphism lives).
 */
public interface Moveable {
    List<Position> getLegalMoves(Board board);
    String getSymbol();
}
