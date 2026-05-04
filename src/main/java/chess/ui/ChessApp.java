package chess.ui;

import chess.core.*;
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
import java.util.List;

public class ChessApp extends Application {

    private static final int TILE = 80;
    private static final int SIZE = 8 * TILE;

    private static final Color LIGHT   = Color.rgb(240, 217, 181);
    private static final Color DARK    = Color.rgb(181, 136,  99);
    private static final Color SELECT  = Color.rgb(255, 255,   0, 0.6);
    private static final Color MOVE_HL = Color.rgb( 50, 200,  50, 0.5);
    private static final Color CHECK_C = Color.rgb(220,  50,  50, 0.7);

    private GameEngine engine;
    private Canvas canvas;
    private Label statusLabel;

    private Position selectedPosition = null;
    private List<Position> legalMoves = null;

    @Override
    public void start(Stage stage) {
        Board board = chess.util.GameSetup.createStandardBoard();
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

        // Coordinate labels
        GridPane colLabels = makeColLabels();
        GridPane rowLabels = makeRowLabels();

        HBox boardRow = new HBox(rowLabels, canvas);
        boardRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(colLabels, boardRow, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #312e2b;");
        root.setPadding(new Insets(16));
        root.setSpacing(0);

        draw();

        Scene scene = new Scene(root);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // ── Drawing ──────────────────────────────────────────────────────────────

    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Board board = engine.getBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                double x = col * TILE;
                double y = row * TILE;
                Position pos = new Position(row, col);

                // Base square color
                g.setFill((row + col) % 2 == 0 ? LIGHT : DARK);
                g.fillRect(x, y, TILE, TILE);

                // Selected square
                if (pos.equals(selectedPosition)) {
                    g.setFill(SELECT);
                    g.fillRect(x, y, TILE, TILE);
                }

                // Legal move highlight
                if (legalMoves != null && legalMoves.contains(pos)) {
                    g.setFill(MOVE_HL);
                    g.fillRect(x, y, TILE, TILE);
                    // Dot for empty squares
                    if (board.isEmpty(pos)) {
                        g.setFill(Color.rgb(0, 120, 0, 0.6));
                        double dot = TILE / 3.0;
                        g.fillOval(x + (TILE - dot) / 2, y + (TILE - dot) / 2, dot, dot);
                    }
                }

                // Check highlight on king
                Piece king = board.findPiece(chess.pieces.King.class, engine.getCurrentTurn());
                if (king != null && king.getPosition().equals(pos) && engine.isInCheck(engine.getCurrentTurn())) {
                    g.setFill(CHECK_C);
                    g.fillRect(x, y, TILE, TILE);
                }

                // Draw piece
                Piece piece = board.getPieceAt(pos);
                if (piece != null) drawPiece(g, piece, x, y);

                // Coordinate hint
                g.setFont(Font.font("Arial", 11));
                g.setFill((row + col) % 2 == 0 ? DARK : LIGHT);
                if (col == 0) g.fillText(String.valueOf(8 - row), x + 3, y + 14);
                if (row == 7) g.fillText(String.valueOf((char)('a' + col)), x + TILE - 12, y + TILE - 4);
            }
        }
    }

    private void drawPiece(GraphicsContext g, Piece piece, double x, double y) {
        String symbol = getUnicode(piece);
        g.setFont(Font.font("Segoe UI Symbol", TILE - 18));

        // Shadow
        g.setFill(Color.rgb(0, 0, 0, 0.3));
        g.fillText(symbol, x + 8, y + TILE - 8);

        // Piece fill
        g.setFill(piece.getColor() == chess.core.Color.WHITE ? Color.WHITE : Color.rgb(20, 20, 20));
        g.fillText(symbol, x + 6, y + TILE - 10);

        // Outline for white pieces
        if (piece.getColor() == chess.core.Color.WHITE) {
            g.setStroke(Color.rgb(80, 80, 80));
            g.setLineWidth(0.5);
            g.strokeText(symbol, x + 6, y + TILE - 10);
        }
    }

    private String getUnicode(Piece piece) {
        boolean w = piece.getColor() == chess.core.Color.WHITE;
        switch (piece.getSymbol().toUpperCase()) {
            case "K": return w ? "♔" : "♚";
            case "Q": return w ? "♕" : "♛";
            case "R": return w ? "♖" : "♜";
            case "B": return w ? "♗" : "♝";
            case "N": return w ? "♘" : "♞";
            case "P": return w ? "♙" : "♟";
            default:  return w ? piece.getSymbol().toUpperCase() : piece.getSymbol().toLowerCase();
        }
    }

    // ── Click handling ───────────────────────────────────────────────────────

    private void handleClick(int col, int row) {
        if (engine.isGameOver()) return;

        Position clicked = new Position(row, col);
        Piece clickedPiece = engine.getBoard().getPieceAt(clicked);

        if (selectedPosition == null) {
            if (clickedPiece != null && clickedPiece.getColor() == engine.getCurrentTurn()) {
                selectedPosition = clicked;
                legalMoves = engine.getSafeMoves(clickedPiece);
            }
        } else {
            if (clicked.equals(selectedPosition)) {
                clearSelection();
            } else if (clickedPiece != null && clickedPiece.getColor() == engine.getCurrentTurn()) {
                selectedPosition = clicked;
                legalMoves = engine.getSafeMoves(clickedPiece);
            } else {
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
    }

    private void updateStatus(MoveResult result) {
        if (!result.isSuccess()) { statusLabel.setText("Invalid move!"); return; }

        if (result.isCheckmate()) {
            chess.core.Color winner = engine.getWinner();
            String name = winner == chess.core.Color.WHITE ? "White" : "Black";
            statusLabel.setText(name + " wins by checkmate!");
            showAlert(name + " wins!", "Checkmate!");
        } else if (result.isStalemate()) {
            statusLabel.setText("Stalemate — Draw!");
            showAlert("Draw!", "Stalemate!");
        } else if (result.isCheck()) {
            statusLabel.setText(engine.getCurrentTurn() + "'s turn — CHECK!");
        } else {
            statusLabel.setText(engine.getCurrentTurn() + "'s turn");
        }
    }

    private void showAlert(String title, String header) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }

    // ── Coordinate labels ────────────────────────────────────────────────────

    private GridPane makeColLabels() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(0, 0, 0, 20));
        for (int col = 0; col < 8; col++) {
            Label l = new Label(String.valueOf((char)('a' + col)));
            l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            l.setTextFill(Color.rgb(200, 200, 200));
            l.setMinWidth(TILE); l.setAlignment(Pos.CENTER);
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
            l.setMinHeight(TILE); l.setAlignment(Pos.CENTER);
            gp.add(l, 0, row);
        }
        return gp;
    }

    public static void main(String[] args) { launch(args); }
}
