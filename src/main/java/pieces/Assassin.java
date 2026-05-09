package pieces;

import core.*;
import java.util.*;

/**
 * Assassin — jumps diagonally up to 4 tiles in any diagonal direction.
 * Can jump over any pieces like a Knight.
 * Captures by landing on an enemy piece.
 */
public class Assassin extends Piece implements Stabbable{

    private static final int[][] DIAGONAL_DIRS = {
        {-1,-1},{-1,1},{1,-1},{1,1}
    };

    private static final int JUMP_RANGE = 4;

    public Assassin(Color color, Position position) { super(color, position); }

    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : DIAGONAL_DIRS) {
            for (int i = 1; i <= JUMP_RANGE; i++) {
                Position candidate = position.offset(dir[0] * i, dir[1] * i);
                if (!candidate.isValid()) break;
                // Can land on empty or enemy squares (jumps over everything)
                if (canMoveTo(candidate, board)) moves.add(candidate);
            }
        }
        return moves;
    }

    @Override
    public Piece stab(Position target, Board board) {
        Piece captured = board.getPieceAt(target);
        board.removePiece(target);
        return captured;
    }

    @Override
    public String getSymbol() { return "S"; }
}
