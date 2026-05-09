package pieces;

import core.*;
import java.util.*;

public class Dragon extends Piece implements Stabbable{

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
    public Piece stab(Position target, Board board) {
        Piece captured = board.getPieceAt(target);
        board.removePiece(target);
        return captured;
    }

    @Override
    public String getSymbol() { return "D"; }
}
