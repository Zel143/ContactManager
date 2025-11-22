package com.virtucio;

import com.virtucio.database.ContactDAO;
import com.virtucio.database.DatabaseException;
import com.virtucio.database.DatabaseManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * ContactManager - Main Application Window
 *
 * Ranzel's Responsibilities:
 * 1. Contact.java data model with Name, Phone, Email properties
 * 2. Main window with TableView showing 3 columns
 * 3. All button layout and functionality
 * 4. Core contact management logic
 */
public class ContactManager extends Application {

    // ===== UI DIMENSION CONSTANTS =====
    // Intent: Centralize magic numbers for easy maintenance and semantic meaning
    private static final double WINDOW_WIDTH = 800.0;
    private static final double WINDOW_HEIGHT = 600.0;
    private static final double NAME_COLUMN_WIDTH = 250.0;
    private static final double PHONE_COLUMN_WIDTH = 150.0;
    private static final double EMAIL_COLUMN_WIDTH = 200.0;
    private static final double BUTTON_SPACING = 10.0;
    private static final double MAIN_PADDING = 10.0;

    // ===== CORE DATA COMPONENTS =====
    private TableView<Contact> contactTable;
    // Observable list automatically updates UI when data changes
    // Populated from database on startup
    private ObservableList<Contact> contactData;
    
    // ===== DATABASE COMPONENTS =====
    private ContactDAO contactDAO;
    private TextField searchField;

