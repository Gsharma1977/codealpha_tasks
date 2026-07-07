package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton class that manages JDBC connection.
 * Uses singleton pattern so only ONE connection object exists at a time.
 */
public class DatabaseConnection {

    // ── JDBC connection parameters ─────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/data_redundancy_db?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "root123";   // ← change to your MySQL password

    private static Connection connection = null;

    // Private constructor prevents instantiation
    private DatabaseConnection() {}

    /**
     * Returns the single shared Connection.
     * Creates it on first call; returns the cached one afterward.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java.jar to your classpath.", e);
            }
        }
        return connection;
    }

    /** Call this when the application shuts down. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            }
        }
    }
}
