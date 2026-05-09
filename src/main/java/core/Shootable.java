package core;

import java.util.List;

public interface Shootable {
    Piece shoot(Position target, Board board);
    List<Position> getTargets(Board board);
}