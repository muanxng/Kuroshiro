package pieces;

import core.*;
import java.util.*;

public class Warrior extends Piece {

    private int lives;

    public Warrior(Color color, Position position) {
        super(color, position);
        this.lives = 2;
    }

    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int dir = (color == Color.WHITE) ? -1 : 1;

        Position one = position.offset(dir, 0);
        if (one.isValid() && isEmpty(one, board)) {
            moves.add(one);

            if (!hasMoved) {
                Position two = position.offset(dir * 2, 0);
                if (isEmpty(two, board)) moves.add(two);
            }
        }

        for (int dc : new int[]{-1, 1}) {
            Position capture = position.offset(dir, dc);
            if (capture.isValid() && isEnemy(capture, board)) {
                moves.add(capture);
            }
        }

        return moves;
    }

    public boolean takeDamage() {
        lives--;
        return lives <= 0;
    }

    public int getLives() { return lives; }
    public boolean isAlive() { return lives > 0; }

    @Override
    public String getSymbol() { return lives == 2 ? "W" : "w"; }
}