    /**
     * Main application startup method
     * Sets up the entire UI and shows the window
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Contact Manager");

        // ===== PHASE 1: DATABASE INITIALIZATION =====
        // Initialize database FIRST before any UI operations
        try {
            DatabaseManager.getInstance().initializeDatabase();
            contactDAO = new ContactDAO();
            
            // Load existing contacts from database
            contactData = contactDAO.getAllContacts();
            
        } catch (DatabaseException e) {
            // CRITICAL ERROR: Cannot run without database
            ContactForm.showErrorDialog("Database Initialization Failed",
                "Failed to initialize database: " + e.getMessage() + 
                "\n\nApplication will now exit.");
            Platform.exit();
            return;
        }

        // ===== PHASE 2: UI SETUP =====
        // Create the main components
        HBox searchBar = setupSearchBar();  // Search functionality at top
        setupTableView();                   // Set up the contact table
        HBox buttonBar = setupButtonBar();  // Create all buttons

        // Create the main layout structure
        BorderPane root = new BorderPane();
        root.setTop(searchBar);          // Search bar at top
        root.setCenter(contactTable);    // Table takes up most of the space
        root.setBottom(buttonBar);       // Buttons at the bottom
        root.setPadding(new Insets(MAIN_PADDING));

        // Create and show the window
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        
        // Register shutdown hook to close database connection cleanly
        primaryStage.setOnCloseRequest(e -> DatabaseManager.getInstance().closeConnection());
        
        primaryStage.show();
    }

    /**
     * Sets up the search bar with real-time filtering.
     * 
     * Intent: Allow users to quickly find contacts as they type.
     * Searches name, phone, and email fields using SQL LIKE.
     */
    private HBox setupSearchBar() {
        HBox searchBar = new HBox(BUTTON_SPACING);
        searchBar.setPadding(new Insets(0, 0, BUTTON_SPACING, 0));
        searchBar.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Type to search name, phone, or email...");
        searchField.setPrefWidth(300);
        
        // Real-time search as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
        
        Button clearSearchButton = new Button("Clear");
        clearSearchButton.setOnAction(e -> {
            searchField.clear();
            performSearch("");  // Show all contacts
        });
        
        searchBar.getChildren().addAll(searchLabel, searchField, clearSearchButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        return searchBar;
    }
    
    /**
     * Performs database search and updates the table.
     * Empty search term shows all contacts.
     * 
     * Intent: Leverage database indexing for fast search even with large datasets.
     */
    private void performSearch(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                // Empty search - show all contacts
                contactData = contactDAO.getAllContacts();
            } else {
                // Filter contacts using database search
                contactData = contactDAO.searchContacts(searchTerm.trim());
            }
            // Update table with new results
            contactTable.setItems(contactData);
        } catch (DatabaseException e) {
            ContactForm.showErrorDialog("Search Error",
                "Failed to search contacts: " + e.getMessage());
        }
    }

    /**
     * RANZEL'S MAIN RESPONSIBILITY: TableView Setup
     * Creates the table with 3 columns: Name, Phone, Email
     * Binds columns to Contact model properties for automatic updates
     */
    private void setupTableView() {
        contactTable = new TableView<>();
        contactTable.setItems(contactData);  // Connect table to our data list

        // UX Enhancement: Show helpful message when table is empty
        Label placeholder = new Label("No contacts yet.\nClick 'Add' to create your first contact.");
        placeholder.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        contactTable.setPlaceholder(placeholder);

        // Create the three required columns
        // Intent: Column widths are now constants for easy adjustment
        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(NAME_COLUMN_WIDTH);

        TableColumn<Contact, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        phoneCol.setPrefWidth(PHONE_COLUMN_WIDTH);

        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailCol.setPrefWidth(EMAIL_COLUMN_WIDTH);

        // Add all columns to the table
        contactTable.getColumns().add(nameCol);
        contactTable.getColumns().add(phoneCol);
        contactTable.getColumns().add(emailCol);

        // Make table resize columns automatically to fill available space
        // Using modern callback-based resize policy (replaces deprecated CONSTRAINED_RESIZE_POLICY)
        contactTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    /**
     * RANZEL'S MAIN RESPONSIBILITY: Button Bar Setup
     * Creates all 5 required buttons with their event handlers
     */
    private HBox setupButtonBar() {
        // Create all required buttons
        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button clearAllButton = new Button("Clear All");
        Button showTotalsButton = new Button("Show Totals");

        // ===== BUTTON EVENT HANDLERS =====

        // Add button - will integrate with Lian's ContactForm dialog
        addButton.setOnAction(e -> openAddContactDialog());

        // Edit button - will integrate with Lian's ContactForm dialog
        editButton.setOnAction(e -> openEditContactDialog());

        // Delete button - RANZEL'S IMPLEMENTATION (complete)
        deleteButton.setOnAction(e -> deleteSelectedContact());

        // Clear All button - RANZEL'S IMPLEMENTATION (will integrate with Lian's confirmation)
        clearAllButton.setOnAction(e -> clearAllContacts());

        // Show Totals button - RANZEL'S IMPLEMENTATION (will integrate with Lian's info dialog)
        showTotalsButton.setOnAction(e -> showTotals());

        // Layout buttons horizontally with spacing
        HBox buttonBar = new HBox(BUTTON_SPACING);
        buttonBar.setPadding(new Insets(BUTTON_SPACING, 0, 0, 0));
        buttonBar.getChildren().addAll(addButton, editButton, deleteButton, clearAllButton, showTotalsButton);

        return buttonBar;
    }

    // ===== RANZEL'S CORE BUTTON FUNCTIONALITY =====

    /**
     * DELETE BUTTON LOGIC - Fully implemented by Ranzel
     * Removes the selected contact from the list with confirmation.
     * 
     * Intent: Prevent accidental deletion by requiring confirmation.
     * Matches UX pattern established by Clear All button.
     */
    private void deleteSelectedContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // Ask for confirmation before destructive operation
            String confirmMessage = String.format(
                "Are you sure you want to delete this contact?\n\nName: %s\nEmail: %s",
                selectedContact.getName(), selectedContact.getEmail()
            );
            
            if (ContactForm.showConfirmationDialog("Confirm Delete", confirmMessage)) {
                try {
                    // Delete from database
                    if (contactDAO.deleteContact(selectedContact.getEmail())) {
                        // Remove from UI list
                        contactData.remove(selectedContact);
                    } else {
                        ContactForm.showErrorDialog("Delete Failed",
                            "Contact not found in database.");
                    }
                } catch (DatabaseException e) {
                    ContactForm.showErrorDialog("Database Error",
                        "Failed to delete contact: " + e.getMessage());
                }
            }
        } else {
            // Use Lian's error dialog helper for consistency
            ContactForm.showErrorDialog("No Selection", 
                "Please select a contact from the table to delete.");
        }
    }

    /**
     * CLEAR ALL BUTTON LOGIC - Ranzel's implementation
     * Now integrated with Lian's confirmation dialog
     */
    private void clearAllContacts() {
        if (!contactData.isEmpty()) {
            // Use Lian's confirmation dialog
            if (ContactForm.showConfirmationDialog("Confirm Clear All",
                    "Are you sure you want to delete all contacts?")) {
                try {
                    // Delete all from database
                    int deleted = contactDAO.deleteAllContacts();
                    // Clear UI list
                    contactData.clear();
                    
                    ContactForm.showInfoDialog("Contacts Cleared",
                        String.format("%d contact(s) deleted successfully.", deleted));
                } catch (DatabaseException e) {
                    ContactForm.showErrorDialog("Database Error",
                        "Failed to clear contacts: " + e.getMessage());
                }
            }
        } else {
            // Use Lian's info dialog to inform user if list is already empty
            ContactForm.showInfoDialog("No Contacts", "There are no contacts to clear.");
        }
    }

    /**
     * SHOW TOTALS BUTTON LOGIC - Ranzel's implementation
     * Now integrated with Lian's info dialog
     */
    private void showTotals() {
        try {
            // Use database count for accuracy (in case of filtered view)
            int totalContacts = contactDAO.getContactCount();
            int displayedContacts = contactData.size();
            
            String message;
            if (totalContacts == displayedContacts) {
                message = String.format("Total contacts: %d", totalContacts);
            } else {
                // User is viewing filtered results
                message = String.format(
                    "Showing: %d of %d total contacts\n(Search filter active)",
                    displayedContacts, totalContacts);
            }
            
            ContactForm.showInfoDialog("Contact Statistics", message);
        } catch (DatabaseException e) {
            ContactForm.showErrorDialog("Database Error",
                "Failed to get contact count: " + e.getMessage());
        }
    }

    // ===== INTEGRATION POINTS FOR LIAN'S DIALOGS =====

    /**
     * ADD BUTTON INTEGRATION
     * Now properly integrated with Lian's ContactForm dialog
     */
    private void openAddContactDialog() {
        // Get the primary stage to pass as owner
        Stage primaryStage = (Stage) contactTable.getScene().getWindow();

        // Call Lian's ContactForm dialog for adding new contacts
        Contact newContact = ContactForm.showContactForm(primaryStage, null);

        // If user didn't cancel, insert into database
        if (newContact != null) {
            try {
                // Insert into database - UNIQUE constraint prevents duplicates
                if (contactDAO.insertContact(newContact)) {
                    // Success - refresh view to show new contact
                    performSearch(searchField.getText());
                } else {
                    // Duplicate email detected by database
                    ContactForm.showErrorDialog("Duplicate Contact",
                        String.format("""
                            A contact with email '%s' already exists.
                            Each contact must have a unique email address.""",
                            newContact.getEmail()));
                }
            } catch (DatabaseException e) {
                ContactForm.showErrorDialog("Database Error",
                    "Failed to save contact: " + e.getMessage());
            }
        }
    }

    /**
     * EDIT BUTTON INTEGRATION
     * Now properly integrated with Lian's ContactForm dialog
     */
    private void openEditContactDialog() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // Store original email to identify record in database
            String originalEmail = selectedContact.getEmail();
            
            // Get the primary stage to pass as owner
            Stage primaryStage = (Stage) contactTable.getScene().getWindow();

            // Call Lian's ContactForm dialog for editing the selected contact
            Contact editedContact = ContactForm.showContactForm(primaryStage, selectedContact);

            if (editedContact != null) {
                try {
                    // Update in database using original email as identifier
                    if (contactDAO.updateContact(editedContact, originalEmail)) {
                        // Success - refresh view to show updated contact
                        contactTable.refresh();
                        performSearch(searchField.getText());
                    } else {
                        ContactForm.showErrorDialog("Update Failed",
                            "Contact not found in database or email already exists.");
                    }
                } catch (DatabaseException e) {
                    ContactForm.showErrorDialog("Database Error",
                        "Failed to update contact: " + e.getMessage());
                }
            }
        } else {
            // Use Lian's error dialog for better consistency
            ContactForm.showErrorDialog("No Selection", "Please select a contact to edit.");
        }
    }

    /**
     * Main method - starts the JavaFX application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
