package ui;

import core.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    private static final int TILE = 80;
    private static final int BOARD_SIZE = 8;
    private static final int CANVAS_SIZE = TILE * BOARD_SIZE;

    private static final Color LIGHT = Color.rgb(240, 217, 181);
    private static final Color DARK = Color.rgb(181, 136, 99);
    private static final Color SELECT = Color.rgb(255, 255, 0, 0.6);
    private static final Color MOVE_HIGHLIGHT = Color.rgb(50, 200, 50, 0.5);
    private static final Color SHOOT_HIGHLIGHT = Color.rgb(255, 140, 0, 0.6);

    private final Map<String, Image> pieceImages = new HashMap<>();

    private final AudioClip captureSound =
            new AudioClip(getClass().getResource("/sounds/capture_sound.wav").toExternalForm());

    private final AudioClip gameOverSound =
            new AudioClip(getClass().getResource("/sounds/game_over_sound.wav").toExternalForm());

    private GameEngine engine;
    private Canvas canvas;
    private Label statusLabel;

    private Position selectedPosition;
    private List<Position> legalMoves;
    private List<Position> shootTargets;

    @Override
    public void start(Stage stage) {
        engine = new GameEngine(GameSetup.createStandardBoard());

        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        canvas.setOnMouseClicked(event ->
                handleClick(
                        (int) (event.getX() / TILE),
                        (int) (event.getY() / TILE)
                )
        );

        statusLabel = createStatusLabel();

        VBox root = new VBox(
                createColumnLabels(),
                createBoardRow(),
                statusLabel
        );

        root.setAlignment(Pos.CENTER);
        root.setSpacing(0);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #312e2b;");

        loadImages();
        draw();

        stage.setTitle("Chess");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private Label createStatusLabel() {
        Label label = new Label("White's turn");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10));
        return label;
    }

    private HBox createBoardRow() {
        HBox row = new HBox(createRowLabels(), canvas);
        row.setAlignment(Pos.CENTER);
        return row;
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

    private void drawCoordinates(
            GraphicsContext g,
            int row,
            int col,
            double x,
            double y
    ) {

        g.setFont(Font.font("Arial", 11));
        g.setFill((row + col) % 2 == 0 ? DARK : LIGHT);

        if (col == 0) {
            g.fillText(String.valueOf(8 - row), x + 3, y + 14);
        }

        if (row == 7) {
            g.fillText(
                    String.valueOf((char) ('a' + col)),
                    x + TILE - 12,
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
                clickedPiece != null
                        && clickedPiece.getColor() == engine.getCurrentTurn()
        ) {

            selectPiece(clicked, clickedPiece);

        } else if (
                shootTargets != null
                        && shootTargets.contains(clicked)
        ) {

            MoveResult result = shoot(selectedPosition, clicked);

            clearSelection();
            updateStatus(result);

        } else {

            MoveResult result =
                    engine.makeMove(selectedPosition, clicked);

            clearSelection();
            updateStatus(result);
        }

        draw();
    }

    private void selectPiece(Position position, Piece piece) {
        if (piece == null || piece.getColor() != engine.getCurrentTurn()) {
            return;
        }

        selectedPosition = position;
        legalMoves = engine.getSafeMoves(piece);

        shootTargets = isShootingPiece(piece)
                ? getShootTargets(piece)
                : null;
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

            if (gameOverSound != null) {
                gameOverSound.play();
            }

            core.Color winner = engine.getWinner();

            String winnerName =
                    winner == core.Color.WHITE ? "White" : "Black";

            statusLabel.setText(
                    winnerName + " wins! All enemy pieces eliminated!"
            );

            showAlert(
                    winnerName + " wins!",
                    "All enemy pieces eliminated!"
            );

            return;
        }

        if (result.getCapturedPiece() != null && captureSound != null) {
            captureSound.play();
        }

        statusLabel.setText(
                engine.getCurrentTurn()
                        + "'s turn  |  Move: "
                        + engine.getTotalMoves()
        );
    }

    private void showAlert(String title, String header) {
        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);

        alert.show();
    }

    private GridPane createColumnLabels() {

        GridPane grid = new GridPane();

        grid.setPadding(new Insets(0, 0, 0, 20));

        for (int col = 0; col < BOARD_SIZE; col++) {

            Label label =
                    new Label(String.valueOf((char) ('a' + col)));

            label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            label.setTextFill(Color.rgb(200, 200, 200));
            label.setMinWidth(TILE);
            label.setAlignment(Pos.CENTER);

            grid.add(label, col, 0);
        }

        return grid;
    }

    private GridPane createRowLabels() {

        GridPane grid = new GridPane();

        grid.setPadding(new Insets(0, 4, 0, 0));

        for (int row = 0; row < BOARD_SIZE; row++) {

            Label label =
                    new Label(String.valueOf(8 - row));

            label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            label.setTextFill(Color.rgb(200, 200, 200));
            label.setMinHeight(TILE);
            label.setAlignment(Pos.CENTER);

            grid.add(label, 0, row);
        }

        return grid;
    }

    private boolean isShootingPiece(Piece piece) {
        return piece instanceof pieces.Mage
                || piece instanceof pieces.Archmage
                || piece instanceof pieces.Archer;
    }

    private List<Position> getShootTargets(Piece piece) {

        if (piece instanceof pieces.Mage mage) {
            return mage.getMagicTargets(engine.getBoard());
        }

        if (piece instanceof pieces.Archmage archmage) {
            return archmage.getMagicTargets(engine.getBoard());
        }

        if (piece instanceof pieces.Archer archer) {
            return archer.getShootTargets(engine.getBoard());
        }

        return new ArrayList<>();
    }

    private MoveResult shoot(Position from, Position target) {

        Piece piece = engine.getBoard().getPieceAt(from);

        if (piece instanceof pieces.Mage) {
            return engine.shootMagic(from, target);
        }

        if (piece instanceof pieces.Archmage) {
            return engine.archmageShoot(from, target);
        }

        if (piece instanceof pieces.Archer) {
            return engine.archerShoot(from, target);
        }

        return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}