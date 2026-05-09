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

public class ChessApp extends Application {

    private static final int BOARD_SIZE = 8;

    private double TILE = 80;

    private static final Color LIGHT = Color.rgb(240, 217, 181);
    private static final Color DARK = Color.rgb(181, 136, 99);
    private static final Color SELECT = Color.rgb(255, 255, 0, 0.6);
    private static final Color MOVE_HIGHLIGHT = Color.rgb(50, 200, 50, 0.5);
    private static final Color SHOOT_HIGHLIGHT = Color.rgb(255, 140, 0, 0.6);

    private final Map<String, Image> pieceImages = new HashMap<>();

    private final AudioClip captureSound =
            new AudioClip(getClass().getResource("/sounds/capture_sound.mp3").toExternalForm());

    private final AudioClip moveSound =
            new AudioClip(getClass().getResource("/sounds/move_sound.mp3").toExternalForm());

    private final AudioClip gameOverSound =
            new AudioClip(getClass().getResource("/sounds/game_over_sound.mp3").toExternalForm());

    private Button newGameButton;

    private GameEngine engine;
    private Canvas canvas;
    private Label statusLabel;

    private Position selectedPosition;
    private List<Position> legalMoves;
    private List<Position> shootTargets;

    private VBox root;

    @Override
    public void start(Stage stage) {
        engine = new GameEngine(GameSetup.createStandardBoard());

        canvas = new Canvas(TILE * BOARD_SIZE, TILE * BOARD_SIZE);
        canvas.setOnMouseClicked(event -> handleClick((int) (event.getX() / TILE), (int) (event.getY() / TILE)));

        statusLabel = createStatusLabel();

        newGameButton = new Button("New Game");
        newGameButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        newGameButton.setVisible(false);
        newGameButton.setOnAction(e -> resetGame());

        root = new VBox(canvas, statusLabel, newGameButton);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-image: url('/images/background.png'); " + "-fx-background-size: contain; " + "-fx-background-position: center;");

        loadImages();

        Scene scene = new Scene(root);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Press Escape to exit fullscreen");

        scene.widthProperty().addListener((obs, oldVal, newVal) -> resizeBoard(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> resizeBoard(scene));

        stage.show();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        resizeBoard(scene);
    }

    private void resizeBoard(Scene scene) {
        double available = Math.min(
                scene.getWidth() - 40,
                scene.getHeight() - 100
        );
        TILE = available / BOARD_SIZE;

        canvas.setWidth(TILE * BOARD_SIZE);
        canvas.setHeight(TILE * BOARD_SIZE);

        draw();
    }

    private Label createStatusLabel() {
        Label label = new Label("White's turn");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.BLACK);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10));
        return label;
    }

    private void loadImages() {
        String[] colors = {"white", "black"};
        String[] pieces = {
                "archmage",
                "dragon",
                "archer",
                "mage",
                "assassin",
                "warrior"
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
    }

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
    }

    private void drawTile(GraphicsContext g, int row, int col, double x, double y) {
        g.setFill((row + col) % 2 == 0 ? LIGHT : DARK);
        g.fillRect(x, y, TILE, TILE);
    }

    private void drawHighlights(
            GraphicsContext g,
            Board board,
            Position position,
            double x,
            double y
    ) {
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
                g.fillOval(
                        x + (TILE - dot) / 2,
                        y + (TILE - dot) / 2,
                        dot,
                        dot
                );
            }
        }
    }

    private void drawCoordinates(GraphicsContext g, int row, int col, double x, double y) {
        g.setFont(Font.font("Arial", FontWeight.BOLD, TILE * 0.15));

        if (col == 0) {
            g.setFill(Color.rgb(200, 200, 200));
            g.fillText(String.valueOf(8 - row), x + 4, y + TILE * 0.2);
        }

        if (row == 7) {
            g.setFill(Color.rgb(200, 200, 200));
            g.fillText(
                    String.valueOf((char)('a' + col)),
                    x + TILE - TILE * 0.2,
                    y + TILE - 4
            );
        }
    }

    private void drawPiece(GraphicsContext g, Piece piece, double x, double y) {

        String color =
                piece.getColor() == core.Color.WHITE ? "white" : "black";

        String name =
                piece.getClass().getSimpleName().toLowerCase();

        String key = color + "_" + name;

        Image image = pieceImages.get(key);

        if (image != null) {
            g.drawImage(image, x + 4, y + 4, TILE - 8, TILE - 8);
            return;
        }

        drawFallbackPiece(g, piece, x, y);
    }

    private void drawFallbackPiece(
            GraphicsContext g,
            Piece piece,
            double x,
            double y
    ) {
        String symbol = piece.getSymbol().toUpperCase();

        g.setFont(Font.font("Arial", FontWeight.BOLD, TILE / 2));

        boolean damagedWarrior =
                piece instanceof pieces.Warrior warrior
                        && warrior.getLives() == 1;

        g.setFill(Color.rgb(0, 0, 0, 0.4));
        g.fillText(symbol, x + TILE / 2.0 - 6, y + TILE / 2.0 + 10);

        if (damagedWarrior) {
            g.setFill(Color.rgb(220, 50, 50));
        } else {
            g.setFill(
                    piece.getColor() == core.Color.WHITE
                            ? Color.WHITE
                            : Color.rgb(30, 30, 30)
            );
        }

        g.fillText(symbol, x + TILE / 2.0 - 8, y + TILE / 2.0 + 8);

        g.setStroke(
                piece.getColor() == core.Color.WHITE
                        ? Color.rgb(80, 80, 80)
                        : Color.rgb(200, 200, 200)
        );

        g.setLineWidth(1);
        g.strokeText(symbol, x + TILE / 2.0 - 8, y + TILE / 2.0 + 8);
    }

    private void handleClick(int col, int row) {

        if (engine.isGameOver()) {
            return;
        }

        Position clicked = new Position(row, col);

        Piece clickedPiece =
                engine.getBoard().getPieceAt(clicked);

        if (selectedPosition == null) {
            selectPiece(clicked, clickedPiece);
            draw();
            return;
        }

        if (clicked.equals(selectedPosition)) {
            clearSelection();

        } else if (
                clickedPiece != null && clickedPiece.getColor() == engine.getCurrentTurn()
        ) {

            selectPiece(clicked, clickedPiece);
        } else {

            MoveResult result = makeMove(selectedPosition, clicked);
            clearSelection();
            updateStatus(result);
        }

        draw();
    }

    private void resetGame() {
        Board board = GameSetup.createStandardBoard();
        engine = new GameEngine(board);
        clearSelection();
        newGameButton.setVisible(false);
        statusLabel.setText("White's turn");
        draw();
    }

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
            // Remove enemy squares from green highlights
            legalMoves = legalMoves.stream()
                    .filter(pos -> engine.getBoard().getPieceAt(pos) == null)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            shootTargets = null;
        }
    }

    private void clearSelection() {
        selectedPosition = null;
        legalMoves = null;
        shootTargets = null;
    }

    private void updateStatus(MoveResult result) {

        if (!result.isSuccess()) {
            statusLabel.setText("Invalid move!");
            return;
        }

        if (result.isWon()) {
            gameOverSound.play();

            core.Color winner = engine.getWinner();
            String winnerName = winner == core.Color.WHITE ? "White" : "Black";

            statusLabel.setText(winnerName + " wins!");
            newGameButton.setVisible(true);
            return;
        }

        if (result.getCapturedPiece() != null) captureSound.play();
        else moveSound.play();

        statusLabel.setText(engine.getCurrentTurn() + "'s turn  |  Move: " + engine.getTotalMoves());
    }

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


    public static void main(String[] args) {
        launch(args);
    }
}