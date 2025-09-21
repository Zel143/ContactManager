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
     * Will integrate with Lian's confirmation dialog
     * Currently uses temporary confirmation dialog
     */
    private void clearAllContacts() {
        if (!contactData.isEmpty()) {
            // TODO: Replace this with Lian's confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Clear All");
            alert.setHeaderText("Clear All Contacts");
            alert.setContentText("Are you sure you want to delete all contacts?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    contactData.clear();  // Remove all contacts
                }
            });
        } else {
            // Inform user if list is already empty
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contacts");
            alert.setHeaderText("Contact List Empty");
            alert.setContentText("There are no contacts to clear.");
            alert.showAndWait();
        }
    }

    /**
     * SHOW TOTALS BUTTON LOGIC - Ranzel's implementation
     * Will integrate with Lian's info dialog
     * Currently uses temporary information dialog
     */
    private void showTotals() {
        int totalContacts = contactData.size();
        // TODO: Replace this with Lian's custom info dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Statistics");
        alert.setHeaderText("Total Contacts");
        alert.setContentText("Total number of contacts: " + totalContacts);
        alert.showAndWait();
    }

    // ===== INTEGRATION POINTS FOR LIAN'S DIALOGS =====

    /**
     * ADD BUTTON INTEGRATION POINT
     * This method will call Lian's ContactForm dialog for adding new contacts
     * Currently has temporary implementation for testing
     */
    private void openAddContactDialog() {
        // TODO: Replace with Lian's ContactForm dialog call
        System.out.println("Add button clicked - will open ContactForm dialog");

        // Temporary implementation for testing - adds a sample contact
        Contact newContact = new Contact("New Contact", "123-456-7890", "new@email.com");
        contactData.add(newContact);
    }

    /**
     * EDIT BUTTON INTEGRATION POINT
     * This method will call Lian's ContactForm dialog for editing existing contacts
     * Includes proper validation for contact selection
     */
    private void openEditContactDialog() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // TODO: Replace with Lian's ContactForm dialog call
            // Pass the selected contact to the dialog for editing
            System.out.println("Edit button clicked - will open ContactForm dialog for: " + selectedContact.getName());
        } else {
            // Show error if no contact is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to edit.");
            alert.showAndWait();
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
