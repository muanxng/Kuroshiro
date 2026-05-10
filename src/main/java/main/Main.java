package main;

import ui.ChessApp;

/**
 * The primary entry point for the Kuroshiro application.
 * This class serves as a bootstrap launcher that delegates execution
 * directly to the JavaFX-based user interface defined in {@link ChessApp}.
 */
public class Main {

    /**
     * The main execution method that starts the Java application.
     *
     * @param args standard command-line arguments passed to the program
     */
    public static void main(String[] args) {
        ChessApp.main(args);
    }
}