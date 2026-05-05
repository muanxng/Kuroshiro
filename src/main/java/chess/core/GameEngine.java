package chess.core;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    private final Board board;
    private Color currentTurn;
    private boolean gameOver;
    private Color winner;

    public GameEngine(Board board) {
        this.board = board;
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        this.winner = null;
    }

    public MoveResult makeMove(Position from, Position to) {
        if (gameOver) return MoveResult.failure(MoveResult.Status.GAME_OVER);

        Piece piece = board.getPieceAt(from);
        if (piece == null) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        List<Position> legalMoves = getSafeMoves(piece);
        if (!legalMoves.contains(to)) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

        Piece captured = board.movePiece(piece, to);

        Color opponent = currentTurn.opposite();
        boolean opponentInCheck = isInCheck(opponent);
        boolean opponentCheckmate = opponentInCheck && getAllSafeMoves(opponent).isEmpty();
        boolean stalemate = !opponentInCheck && getAllSafeMoves(opponent).isEmpty();

        if (opponentCheckmate) { gameOver = true; winner = currentTurn; }
        else if (stalemate)    { gameOver = true; winner = null; }

        currentTurn = opponent;
        return MoveResult.success(captured, opponentInCheck, opponentCheckmate, stalemate);
    }

    public List<Position> getSafeMoves(Piece piece) {
        List<Position> safe = new ArrayList<>();
        for (Position candidate : piece.getLegalMoves(board))
            if (!wouldLeaveKingInCheck(piece, candidate)) safe.add(candidate);
        return safe;
    }

    public boolean isInCheck(Color color) {
        Piece king = board.findPiece(chess.pieces.King.class, color);
        if (king == null) return false;
        return board.isUnderAttack(king.getPosition(), color.opposite());
    }

    public Color getCurrentTurn() { return currentTurn; }
    public boolean isGameOver()   { return gameOver; }
    public Color getWinner()      { return winner; }
    public Board getBoard()       { return board; }

    private boolean wouldLeaveKingInCheck(Piece piece, Position destination) {
        Position originalPos = piece.getPosition();
        boolean originalHasMoved = piece.hasMoved();
        Piece captured = board.getPieceAt(destination);

        board.removePiece(originalPos);
        board.removePiece(destination);
        piece.setPosition(destination);
        board.placePiece(piece);

        boolean inCheck = isInCheck(piece.getColor());

        board.removePiece(destination);
        piece.setPosition(originalPos);
        piece.resetHasMoved(originalHasMoved);
        board.placePiece(piece);
        if (captured != null) board.placePiece(captured);

        return inCheck;
    }

    private List<Position> getAllSafeMoves(Color color) {
        List<Position> all = new ArrayList<>();
        for (Piece piece : board.getPieces(color)) all.addAll(getSafeMoves(piece));
        return all;
    }
}
