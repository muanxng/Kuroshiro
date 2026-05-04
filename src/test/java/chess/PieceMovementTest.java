package chess;

import chess.core.*;
import chess.pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PieceMovementTest {

    private Board board;

    @BeforeEach void setUp() { board = new Board(); }

    @Test void rookSlidesAllDirections() {
        Rook r = new Rook(Color.WHITE, new Position(4,4));
        board.placePiece(r);
        List<Position> moves = r.getLegalMoves(board);
        assertTrue(moves.contains(new Position(0,4)));
        assertTrue(moves.contains(new Position(7,4)));
        assertTrue(moves.contains(new Position(4,0)));
        assertTrue(moves.contains(new Position(4,7)));
    }

    @Test void rookBlockedByFriendly() {
        Rook r = new Rook(Color.WHITE, new Position(4,0));
        board.placePiece(r);
        board.placePiece(new Pawn(Color.WHITE, new Position(4,3)));
        assertFalse(r.getLegalMoves(board).contains(new Position(4,3)));
        assertFalse(r.getLegalMoves(board).contains(new Position(4,7)));
    }

    @Test void rookCapturesEnemy() {
        Rook r = new Rook(Color.WHITE, new Position(4,0));
        board.placePiece(r);
        board.placePiece(new Pawn(Color.BLACK, new Position(4,3)));
        assertTrue(r.getLegalMoves(board).contains(new Position(4,3)));
        assertFalse(r.getLegalMoves(board).contains(new Position(4,7)));
    }

    @Test void knightHasEightMovesFromCenter() {
        Knight n = new Knight(Color.WHITE, new Position(4,4));
        board.placePiece(n);
        assertEquals(8, n.getLegalMoves(board).size());
    }

    @Test void knightJumpsOverPieces() {
        Knight n = new Knight(Color.WHITE, new Position(4,4));
        board.placePiece(n);
        board.placePiece(new Pawn(Color.WHITE, new Position(3,4)));
        board.placePiece(new Pawn(Color.WHITE, new Position(5,4)));
        assertEquals(8, n.getLegalMoves(board).size()); // still jumps
    }

    @Test void whitePawnMovesTwoOnFirstMove() {
        Pawn p = new Pawn(Color.WHITE, new Position(6,3));
        board.placePiece(p);
        assertTrue(p.getLegalMoves(board).contains(new Position(4,3)));
    }

    @Test void pawnCannotMoveForwardIfBlocked() {
        Pawn white = new Pawn(Color.WHITE, new Position(4,3));
        board.placePiece(white);
        board.placePiece(new Pawn(Color.BLACK, new Position(3,3)));
        assertFalse(white.getLegalMoves(board).contains(new Position(3,3)));
    }

    @Test void pawnCapturesDiagonally() {
        Pawn p = new Pawn(Color.WHITE, new Position(4,3));
        board.placePiece(p);
        board.placePiece(new Pawn(Color.BLACK, new Position(3,2)));
        board.placePiece(new Pawn(Color.BLACK, new Position(3,4)));
        assertTrue(p.getLegalMoves(board).contains(new Position(3,2)));
        assertTrue(p.getLegalMoves(board).contains(new Position(3,4)));
    }

    @Test void kingMovesOneSquare() {
        King k = new King(Color.WHITE, new Position(4,4));
        board.placePiece(k);
        assertEquals(8, k.getLegalMoves(board).size());
    }

    @Test void bishopSlidesDiagonally() {
        Bishop b = new Bishop(Color.WHITE, new Position(4,4));
        board.placePiece(b);
        assertTrue(b.getLegalMoves(board).contains(new Position(0,0)));
        assertTrue(b.getLegalMoves(board).contains(new Position(7,7)));
    }
}
