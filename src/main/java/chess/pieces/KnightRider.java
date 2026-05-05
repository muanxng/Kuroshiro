package chess.pieces;

import chess.core.*;
import chess.core.Color;

import java.awt.*;
import java.util.*;
import java.util.List;

public class KnightRider extends Piece{

    private static final int[][] JUMPS = {
        {-2,-1},{-2,1},{-1,-2},{-1,2},
        { 1,-2},{ 1,2},{ 2,-1},{ 2,1}
    };

    public KnightRider(Color color, Position position){
        super(color, position);
    }


    @Override
    public List<Position> getLegalMoves(Board board) {
        Set<Position> moves = new HashSet<>();

        for (int[] first : JUMPS) {
            Position hop1 = position.offset(first[0], first[1]);
            if (!hop1.isValid()) continue;

            // First hop
            if (canMoveTo(hop1, board)) moves.add(hop1);

            // Second hop — only blocked by friendly pieces, not enemies
            for (int[] second : JUMPS) {
                Position hop2 = hop1.offset(second[0], second[1]);
                if (hop2.isValid() && canMoveTo(hop2, board)) {
                    moves.add(hop2);
                }
            }
        }

        return new ArrayList<>(moves);
    }

    @Override
    public String getSymbol() {return "X";}
}
