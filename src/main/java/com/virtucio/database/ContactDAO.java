package com.virtucio.database;

import com.virtucio.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object (DAO) for Contact CRUD operations.
 * 
 * Encapsulates all SQL logic and provides clean API for UI layer.
 * Uses PreparedStatement throughout to prevent SQL injection attacks.
 * 
 * Architectural Decision: Returns ObservableList for seamless JavaFX
 * TableView integration while maintaining database as source of truth.
 */
public class ContactDAO {
    
    private final DatabaseManager dbManager;
    
    /**
     * Creates a new ContactDAO with reference to database manager.
     */
    public ContactDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Retrieves all contacts from the database.
     * Returns JavaFX ObservableList for automatic UI updates.
     * 
     * Intent: Load all contacts on application startup.
     * Ordered by name for consistent display.
     * 
     * @return ObservableList of all contacts, empty if none exist
     * @throws DatabaseException if query fails
     */
    public ObservableList<Contact> getAllContacts() throws DatabaseException {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String query = "SELECT name, phone, email FROM contacts ORDER BY name COLLATE NOCASE";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Contact contact = new Contact(
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
                contacts.add(contact);
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve contacts from database", e);
        }
        
        return contacts;
    }
    
    /**
     * Inserts a new contact into the database.
     * 
     * Intent: Add contact with duplicate detection at DB level.
     * UNIQUE constraint on email prevents duplicates automatically.
     * 
     * @param contact Contact to insert (must have all fields populated)
     * @return true if inserted successfully, false if duplicate email exists
     * @throws DatabaseException if insertion fails for reasons other than duplicate
     */
    public boolean insertContact(Contact contact) throws DatabaseException {
        String query = "INSERT INTO contacts (name, phone, email, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Bind parameters - prevents SQL injection
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhone());
            pstmt.setString(3, contact.getEmail());
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            // Check if error is UNIQUE constraint violation (duplicate email)
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return false;  // Not an error - just a duplicate
            }
            // All other SQL errors are real problems
            throw new DatabaseException("Failed to insert contact into database", e);
        }
    }
    
    /**
     * Updates an existing contact in the database.
     * Uses oldEmail to identify the record since email is the unique key.
     * 
     * Intent: Support editing all fields including email itself.
     * If email changes, oldEmail locates the record to update.
     * 
     * @param contact Contact with updated values
     * @param oldEmail Original email to identify the record
     * @return true if update successful, false if contact not found
     * @throws DatabaseException if update fails
     */
    public boolean updateContact(Contact contact, String oldEmail) throws DatabaseException {
        String query = "UPDATE contacts SET name=?, phone=?, email=?, updated_at=CURRENT_TIMESTAMP WHERE email=?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhone());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, oldEmail);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns false if no matching record found
            
        } catch (SQLException e) {
            // Check for UNIQUE constraint if email was changed to existing one
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return false;  // New email already exists
            }
            throw new DatabaseException("Failed to update contact in database", e);
        }
    }
    
    /**
     * Deletes a contact from the database by email.
     * 
     * Intent: Remove contact permanently (hard delete).
     * Future enhancement: Implement soft delete with deleted_at column.
     * 
     * @param email Email of contact to delete (unique identifier)
     * @return true if deleted, false if contact not found
     * @throws DatabaseException if deletion fails
     */
    public boolean deleteContact(String email) throws DatabaseException {
        String query = "DELETE FROM contacts WHERE email = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete contact from database", e);
        }
    }
    
    /**
     * Deletes all contacts from the database.
     * 
     * Intent: Support "Clear All" button functionality.
     * Uses TRUNCATE-equivalent DELETE for complete wipe.
     * 
     * @return Number of contacts deleted
     * @throws DatabaseException if deletion fails
     */
    public int deleteAllContacts() throws DatabaseException {
        String query = "DELETE FROM contacts";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            return stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete all contacts from database", e);
        }
    }
    
    /**
     * Searches contacts by name or email using SQL LIKE.
     * Case-insensitive search with wildcard matching.
     * 
     * Intent: Support search/filter functionality.
     * Searches both name and email fields for maximum flexibility.
     * 
     * Examples:
     * - searchTerm "john" matches "John Doe" and "john@example.com"
     * - searchTerm "555" matches phone numbers containing 555
     * 
     * @param searchTerm Text to search for (supports partial matches)
     * @return ObservableList of matching contacts
     * @throws DatabaseException if search fails
     */
    public ObservableList<Contact> searchContacts(String searchTerm) throws DatabaseException {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        
        // Search across name, phone, and email fields
        String query = """
            SELECT name, phone, email FROM contacts 
            WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?
            ORDER BY name COLLATE NOCASE
            """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Add wildcards for partial matching
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contact contact = new Contact(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                    );
                    contacts.add(contact);
                }
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search contacts in database", e);
        }
        
        return contacts;
    }
    
    /**
     * Returns the total number of contacts in the database.
     * Optimized COUNT query - faster than loading all contacts.
     * 
     * Intent: Support "Show Totals" functionality efficiently.
     * 
     * @return Total count of contacts
     * @throws DatabaseException if count fails
     */
    public int getContactCount() throws DatabaseException {
        String query = "SELECT COUNT(*) FROM contacts";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count contacts in database", e);
        }
    }
    
    /**
     * Checks if a contact with the given email exists.
     * Efficient existence check without loading full contact data.
     * 
     * @param email Email to check
     * @return true if contact exists, false otherwise
     * @throws DatabaseException if check fails
     */
    public boolean contactExists(String email) throws DatabaseException {
        String query = "SELECT 1 FROM contacts WHERE email = ? LIMIT 1";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();  // Returns true if any row found
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check if contact exists", e);
        }
    }
}
