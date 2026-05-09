package pieces;

import core.*;
import java.util.*;

public class Archmage extends Piece implements Shootable{

    private static final int[][] MOVE_DIRS = {
        {-1,-1},{-1,0},{-1,1},
        { 0,-1},        { 0,1},
        { 1,-1},{ 1,0},{ 1,1}
    };

    private static final int[][] SHOOT_DIRS = {
        {-1, 0}, // up
        { 1, 0}, // down
        { 0,-1}, // left
        { 0, 1}  // right
    };

    private int magicCooldown;

    public Archmage(Color color, Position position) {
        super(color, position);
        this.magicCooldown = 0;
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
        if (!canUseMagic()) return targets;

        for (int[] dir : SHOOT_DIRS) {
            Position current = position.offset(dir[0], dir[1]);
            while (current.isValid()) {
                Piece target = board.getPieceAt(current);
                if (target != null) {
                    if (target.getColor() != this.color) targets.add(current);
                    break; // blocked by any piece
                }
                current = current.offset(dir[0], dir[1]);
            }
        }
        return targets;
    }

    public Piece shoot(Position target, Board board) {
        if (!canUseMagic()) return null;
        if (!getShootTargets(board).contains(target)) return null;
        Piece captured = board.getPieceAt(target);
        magicCooldown = 2;
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

    public boolean canUseMagic()      { return magicCooldown == 0; }
    public int getMagicCooldown()     { return magicCooldown; }

    public void decrementCooldown() {
        if (magicCooldown > 0) magicCooldown--;
    }

    @Override
    public String getSymbol() { return "Z"; }
}
