package chess.pieces;
import chess.core.*;
import java.util.*;
public class Knight extends Piece {
    private static final int[][] JUMPS = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
    public Knight(Color color, Position position) { super(color, position); }
    @Override public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] j : JUMPS) { Position p = position.offset(j[0],j[1]); if (canMoveTo(p,board)) moves.add(p); }
        return moves;
    }
    @Override public String getSymbol() { return "N"; }
}
