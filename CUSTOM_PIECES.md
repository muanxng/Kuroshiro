# How to Add a Custom Piece

## Step 1 — Create your piece class in chess/pieces/

```java
package chess.pieces;

import java.util.*;

// Example: Teleporter — jumps to any square on the board
public class Teleporter extends Piece {

    public Teleporter(Color color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                if (!pos.equals(position) && canMoveTo(pos, board))
                    moves.add(pos);
            }
        return moves;
    }

    @Override
    public String getSymbol() {
        return "T";
    }
}
```

## Step 2 — Add a Unicode symbol in ChessApp.java

Find `getUnicode()` and add a case:
```java
case "T": return w ? "✦" : "✧";
```

## Step 3 — Add to the board in GameSetup.java

```java
board.placePiece(new Teleporter(color, new Position(backRow, 1)));
```

## Step 4 — Write a JUnit test

```java
@Test void teleporterCanReachAnySquare() {
    Board board = new Board();
    Teleporter t = new Teleporter(Color.WHITE, new Position(4,4));
    board.placePiece(t);
    assertEquals(63, t.getLegalMoves(board).size());
}
```

---

## Useful helpers from Piece

| Method | What it does |
|--------|-------------|
| `canMoveTo(pos, board)` | Empty or enemy square |
| `isEnemy(pos, board)` | Has an enemy piece |
| `isEmpty(pos, board)` | Square is empty |
| `slide(dr, dc, board)` | Slide until blocked — used by Rook/Bishop/Queen |

## Architecture

```
Moveable (interface)
    └── Piece (abstract)
            ├── King / Queen / Rook / Bishop / Knight / Pawn
            └── YourCustomPiece  ← add here

Board        — stores & moves pieces
GameEngine   — turns, check, checkmate, stalemate
GameSetup    — initial piece placement
ChessApp     — JavaFX UI
Position     — immutable (row, col)
Color        — WHITE / BLACK
MoveResult   — result of GameEngine.makeMove()
```

## Build & Run commands

```bash
# Run directly
mvn javafx:run

# Run tests
mvn test

# Build fat JAR (includes JavaFX)
mvn package
java -jar target/chess.jar
```
