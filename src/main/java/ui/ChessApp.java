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
import util.GameSetup;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ChessApp extends Application {

    private static final int TILE = 80;
    private static final int SIZE = 8 * TILE;

    private final Map<String, Image> pieceImages = new HashMap<>();

    private static final Color LIGHT  = Color.rgb(240, 217, 181);
    private static final Color DARK   = Color.rgb(181, 136,  99);
    private static final Color SELECT = Color.rgb(255, 255,   0, 0.6);
    private static final Color MOVE_HL = Color.rgb( 50, 200,  50, 0.5);

    private GameEngine engine;
    private Canvas canvas;
    private Label statusLabel;

    private Position selectedPosition = null;
    private List<Position> legalMoves = null;

    @Override
    public void start(Stage stage) {
        Board board = GameSetup.createStandardBoard();
        engine = new GameEngine(board);

        canvas = new Canvas(SIZE, SIZE);
        canvas.setOnMouseClicked(e -> handleClick(
                (int)(e.getX() / TILE),
                (int)(e.getY() / TILE)
        ));

        statusLabel = new Label("White's turn");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setPadding(new Insets(10));

        GridPane colLabels = makeColLabels();
        GridPane rowLabels = makeRowLabels();

        HBox boardRow = new HBox(rowLabels, canvas);
        boardRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(colLabels, boardRow, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #312e2b;");
        root.setPadding(new Insets(16));
        root.setSpacing(0);

        loadImages();
        draw();

        Scene scene = new Scene(root);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void loadImages() {
        String[] colors = {"white", "black"};
        String[] pieces = {"archmage", "dragon", "archer", "mage", "assassin", "warrior"};
        for (String color : colors) {
            for (String piece : pieces) {
                String key = color + "_" + piece;
                try {
                    Image img = new Image(getClass().getResourceAsStream("/images/" + key + ".png"));
                    pieceImages.put(key, img);
                } catch (Exception e) {
                    System.out.println("Image not found: " + key);
                }
            }
        }
    }

    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Board board = engine.getBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                double x = col * TILE;
                double y = row * TILE;
                Position pos = new Position(row, col);

                // Base square
                g.setFill((row + col) % 2 == 0 ? LIGHT : DARK);
                g.fillRect(x, y, TILE, TILE);

                // Selected square
                if (pos.equals(selectedPosition)) {
                    g.setFill(SELECT);
                    g.fillRect(x, y, TILE, TILE);
                }

                if (shootTargets != null && shootTargets.contains(pos)) {
                    g.setFill(Color.rgb(255, 140, 0, 0.6));
                    g.fillRect(x, y, TILE, TILE);
                }

                if (legalMoves != null && legalMoves.contains(pos)) {
                    g.setFill(MOVE_HL);
                    g.fillRect(x, y, TILE, TILE);
                    if (board.isEmpty(pos)) {
                        g.setFill(Color.rgb(0, 120, 0, 0.6));
                        double dot = TILE / 3.0;
                        g.fillOval(x + (TILE - dot) / 2, y + (TILE - dot) / 2, dot, dot);
                    }
                }

                // Draw piece
                Piece piece = board.getPieceAt(pos);
                if (piece != null) drawPiece(g, piece, x, y);

                // Coordinate labels
                g.setFont(Font.font("Arial", 11));
                g.setFill((row + col) % 2 == 0 ? DARK : LIGHT);
                if (col == 0) g.fillText(String.valueOf(8 - row), x + 3, y + 14);
                if (row == 7) g.fillText(String.valueOf((char)('a' + col)), x + TILE - 12, y + TILE - 4);
            }
        }
    }

    private void drawPiece(GraphicsContext g, Piece piece, double x, double y) {
        String color = piece.getColor() == core.Color.WHITE ? "white" : "black";
        String name = piece.getClass().getSimpleName().toLowerCase();
        String key = color + "_" + name;

        Image img = pieceImages.get(key);
        if (img != null) {
            g.drawImage(img, x + 4, y + 4, TILE - 8, TILE - 8);
        } else {
            String symbol = piece.getSymbol().toUpperCase();
            g.setFont(Font.font("Arial", FontWeight.BOLD, TILE / 2));

            boolean isDamagedWarrior = piece instanceof pieces.Warrior
                    && ((pieces.Warrior) piece).getLives() == 1;

            g.setFill(Color.rgb(0, 0, 0, 0.4));
            g.fillText(symbol, x + (TILE / 2.0) - 8 + 2, y + (TILE / 2.0) + 8 + 2);

            if (isDamagedWarrior) {
                g.setFill(Color.rgb(220, 50, 50));
            } else {
                g.setFill(piece.getColor() == core.Color.WHITE ? Color.WHITE : Color.rgb(30, 30, 30));
            }
            g.fillText(symbol, x + (TILE / 2.0) - 8, y + (TILE / 2.0) + 8);

            g.setStroke(piece.getColor() == core.Color.WHITE ? Color.rgb(80, 80, 80) : Color.rgb(200, 200, 200));
            g.setLineWidth(1);
            g.strokeText(symbol, x + (TILE / 2.0) - 8, y + (TILE / 2.0) + 8);
        }
    }

    private void handleClick(int col, int row) {

        if (engine.isGameOver()) return;

        Position clicked = new Position(row, col);
        Piece clickedPiece = engine.getBoard().getPieceAt(clicked);

        if (selectedPosition == null) {
            // Select a piece
            if (clickedPiece != null && clickedPiece.getColor() == engine.getCurrentTurn()) {
                selectedPosition = clicked;
                legalMoves = engine.getSafeMoves(clickedPiece);
                if (isShooting(clickedPiece)) {
                    shootTargets = getShootTargets(clickedPiece);
                }
            }
        } else {
            if (clicked.equals(selectedPosition)) {
                // Deselect
                clearSelection();
            } else if (clickedPiece != null && clickedPiece.getColor() == engine.getCurrentTurn()) {
                // Switch to another piece
                selectedPosition = clicked;
                legalMoves = engine.getSafeMoves(clickedPiece);
                shootTargets = isShooting(clickedPiece) ? getShootTargets(clickedPiece) : null;
            } else if (shootTargets != null && shootTargets.contains(clicked)) {
                // Clicked an orange target — shoot
                MoveResult result = shoot(selectedPosition, clicked);
                clearSelection();
                updateStatus(result);
            } else {
                // Clicked a green target — move
                MoveResult result = engine.makeMove(selectedPosition, clicked);
                clearSelection();
                updateStatus(result);
            }
        }
        draw();
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
            core.Color winner = engine.getWinner();
            String name = winner == core.Color.WHITE ? "White" : "Black";
            statusLabel.setText(name + " wins! All enemy pieces eliminated!");
            showAlert(name + " wins!", "All enemy pieces eliminated!");
        } else {
            statusLabel.setText(engine.getCurrentTurn() + "'s turn  |  Move: " + engine.getTotalMoves());
        }
    }

    private void showAlert(String title, String header) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }

    private GridPane makeColLabels() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(0, 0, 0, 20));
        for (int col = 0; col < 8; col++) {
            Label l = new Label(String.valueOf((char)('a' + col)));
            l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            l.setTextFill(Color.rgb(200, 200, 200));
            l.setMinWidth(TILE);
            l.setAlignment(Pos.CENTER);
            gp.add(l, col, 0);
        }
        return gp;
    }

    private GridPane makeRowLabels() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(0, 4, 0, 0));
        for (int row = 0; row < 8; row++) {
            Label l = new Label(String.valueOf(8 - row));
            l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            l.setTextFill(Color.rgb(200, 200, 200));
            l.setMinHeight(TILE);
            l.setAlignment(Pos.CENTER);
            gp.add(l, 0, row);
        }
        return gp;
    }

    private boolean isShooting(Piece piece) {
        return piece instanceof pieces.Mage
                || piece instanceof pieces.Archmage
                || piece instanceof pieces.Archer;
    }

    private List<Position> shootTargets = null;

    private List<Position> getShootTargets(Piece piece) {
        if (piece instanceof pieces.Mage)
            return ((pieces.Mage) piece).getMagicTargets(engine.getBoard());
        if (piece instanceof pieces.Archmage)
            return ((pieces.Archmage) piece).getMagicTargets(engine.getBoard());
        if (piece instanceof pieces.Archer)
            return ((pieces.Archer) piece).getShootTargets(engine.getBoard());
        return new ArrayList<>();
    }

    private MoveResult shoot(Position from, Position target) {
        Piece piece = engine.getBoard().getPieceAt(from);
        if (piece instanceof pieces.Mage)
            return engine.shootMagic(from, target);
        if (piece instanceof pieces.Archmage)
            return engine.archmageShoot(from, target);
        if (piece instanceof pieces.Archer)
            return engine.archerShoot(from, target);
        return MoveResult.failure(MoveResult.Status.INVALID_MOVE);
    }

    public static void main(String[] args) { launch(args); }
}