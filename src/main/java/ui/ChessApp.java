package ui;

import core.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import util.GameSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The primary graphical user interface for the chess game.
 * This class handles window management, rendering the board and pieces,
 * loading audio/visual assets, and processing user input via mouse clicks.
 */
public class ChessApp extends Application {

    private static final int BOARD_SIZE = 8;

    private double TILE = 80;

    // UI Color Palette
    private static final Color LIGHT = Color.rgb(242, 242, 242);
    private static final Color DARK = Color.rgb(176, 196, 210);
    private static final Color SELECT = Color.rgb(255, 255, 0, 0.6);
    private static final Color MOVE_HIGHLIGHT = Color.rgb(50, 200, 50, 0.5);
    private static final Color SHOOT_HIGHLIGHT = Color.rgb(255, 140, 0, 0.6);

    private final Map<String, Image> pieceImages = new HashMap<>();

    // Audio assets
    private AudioClip captureSound;
    private AudioClip moveSound;
    private AudioClip gameOverSound;

    // UI Components
    private Button newGameButton;
    private Button resignButton;
    private GameEngine engine;
    private Canvas canvas;
    private Label statusLabel;

    // State management for user interaction
    private Position selectedPosition;
    private List<Position> legalMoves;
    private List<Position> shootTargets;

    /**
     * The main entry point for the JavaFX application.
     * Initializes the game engine, sets up the UI layout, loads assets,
     * and configures event listeners for the stage and scene.
     *
     * @param stage the primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        engine = new GameEngine(GameSetup.createStandardBoard());

        canvas = new Canvas(TILE * BOARD_SIZE, TILE * BOARD_SIZE);
        canvas.setOnMouseClicked(event -> handleClick((int) (event.getX() / TILE), (int) (event.getY() / TILE)));

        statusLabel = createStatusLabel();

        newGameButton = new Button("New Game");
        newGameButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        newGameButton.setVisible(false);
        newGameButton.setManaged(false);
        newGameButton.setOnAction(e -> resetGame());
        newGameButton.setStyle("-fx-background-color: rgba(50, 140, 50, 0.8); -fx-text-fill: black; -fx-background-radius: 8;");

        resignButton = new Button("Resign");
        resignButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        resignButton.setVisible(true);
        resignButton.setManaged(true);
        resignButton.setOnAction(e -> resign());
        resignButton.setStyle("-fx-background-color: rgba(180, 50, 50, 0.8); -fx-text-fill: black; -fx-background-radius: 8;");


        Label fullScreenLabel = new Label("Press F11 to toggle fullscreen on/off");
        fullScreenLabel.setStyle("-fx-background-color: rgba(176, 196, 210, 0.8); -fx-text-fill: black; -fx-background-radius: 8;");
        fullScreenLabel.setPadding(new Insets(5, 10, 5, 10));

        VBox root = new VBox(canvas,statusLabel,resignButton,newGameButton,fullScreenLabel);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #312e2b; " +
                "-fx-background-image: url('/images/background.png'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center; " +
                "-fx-background-repeat: no-repeat;");

        loadImages();
        loadSounds();

        Scene scene = new Scene(root);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Press F11 to exit fullscreen");

        scene.widthProperty().addListener((obs, oldVal, newVal) -> resizeBoard(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> resizeBoard(scene));

        stage.show();
        stage.setMinWidth(500);
        stage.setMinHeight(600);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        resizeBoard(scene);
    }

    /**
     * Dynamically resizes the game board canvas to fit within the current window dimensions.
     *
     * @param scene the active game scene containing the canvas
     */
    private void resizeBoard(Scene scene) {
        double available = Math.min(
                scene.getWidth() - 40,
                scene.getHeight() - 100
        );

        available = Math.max(available, 400); // minimum board size of 400px

        TILE = available / BOARD_SIZE;

        canvas.setWidth(TILE * BOARD_SIZE);
        canvas.setHeight(TILE * BOARD_SIZE);

        draw();
    }

