package com.virtucio;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    // ===== CORE DATA COMPONENTS =====
    private TableView<Contact> contactTable;
    // Observable list automatically updates UI when data changes
    private ObservableList<Contact> contactData = FXCollections.observableArrayList();

    /**
     * Main application startup method
     * Sets up the entire UI and shows the window
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Contact Manager");

        // Create the main components
        setupTableView();  // Set up the contact table
        HBox buttonBar = setupButtonBar();  // Create all buttons

        // Create the main layout structure
        BorderPane root = new BorderPane();
        root.setCenter(contactTable);    // Table takes up most of the space
        root.setBottom(buttonBar);       // Buttons at the bottom
        root.setPadding(new Insets(10));  // 10px padding around everything

        // Add sample data for testing the application
        addSampleData();

        // Create and show the window
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * RANZEL'S MAIN RESPONSIBILITY: TableView Setup
     * Creates the table with 3 columns: Name, Phone, Email
     * Binds columns to Contact model properties for automatic updates
     */
    private void setupTableView() {
        contactTable = new TableView<>();
        contactTable.setItems(contactData);  // Connect table to our data list

        // Create the three required columns
        // Name column - wider since names can be long
        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(250);

        // Phone column - medium width for phone numbers
        TableColumn<Contact, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        phoneCol.setPrefWidth(150);

        // Email column - wider for email addresses
        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailCol.setPrefWidth(200);

        // Add all columns to the table
        contactTable.getColumns().addAll(nameCol, phoneCol, emailCol);

        // Make table resize columns automatically to fill available space
        contactTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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

        // Create horizontal layout for buttons
        HBox buttonBar = new HBox(10);  // 10px spacing between buttons
        buttonBar.setPadding(new Insets(10, 0, 0, 0));  // 10px top padding
        buttonBar.getChildren().addAll(addButton, editButton, deleteButton, clearAllButton, showTotalsButton);

        return buttonBar;
    }

    // ===== RANZEL'S CORE BUTTON FUNCTIONALITY =====

    /**
     * DELETE BUTTON LOGIC - Fully implemented by Ranzel
     * Removes the selected contact from the list
     * Shows warning if no contact is selected
     */
    private void deleteSelectedContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // Remove from observable list - UI updates automatically
            contactData.remove(selectedContact);
        } else {
            // User-friendly error message
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to delete.");
            alert.showAndWait();
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
                contactData.clear();  // Remove all contacts
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
        int totalContacts = contactData.size();
        // Use Lian's custom info dialog
        ContactForm.showInfoDialog("Contact Statistics",
                "Total number of contacts: " + totalContacts);
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

        // If user didn't cancel, add the new contact to the list
        if (newContact != null) {
            contactData.add(newContact);
        }
    }

    /**
     * EDIT BUTTON INTEGRATION
     * Now properly integrated with Lian's ContactForm dialog
     */
    private void openEditContactDialog() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // Get the primary stage to pass as owner
            Stage primaryStage = (Stage) contactTable.getScene().getWindow();

            // Call Lian's ContactForm dialog for editing the selected contact
            Contact editedContact = ContactForm.showContactForm(primaryStage, selectedContact);

            // The contact object is modified in place, so the table will update automatically
            // due to the property bindings in the Contact model
            if (editedContact != null) {
                // Force table refresh to show updated values
                contactTable.refresh();
            }
        } else {
            // Use Lian's error dialog for better consistency
            ContactForm.showErrorDialog("No Selection", "Please select a contact to edit.");
        }
    }

    /**
     * SAMPLE DATA FOR TESTING
     * Adds 3 test contacts so you can immediately see the application working
     */
    private void addSampleData() {
        contactData.add(new Contact("John Doe", "555-0123", "john.doe@email.com"));
        contactData.add(new Contact("Jane Smith", "555-0456", "jane.smith@email.com"));
        contactData.add(new Contact("Bob Johnson", "555-0789", "bob.johnson@email.com"));
    }

    /**
     * Main method - starts the JavaFX application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
