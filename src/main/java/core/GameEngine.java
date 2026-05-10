package core;

import pieces.Archmage;

import java.util.List;

/**
 * The core engine of the game responsible for managing game state, turn progression,
 * move validation, and specific combat actions like shooting and stabbing.
 */
public class GameEngine {

    private final Board board;
    private Color currentTurn;
    private boolean gameOver;
    private Color winner;
    private int totalMoves;
    private java.util.function.Function<Color, Piece> promotionCallback;
    private boolean promotionPending = false;
    private core.Color promotionColor;
    private Position promotionPosition;

    /**
     * Initializes a new GameEngine with the specified board.
     * The game starts with the WHITE player's turn.
     *
     * @param board the game board to be used for this engine instance
     */
    public GameEngine(Board board) {
        this.board = board;
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        this.winner = null;
        this.totalMoves = 0;
    }

    /**
     * Attempts to perform a standard move for a piece from one position to another.
     * * @param from the starting position of the piece to move
     * @param to the destination position
     * @return a {@link MoveResult} indicating success or the specific reason for failure
     */
    public MoveResult makeMove(Position from, Position to) {
        if (gameOver) return MoveResult.failure(MoveResult.Status.GAME_OVER);

        Piece piece = board.getPieceAt(from);
        Piece target = board.getPieceAt(to);

        if (piece == null) return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        if (piece.getColor() != currentTurn) return MoveResult.failure(MoveResult.Status.WRONG_TURN);

        List<Position> legalMoves = getSafeMoves(piece);
        if (!legalMoves.contains(to)) return MoveResult.failure(MoveResult.Status.INVALID_MOVE);

        Piece captured = board.movePiece(piece, to);
        checkPromotion(piece);

        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    /**
     * Executes a melee stab action if the piece at the starting position implements {@link Stabbable}.
     * This moves the attacking piece to the target position upon a successful stab.
     *
     * @param shooterPos the current position of the attacking piece
     * @param target the target position to stab
     * @return a {@link MoveResult} detailing the outcome of the action
     */
    public MoveResult stab(Position shooterPos, Position target) {

        if (gameOver) {
            return MoveResult.failure(MoveResult.Status.GAME_OVER);
        }

        Piece piece = board.getPieceAt(shooterPos);

        if (!(piece instanceof Stabbable stabbable)) {
            return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        }

        if (piece.getColor() != currentTurn) {
            return MoveResult.failure(MoveResult.Status.WRONG_TURN);
        }

        Piece captured = stabbable.stab(target, board);

        if (captured == null) {
            return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
        }

        board.movePiece(piece, target);
        checkPromotion(piece);
        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    /**
     * Executes a ranged shoot action if the piece at the starting position implements {@link Shootable}.
     * A shooting attack does not move the attacking piece.
     *
     * @param shooterPos the current position of the shooting piece
     * @param target the targeted position to shoot at
     * @return a {@link MoveResult} detailing the outcome of the action
     */
    public MoveResult shoot(Position shooterPos, Position target) {

        if (gameOver) {
            return MoveResult.failure(MoveResult.Status.GAME_OVER);
        }

        Piece piece = board.getPieceAt(shooterPos);

        if (!(piece instanceof Shootable shootable)) {
            return MoveResult.failure(MoveResult.Status.NO_PIECE_SELECTED);
        }

        if (piece.getColor() != currentTurn) {
            return MoveResult.failure(MoveResult.Status.WRONG_TURN);
        }

        Piece captured = shootable.shoot(target, board);

        if (captured == null) {
            return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
        }

        decrementCooldowns();
        totalMoves++;

        return finishTurn(captured);
    }

    /**
     * Retrieves all valid and safe moves for a given piece on the current board.
     *
     * @param piece the piece to check
     * @return a list of legal destination positions
     */
    public List<Position> getSafeMoves(Piece piece) {
        return piece.getLegalMoves(board);
    }

    /**
     * Gets the color of the player whose turn it currently is.
     *
     * @return the current turn's color
     */
    public Color getCurrentTurn() { return currentTurn; }

    /**
     * Checks if the game has concluded.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver()   { return gameOver; }

    /**
     * Gets the winning color, if the game is over.
     *
     * @return the winning color, or null if the game is still ongoing
     */
    public Color getWinner()      { return winner; }

    /**
     * Retrieves the current game board.
     *
     * @return the board instance
     */
    public Board getBoard()       { return board; }

    /**
     * Gets the total number of valid moves or actions made in the game so far.
     *
     * @return the total move count
     */
    public int getTotalMoves()    { return totalMoves; }

    /**
     * Handles end-of-turn logic, including checking for win conditions
     * and passing the turn to the opposing player.
     *
     * @param captured the piece that was captured during the turn, if any
     * @return a successful {@link MoveResult} containing the captured piece and game over status
     */
    private MoveResult finishTurn(Piece captured) {
        Color opponent = currentTurn.opposite();
        if (!promotionPending) {
            if (board.getPieces(opponent).isEmpty()) {
                gameOver = true;
                winner = currentTurn;
            }
            currentTurn = opponent;
        }
        return MoveResult.success(captured, gameOver);
    }

    /**
     * Decrements ability cooldowns for pieces belonging to the current player.
     */
    private void decrementCooldowns() {
        for (Piece p : board.getPieces(currentTurn)) {
            if (p instanceof Archmage archmage)
                archmage.decrementCooldown();
        }
    }

    private void checkPromotion(Piece piece) {
        if (!(piece instanceof pieces.Warrior)) return;
        int promotionRow = piece.getColor() == Color.WHITE ? 0 : 7;
        if (piece.getPosition().getRow() != promotionRow) return;

        // Don't remove yet — just flag as pending
        promotionPending = true;
        promotionColor = piece.getColor();
        promotionPosition = piece.getPosition();
    }

    public boolean isPromotionPending() { return promotionPending; }
    public Color getPromotionColor()    { return promotionColor; }
    public Position getPromotionPosition() { return promotionPosition; }

    public void promote(Piece promoted) {
        // Remove the warrior now that player has chosen
        board.removePiece(promotionPosition);
        board.placePiece(promoted);
        promotionPending = false;
        promotionColor = null;
        promotionPosition = null;

        Color opponent = currentTurn.opposite();
        if (board.getPieces(opponent).isEmpty()) {
            gameOver = true;
            winner = currentTurn;
        }
        currentTurn = opponent;
    }

    public void setPromotionCallback(java.util.function.Function<Color, Piece> callback) {
        this.promotionCallback = callback;
    }

    private Piece createPromotedPiece(Piece choice, Color color, Position position) {
        if (choice instanceof pieces.Mage)     return new pieces.Mage(color, position);
        if (choice instanceof pieces.Assassin) return new pieces.Assassin(color, position);
        if (choice instanceof pieces.Archer)   return new pieces.Archer(color, position);
        return new pieces.Dragon(color, position); // default
    }
}