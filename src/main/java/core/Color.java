package core;

/**
 * Represents the color of a game piece or player.
 */
public enum Color {

    /** Represents the white pieces or player. */
    WHITE,

    /** Represents the black pieces or player. */
    BLACK;

    /**
     * Retrieves the opposite color.
     *
     * @return BLACK if the current color is WHITE, otherwise WHITE
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}