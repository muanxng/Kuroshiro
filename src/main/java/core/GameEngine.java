package core;

import pieces.Archmage;

import java.util.List;

/**
 * The core engine of the Kuroshiro game, responsible for managing the central game state.
 * This includes turn progression, move validation, ability cooldowns,
 * specialized combat actions (shooting/stabbing), and Pawn-style promotions.
 */
public class GameEngine {

    private final Board board;
    private Color currentTurn;
    private boolean gameOver;
    private Color winner;
    private int totalMoves;
    private boolean promotionPending = false;
    private core.Color promotionColor;
    private Position promotionPosition;

    /**
     * Initializes a new GameEngine with the specified board configuration.
     * By default, the game starts with the {@link Color#WHITE} player's turn.
     *
     * @param board the game board to be managed by this engine instance
     */
    public GameEngine(Board board) {
        this.board = board;
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        this.winner = null;
        this.totalMoves = 0;
    }

    /**
     * Attempts to perform a standard non-combat movement for a piece.
     * Validates turn order, piece ownership, and move legality before executing.
     *
     * @param from the starting coordinate of the piece to move
     * @param to the target destination coordinate
     * @return a {@link MoveResult} indicating a successful move, or the specific reason for failure
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
     * Executes a melee action if the acting piece implements {@link Stabbable}.
     * A successful stab instantly captures the target and moves the attacker to the target's square.
     *
     * @param shooterPos the current coordinate of the attacking piece
     * @param target the coordinate of the enemy to stab
     * @return a {@link MoveResult} detailing the success or failure of the stab action
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
     * Executes a ranged attack if the acting piece implements {@link Shootable}.
     * Unlike moving or stabbing, a successful shoot action damages/captures the target
     * without altering the attacking piece's position on the board.
     *
     * @param shooterPos the current coordinate of the shooting piece
     * @param target the coordinate of the target being shot at
     * @return a {@link MoveResult} detailing the outcome of the attack
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
     * Retrieves all legal movement destinations for a specific piece on the current board layout.
     *
     * @param piece the piece to evaluate
     * @return a list of safe, valid {@link Position} coordinates
     */
    public List<Position> getSafeMoves(Piece piece) {
        return piece.getLegalMoves(board);
    }

    /**
     * Returns the color of the player whose turn it currently is.
     *
     * @return the active turn's {@link Color}
     */
    public Color getCurrentTurn() { return currentTurn; }

    /**
     * Evaluates if the game has reached an end state.
     *
     * @return {@code true} if the game has concluded, {@code false} if it is ongoing
     */
    public boolean isGameOver()   { return gameOver; }

    /**
     * Returns the player who won the game.
     *
     * @return the winning {@link Color}, or {@code null} if the match is unresolved
     */
    public Color getWinner()      { return winner; }

    /**
     * Provides direct access to the current state of the game board.
     *
     * @return the active {@link Board} instance
     */
    public Board getBoard()       { return board; }

    /**
     * Returns the total number of actions (moves, stabs, shoots) executed in the match.
     *
     * @return the aggregate move count
     */
    public int getTotalMoves()    { return totalMoves; }

    /**
     * Processes end-of-turn mechanics.
     * This method evaluates win conditions (total annihilation) and officially
     * passes control to the opposing player, unless a promotion action is pending.
     *
     * @param captured the piece captured during the turn, or {@code null} if none
     * @return a finalized {@link MoveResult} reflecting the turn's conclusion
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
     * Cycles through all pieces owned by the current player and decrements active cooldowns
     * (e.g., reducing the Archmage's infinite-range attack timer).
     */
    private void decrementCooldowns() {
        for (Piece p : board.getPieces(currentTurn)) {
            if (p instanceof Archmage archmage)
                archmage.decrementCooldown();
        }
    }

    /**
     * Checks if a moved {@link pieces.Warrior} has reached the furthest opposite rank,
     * flagging the engine state to await a promotion selection.
     *
     * @param piece the piece that just completed a movement or stab action
     */
    private void checkPromotion(Piece piece) {
        if (!(piece instanceof pieces.Warrior)) return;
        int promotionRow = piece.getColor() == Color.WHITE ? 0 : 7;
        if (piece.getPosition().getRow() != promotionRow) return;

        promotionPending = true;
        promotionColor = piece.getColor();
        promotionPosition = piece.getPosition();
    }

    /**
     * Checks if the engine is currently halted, waiting for a player to select a promotion upgrade.
     *
     * @return {@code true} if a promotion is pending
     */
    public boolean isPromotionPending() { return promotionPending; }

    /**
     * Returns the color of the piece currently awaiting promotion.
     *
     * @return the promoting piece's {@link Color}
     */
    public Color getPromotionColor()    { return promotionColor; }

    /**
     * Returns the board coordinate where the promotion is taking place.
     *
     * @return the {@link Position} of the pending promotion
     */
    public Position getPromotionPosition() { return promotionPosition; }

    /**
     * Executes the promotion process, replacing the original piece with the selected upgrade.
     * Upon completion, it resumes standard end-of-turn logic and passes the turn.
     *
     * @param promoted the newly instantiated piece chosen by the player
     */
    public void promote(Piece promoted) {
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

    /**
     * Immediately ends the game and declares the opponent as the winner.
     */
    public void resign() {
        gameOver = true;
        winner = currentTurn.opposite();
    }
}