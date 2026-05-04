package chess.pieces;
import chess.core.*;
import java.util.*;
public class Pawn extends Piece {
    public Pawn(Color color, Position position) { super(color, position); }
    @Override public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int dir = (color == Color.WHITE) ? -1 : 1;
        Position one = position.offset(dir, 0);
        if (one.isValid() && isEmpty(one, board)) {
            moves.add(one);
            if (!hasMoved) { Position two = position.offset(dir*2,0); if (isEmpty(two,board)) moves.add(two); }
        }
        for (int dc : new int[]{-1,1}) { Position cap = position.offset(dir,dc); if (cap.isValid() && isEnemy(cap,board)) moves.add(cap); }
        return moves;
    }
    @Override public String getSymbol() { return "P"; }
}
