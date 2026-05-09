package pieces;

import core.*;
import java.util.*;

/**
 * Mage — moves one square in any direction.
 * Shoots magic in 4 directions (up, down, left, right), up to 4 tiles.
 * Kills the first enemy it hits. Blocked by allies. No cooldown.
 */
public class Mage extends Piece {

    private static final int[][] MOVE_DIRS = {
        {-1,-1},{-1,0},{-1,1},
        { 0,-1},        { 0,1},
        { 1,-1},{ 1,0},{ 1,1}
    };

    private static final int[][] SHOOT_DIRS = {
        {-1, 0}, {1, 0}, {0,-1}, {0, 1}
    };

    private static final int SHOOT_RANGE = 4;
    private int magicCharges;

    public Mage(Color color, Position position) {
        super(color, position);
        this.magicCharges = 3;
    }

    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : MOVE_DIRS) {
            Position candidate = position.offset(dir[0], dir[1]);
            if (candidate.isValid() && isEmpty(candidate, board)) moves.add(candidate);
        }
        return moves;
    }

    public List<Position> getMagicTargets(Board board) {
        List<Position> targets = new ArrayList<>();
        if (!canUseMagic()) return targets;
        for (int[] dir : SHOOT_DIRS) {
            for (int i = 1; i <= SHOOT_RANGE; i++) {
                Position current = position.offset(dir[0] * i, dir[1] * i);
                if (!current.isValid()) break;
                Piece target = board.getPieceAt(current);
                if (target != null) {
                    if (target.getColor() != this.color) targets.add(current);
                    break;
                }
            }
        }
        return targets;
    }

    public Piece shootMagic(Position target, Board board) {
        if (!canUseMagic()) return null;
        if (!getMagicTargets(board).contains(target)) return null;
        Piece captured = board.getPieceAt(target);
        board.removePiece(target);
        magicCharges--;
        return captured;
    }

    public boolean canUseMagic() { return magicCharges > 0; }
    public int getMagicCharges() { return magicCharges; }

    @Override
    public String getSymbol() { return "M"; }
}
