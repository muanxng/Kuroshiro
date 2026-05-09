package core;

import java.util.List;

public interface Moveable {
    List<Position> getLegalMoves(Board board);
    String getSymbol();
}
