package ui;

import database.DatabaseConnection;

/**
 * Main – Entry point of the application.
 *
 * What happens when you run this:
 *   1. Connects to MySQL
 *   2. Starts HTTP server on port 8080
 *   3. You open http://localhost:8080/web/login.html in a browser
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Data Redundancy Removal and Validation System...");

        // 1. Test DB connection early
        try {
            DatabaseConnection.getConnection();
        } catch (Exception e) {
            System.err.println("FATAL: Cannot connect to MySQL. Check credentials in DatabaseConnection.java");
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        // 2. Start HTTP server
        try {
            new SimpleHttpServer().start();
        } catch (Exception e) {
            System.err.println("FATAL: Cannot start HTTP server: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Add shutdown hook to close DB
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
            System.out.println("Server stopped.");
        }));
    }
}
