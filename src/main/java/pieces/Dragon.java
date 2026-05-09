package pieces;

import core.*;
import java.util.*;

/**
 * Dragon — slides in all 8 directions like a Queen.
 */
public class Dragon extends Piece {

    private static final int[][] DIRS = {
        {-1,0},{1,0},{0,-1},{0,1},
        {-1,-1},{-1,1},{1,-1},{1,1}
    };

    public Dragon(Color color, Position position) { super(color, position); }

    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : DIRS) moves.addAll(slide(dir[0], dir[1], board));
        return moves;
    }

    @Override
    public String getSymbol() { return "D"; }
}
