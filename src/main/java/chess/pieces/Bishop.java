package chess.pieces;
import chess.core.*;
import java.util.*;
public class Bishop extends Piece {
    private static final int[][] DIRS = {{-1,-1},{-1,1},{1,-1},{1,1}};
    public Bishop(Color color, Position position) { super(color, position); }
    @Override public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] d : DIRS) moves.addAll(slide(d[0],d[1],board));
        return moves;
    }
    @Override public String getSymbol() { return "B"; }
}
