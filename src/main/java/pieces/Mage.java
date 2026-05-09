package pieces;

import core.*;
import java.util.*;

public class Mage extends Piece implements Shootable{

    private static final int[][] MOVE_DIRS = {
        {-1,-1},{-1,0},{-1,1},
        { 0,-1},        { 0,1},
        { 1,-1},{ 1,0},{ 1,1}
    };

    private static final int[][] SHOOT_DIRS = {
        {-1, 0}, {1, 0}, {0,-1}, {0, 1}
    };

    private static final int SHOOT_RANGE = 4;

    public Mage(Color color, Position position) {
        super(color, position);
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

    public List<Position> getShootTargets(Board board) {
        List<Position> targets = new ArrayList<>();
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

    public Piece shoot(Position target, Board board) {
        if (!getShootTargets(board).contains(target)) return null;
        Piece captured = board.getPieceAt(target);
        if (captured instanceof Warrior warrior) {
            if (warrior.takeDamage()) board.removePiece(target);
        }
        else board.removePiece(target);
        return captured;
    }

    @Override
    public List<Position> getTargets(Board board) {
        return getShootTargets(board);
    }

    @Override
    public String getSymbol() { return "M"; }
}