    /**
     * Creates and configures the label used to display turn information and game status.
     *
     * @return the configured status label
     */
    private Label createStatusLabel() {
        Label label = new Label("White's turn");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.WHITE);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10));
        label.setStyle("-fx-background-color: rgba(176, 196, 210, 0.8); -fx-background-radius: 8;");
        return label;
    }

    /**
     * Loads piece images from the resources folder into the image cache.
     * Silently catches and logs exceptions if specific images are missing.
     */
    private void loadImages() {
        String[] colors = {"white", "black"};
        String[] pieces = {
                "archmage", "dragon", "archer",
                "mage", "assassin", "warrior"
        };

        for (String color : colors) {
            for (String piece : pieces) {
                String key = color + "_" + piece;

                try {
                    Image image = new Image(
                            getClass().getResourceAsStream("/images/" + key + ".png")
                    );
                    pieceImages.put(key, image);
                } catch (Exception ignored) {
                    System.out.println("Missing image: " + key);
                }
            }
        }
        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/images/white_warrior_damaged.png")
            );
            pieceImages.put("white_warrior_damaged", image);
        } catch (Exception ignored) {
            System.out.println("Missing image: white_warrior_damaged");
        }

        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/images/black_warrior_damaged.png")
            );
            pieceImages.put("black_warrior_damaged", image);
        } catch (Exception ignored) {
            System.out.println("Missing image: black_warrior_damaged");
        }
    }

    /**
     * Loads sound effect files from the resources folder.
     * Logs missing files to the console instead of crashing the application.
     */
    private void loadSounds() {
        try {
            captureSound = new AudioClip(getClass().getResource("/sounds/capture_sound.mp3").toExternalForm());
        } catch (Exception e) {
            System.out.println("capture_sound.mp3 not found");
        }
        try {
            moveSound = new AudioClip(getClass().getResource("/sounds/move_sound.mp3").toExternalForm());
        } catch (Exception e) {
            System.out.println("move_sound.mp3 not found");
        }
        try {
            gameOverSound = new AudioClip(getClass().getResource("/sounds/game_over_sound.mp3").toExternalForm());
        } catch (Exception e) {
            System.out.println("game_over_sound.mp3 not found");
        }
    }

    /**
     * The main rendering loop. Clears the canvas and redraws the tiles,
     * highlights, coordinates, and pieces based on the current engine state.
     */
    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Board board = engine.getBoard();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {

                double x = col * TILE;
                double y = row * TILE;

                Position position = new Position(row, col);

                drawTile(g, row, col, x, y);
                drawHighlights(g, board, position, x, y);

                Piece piece = board.getPieceAt(position);

                if (piece != null) {
                    drawPiece(g, piece, x, y);
                }

                drawCoordinates(g, row, col, x, y);
            }
        }
        if (engine.isPromotionPending()) {
            drawPromotionOverlay(canvas.getGraphicsContext2D());
        }
    }

    /**
     * Draws a single background tile on the board.
     *
     * @param g the graphics context
     * @param row the grid row
     * @param col the grid column
     * @param x the pixel x-coordinate
     * @param y the pixel y-coordinate
     */
    private void drawTile(GraphicsContext g, int row, int col, double x, double y) {
        g.setFill((row + col) % 2 == 0 ? LIGHT : DARK);
        g.fillRect(x, y, TILE, TILE);
    }

    /**
     * Overlays color highlights on a tile if it is selected, a valid move destination,
     * or a valid shooting target.
     *
     * @param g the graphics context
     * @param board the current game board
     * @param position the grid position to check
     * @param x the pixel x-coordinate
     * @param y the pixel y-coordinate
     */
    private void drawHighlights(GraphicsContext g, Board board, Position position, double x, double y) {
        if (position.equals(selectedPosition)) {
            g.setFill(SELECT);
            g.fillRect(x, y, TILE, TILE);
        }

        if (shootTargets != null && shootTargets.contains(position)) {
            g.setFill(SHOOT_HIGHLIGHT);
            g.fillRect(x, y, TILE, TILE);
        }

        if (legalMoves != null && legalMoves.contains(position)) {
            g.setFill(MOVE_HIGHLIGHT);
            g.fillRect(x, y, TILE, TILE);

            if (board.isEmpty(position)) {
                double dot = TILE / 3.0;
                g.setFill(Color.rgb(0, 120, 0, 0.6));
                g.fillOval(x + (TILE - dot) / 2, y + (TILE - dot) / 2, dot, dot);
            }
        }
    }

    /**
     * Draws algebraic coordinates (a-h, 1-8) along the edges of the board.
     *
     * @param g the graphics context
     * @param row the grid row
     * @param col the grid column
     * @param x the pixel x-coordinate
     * @param y the pixel y-coordinate
     */
    private void drawCoordinates(GraphicsContext g, int row, int col, double x, double y) {
        g.setFont(Font.font("Arial", FontWeight.BOLD, TILE * 0.15));

        if (col == 0) {
            g.setFill(Color.rgb(200, 200, 200));
            g.fillText(String.valueOf(8 - row), x + 4, y + TILE * 0.2);
        }

        if (row == 7) {
            g.setFill(Color.rgb(200, 200, 200));
            g.fillText(String.valueOf((char)('a' + col)), x + TILE - TILE * 0.2, y + TILE - 4);
        }
    }

    /**
     * Draws a piece on the board. Will attempt to use a loaded image,
     * but falls back to text-based rendering if the image is missing.
     *
     * @param g the graphics context
     * @param piece the piece to draw
     * @param x the pixel x-coordinate
     * @param y the pixel y-coordinate
     */
    private void drawPiece(GraphicsContext g, Piece piece, double x, double y) {
        String color = piece.getColor() == core.Color.WHITE ? "white" : "black";
        String name = piece.getClass().getSimpleName().toLowerCase();
        String key = color + "_" + name;

        // Use damaged image if Warrior has 1 life
        if (piece instanceof pieces.Warrior warrior && warrior.getLives() == 1) {
            key = color + "_warrior_damaged";
        }

        Image image = pieceImages.get(key);

        if (image != null) {
            g.drawImage(image, x + 4, y + 4, TILE - 8, TILE - 8);
            return;
        }

        drawFallbackPiece(g, piece, x, y);
    }

    /**
     * Fallback drawing method that renders a piece using its text symbol.
     * Applies special styling for damaged units (like Warriors).
     *
     * @param g the graphics context
     * @param piece the piece to draw
     * @param x the pixel x-coordinate
     * @param y the pixel y-coordinate
     */
    private void drawFallbackPiece(GraphicsContext g, Piece piece, double x, double y) {
        String symbol = piece.getSymbol().toUpperCase();

        g.setFont(Font.font("Arial", FontWeight.BOLD, TILE / 2));

        boolean damagedWarrior = piece instanceof pieces.Warrior warrior && warrior.getLives() == 1;

        // Shadow
        g.setFill(Color.rgb(0, 0, 0, 0.4));
        g.fillText(symbol, x + TILE / 2.0 - 6, y + TILE / 2.0 + 10);

        // Fill color
        if (damagedWarrior) {
            g.setFill(Color.rgb(220, 50, 50));
        } else {
            g.setFill(piece.getColor() == core.Color.WHITE ? Color.WHITE : Color.rgb(30, 30, 30));
        }

        g.fillText(symbol, x + TILE / 2.0 - 8, y + TILE / 2.0 + 8);

        // Border stroke
        g.setStroke(piece.getColor() == core.Color.WHITE ? Color.rgb(80, 80, 80) : Color.rgb(200, 200, 200));
        g.setLineWidth(1);
        g.strokeText(symbol, x + TILE / 2.0 - 8, y + TILE / 2.0 + 8);
    }

    private void drawPromotionOverlay(GraphicsContext g) {
        // Dim the board
        g.setFill(Color.rgb(0, 0, 0, 0.6));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw promotion box
        double boxWidth = TILE * 5;
        double boxHeight = TILE * 2.5;
        double boxX = (canvas.getWidth() - boxWidth) / 2;
        double boxY = (canvas.getHeight() - boxHeight) / 2;

        g.setFill(Color.rgb(50, 40, 30));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g.setStroke(Color.rgb(200, 180, 120));
        g.setLineWidth(2);
        g.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Title
        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", FontWeight.BOLD, TILE * 0.25));
        g.fillText("Choose promotion:", boxX + TILE * 0.3, boxY + TILE * 0.5);

        // Draw three piece choices
        String[] choices = {"mage", "assassin", "archer"};
        for (int i = 0; i < choices.length; i++) {
            double pieceX = boxX + TILE * 0.5 + i * TILE * 1.5;
            double pieceY = boxY + TILE * 0.7;

            // Highlight box per piece
            g.setFill(Color.rgb(255, 255, 255, 0.1));
            g.fillRoundRect(pieceX, pieceY, TILE * 1.2, TILE * 1.2, 10, 10);

            // Draw piece image or letter
            String colorStr = engine.getPromotionColor() == core.Color.WHITE ? "white" : "black";
            String key = colorStr + "_" + choices[i];
            Image img = pieceImages.get(key);

            if (img != null) {
                g.drawImage(img, pieceX + 4, pieceY + 4, TILE * 1.2 - 8, TILE * 1.2 - 8);
            } else {
                g.setFill(Color.WHITE);
                g.setFont(Font.font("Arial", FontWeight.BOLD, TILE * 0.5));
                g.fillText(choices[i].substring(0, 1).toUpperCase(),
                        pieceX + TILE * 0.35, pieceY + TILE * 0.85);
            }
        }
    }

    /**
     * Handles mouse click events on the canvas, translating screen coordinates
     * to grid positions and managing piece selection or movement actions.
     *
     * @param col the clicked column index
     * @param row the clicked row index
     */
    private void handleClick(int col, int row) {
        if (engine.isPromotionPending()) {
            handlePromotionClick(col, row);
            draw();
            return;
        }

        if (engine.isGameOver()) return;

        Position clicked = new Position(row, col);
        Piece clickedPiece = engine.getBoard().getPieceAt(clicked);

        if (selectedPosition == null) {
            selectPiece(clicked, clickedPiece);
            draw();
            return;
        }

        if (clicked.equals(selectedPosition)) {
            clearSelection();
        } else if (clickedPiece != null && clickedPiece.getColor() == engine.getCurrentTurn()) {
            selectPiece(clicked, clickedPiece);
        } else {
            MoveResult result = makeMove(selectedPosition, clicked);
            clearSelection();
            updateStatus(result);
        }

        draw();
    }

    private void handlePromotionClick(int col, int row) {
        double boxWidth = TILE * 5;
        double boxHeight = TILE * 2.5;
        double boxX = (canvas.getWidth() - boxWidth) / 2;
        double boxY = (canvas.getHeight() - boxHeight) / 2;

        double clickX = col * TILE + TILE / 2.0;
        double clickY = row * TILE + TILE / 2.0;

        String[] choices = {"mage", "assassin", "archer"};
        for (int i = 0; i < choices.length; i++) {
            double pieceX = boxX + TILE * 0.5 + i * TILE * 1.5;
            double pieceY = boxY + TILE * 0.7;

            if (clickX >= pieceX && clickX <= pieceX + TILE * 1.2
                    && clickY >= pieceY && clickY <= pieceY + TILE * 1.2) {

                core.Color color = engine.getPromotionColor();
                Position pos = engine.getPromotionPosition();
                Piece promoted;

                switch (choices[i]) {
                    case "assassin": promoted = new pieces.Assassin(color, pos); break;
                    case "archer":   promoted = new pieces.Archer(color, pos); break;
                    default:         promoted = new pieces.Mage(color, pos); break;
                }

                engine.promote(promoted);
                statusLabel.setText(engine.getCurrentTurn() + "'s turn  |  Move: " + engine.getTotalMoves());
                return;
            }
        }
    }

    /**
     * Resets the game state, clears selections, and starts a fresh match.
     */
    private void resetGame() {
        Board board = GameSetup.createStandardBoard();
        engine = new GameEngine(board);
        clearSelection();
        newGameButton.setVisible(false);
        newGameButton.setManaged(false);
        resignButton.setVisible(true);
        resignButton.setManaged(true);
        statusLabel.setText("White's turn");
        draw();
    }

    private void resign() {
        engine.resign();
        core.Color winner = engine.getWinner();
        String winnerName = winner == core.Color.WHITE ? "White" : "Black";
        statusLabel.setText(winnerName + " wins! Opponent resigned.");
        newGameButton.setVisible(true);
        newGameButton.setManaged(true);
        resignButton.setVisible(false);
        resignButton.setManaged(false);
        if (gameOverSound != null) gameOverSound.play();
        draw();
    }

    /**
     * Selects a specific piece, generating the necessary UI highlights for
     * legal moves, stabbing ranges, and shooting targets.
     *
     * @param position the position of the piece
     * @param piece the piece being selected
     */
    private void selectPiece(Position position, Piece piece) {
        if (piece == null || piece.getColor() != engine.getCurrentTurn()) {
            return;
        }

        selectedPosition = position;
        legalMoves = engine.getSafeMoves(piece);

        if (piece instanceof Shootable) {
            shootTargets = getShootTargets(piece);
        } else if (piece instanceof Stabbable) {
            shootTargets = getStabTargets(piece);
            // Remove enemy squares from standard green movement highlights
            legalMoves = legalMoves.stream()
                    .filter(pos -> engine.getBoard().getPieceAt(pos) == null)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            shootTargets = null;
        }
    }

    /**
     * Clears the current piece selection and removes all UI highlights.
     */
    private void clearSelection() {
        selectedPosition = null;
        legalMoves = null;
        shootTargets = null;
    }

    /**
     * Updates the top status label based on the outcome of a move attempt.
     * Plays appropriate sound effects and handles win conditions.
     *
     * @param result the outcome of the latest move
     */
    private void updateStatus(MoveResult result) {
        if (!result.isSuccess()) {
            statusLabel.setText("Invalid move!");
            return;
        }

        if (result.isWon()) {
            if (gameOverSound != null) gameOverSound.play();
            newGameButton.setVisible(true);
            newGameButton.setManaged(true);
            resignButton.setVisible(false);
            resignButton.setManaged(false);

            core.Color winner = engine.getWinner();
            String winnerName = winner == core.Color.WHITE ? "White" : "Black";

            statusLabel.setText(winnerName + " wins!");
            newGameButton.setVisible(true);
            return;
        }

        if (result.getCapturedPiece() != null) {
            if (captureSound != null) {
                captureSound.play();
            }
        } else {
            if (moveSound != null) {
                moveSound.play();
            }
        }

        statusLabel.setText(engine.getCurrentTurn() + "'s turn  |  Move: " + engine.getTotalMoves());
    }

    /**
     * Extracts valid melee targets for a stabbable piece.
     *
     * @param piece the stabbable piece
     * @return a list of positions containing enemy pieces within stab range
     */
    private List<Position> getStabTargets(Piece piece) {
        List<Position> targets = new ArrayList<>();
        for (Position pos : engine.getSafeMoves(piece)) {
            Piece occupant = engine.getBoard().getPieceAt(pos);
            if (occupant != null && occupant.getColor() != engine.getCurrentTurn()) {
                targets.add(pos);
            }
        }
        return targets;
    }

    /**
     * Retrieves valid ranged targets depending on the specific subclass of the piece.
     *
     * @param piece the shootable piece
     * @return a list of valid target positions for a ranged attack
     */
    private List<Position> getShootTargets(Piece piece) {
        if (piece instanceof pieces.Mage mage) {
            return mage.getShootTargets(engine.getBoard());
        }
        if (piece instanceof pieces.Archmage archmage) {
            return archmage.getShootTargets(engine.getBoard());
        }
        if (piece instanceof pieces.Archer archer) {
            return archer.getShootTargets(engine.getBoard());
        }
        return new ArrayList<>();
    }

    /**
     * Delegates an action to the game engine, determining whether a player's interaction
     * should result in a standard move, a ranged shot, or a melee stab.
     *
     * @param from the starting position of the active piece
     * @param target the destination position or attacked square
     * @return a MoveResult representing the success or failure of the action
     */
    private MoveResult makeMove(Position from, Position target) {
        Piece piece = engine.getBoard().getPieceAt(from);
        Piece targetPiece = engine.getBoard().getPieceAt(target);

        if (targetPiece != null && targetPiece.getColor() != engine.getCurrentTurn()) {
            if (piece instanceof Shootable) {
                List<Position> targets = getShootTargets(piece);
                if (!targets.contains(target)) {
                    return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
                }
                return engine.shoot(from, target);
            }
            if (piece instanceof Stabbable) {
                List<Position> targets = engine.getSafeMoves(piece);
                if (!targets.contains(target)) {
                    return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
                }
                return engine.stab(from, target);
            }
        }

        return engine.makeMove(from, target);
    }

    /**
     * Static launcher method for the JavaFX framework.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}