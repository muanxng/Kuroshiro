package chess.pieces;

import chess.core.*;
import java.util.*;

public class King extends Piece {
    private static final int[][] DIRS = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
    public King(Color color, Position position) { super(color, position); }
    @Override public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] d : DIRS) { Position p = position.offset(d[0],d[1]); if (canMoveTo(p,board)) moves.add(p); }
        return moves;
    }
    @Override public String getSymbol() { return "K"; }
}
