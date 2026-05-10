package core;

/**
 * Represents the affiliation of a game piece or a player within the Kuroshiro engine.
 * The game operates strictly on a two-player, turn-based system utilizing these two colors.
 */
public enum Color {

    /** * Represents the White player or a piece belonging to the White team.
     * By standard game rules, the White player takes the first turn.
     */
    WHITE,

    /** * Represents the Black player or a piece belonging to the Black team.
     * The Black player responds after White's initial turn.
     */
    BLACK;

    /**
     * Identifies and returns the opposing color.
     * This is highly useful for turn-switching logic within the game engine,
     * or for determining enemy targets during combat calculation.
     *
     * @return {@code BLACK} if the current color is {@code WHITE}, or {@code WHITE} if the current color is {@code BLACK}
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}