package com.virtucio.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton manager for SQLite database connections and initialization.
 * 
 * Thread-safe implementation using double-checked locking pattern.
 * Handles schema creation and connection lifecycle management.
 * 
 * Architectural Decision: Singleton ensures single connection point,
 * preventing connection leaks and resource exhaustion.
 */
public class DatabaseManager {
    
    private static volatile DatabaseManager instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:contacts.db";
    private static final int DB_VERSION = 1;
    
    /**
     * Private constructor prevents direct instantiation.
     * Use getInstance() to obtain the singleton instance.
     */
    private DatabaseManager() {
        // Intentionally empty - lazy initialization in getInstance()
    }
    
    /**
     * Returns the singleton instance of DatabaseManager.
     * Thread-safe using double-checked locking pattern.
     * 
     * @return The singleton DatabaseManager instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Obtains a connection to the SQLite database.
     * Creates new connection if none exists or if previous connection closed.
     * 
     * Intent: Lazy connection initialization - only connects when needed.
     * Enables PRAGMA foreign_keys for referential integrity.
     * 
     * @return Active database connection
     * @throws DatabaseException if connection fails
     */
    public Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                
                // Enable foreign key constraints (disabled by default in SQLite)
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to establish database connection", e);
        }
    }
    
    /**
     * Initializes the database schema on first run.
     * Creates contacts table with proper constraints and indexes.
     * 
     * Intent: Idempotent operation - safe to call multiple times.
     * Uses CREATE TABLE IF NOT EXISTS for resilience.
     * 
     * Schema Design:
     * - id: Auto-incrementing primary key for internal use
     * - email: UNIQUE constraint enforces no duplicates at DB level
     * - timestamps: Audit trail for created/updated times
     * - indexes: Optimize search operations on name and email
     * 
     * @throws DatabaseException if schema creation fails
     */
    public void initializeDatabase() throws DatabaseException {
        try (Statement stmt = getConnection().createStatement()) {
            
            // Create contacts table with constraints
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.execute(createTableSQL);
            
            // Create index on email for fast unique lookups
            String createEmailIndexSQL = 
                "CREATE INDEX IF NOT EXISTS idx_email ON contacts(email)";
            stmt.execute(createEmailIndexSQL);
            
            // Create index on name for fast search (case-insensitive)
            String createNameIndexSQL = 
                "CREATE INDEX IF NOT EXISTS idx_name ON contacts(name COLLATE NOCASE)";
            stmt.execute(createNameIndexSQL);
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database schema", e);
        }
    }
    
    /**
     * Closes the database connection gracefully.
     * Should be called on application shutdown.
     * 
     * Intent: Cleanup resources to prevent connection leaks.
     * Non-critical if fails - connection will be released on JVM exit.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Log but don't throw - application is likely shutting down
            System.err.println("Warning: Failed to close database connection: " + e.getMessage());
        }
    }
    
    /**
     * Returns the current database version for migration management.
     * Future enhancement: Track schema versions for safe upgrades.
     * 
     * @return Current database schema version
     */
    public int getDatabaseVersion() {
        return DB_VERSION;
    }
}
