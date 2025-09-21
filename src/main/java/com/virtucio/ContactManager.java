// Three columns bound to Contact properties
TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

// Observable list for automatic UI updates
private ObservableList<Contact> contactData = FXCollections.observableArrayList();package com.virtucio;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ContactManager extends Application {

    private TableView<Contact> contactTable;
    private ObservableList<Contact> contactData = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Contact Manager");

        // Create the TableView
        setupTableView();

        // Create buttons
        HBox buttonBar = setupButtonBar();

        // Create the layout
        BorderPane root = new BorderPane();
        root.setCenter(contactTable);
        root.setBottom(buttonBar);
        root.setPadding(new Insets(10));

        // Add some sample data for testing
        addSampleData();

        // Create the scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTableView() {
        contactTable = new TableView<>();
        contactTable.setItems(contactData);

        // Create columns for Name, Phone, and Email
        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(250);

        TableColumn<Contact, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        phoneCol.setPrefWidth(150);

        TableColumn<Contact, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailCol.setPrefWidth(200);

        contactTable.getColumns().addAll(nameCol, phoneCol, emailCol);

        // Allow the table to fill the available space
        contactTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox setupButtonBar() {
        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button clearAllButton = new Button("Clear All");
        Button showTotalsButton = new Button("Show Totals");

        // Add button event handler - will open ContactForm dialog (Lian's part)
        addButton.setOnAction(e -> openAddContactDialog());

        // Edit button event handler - will open ContactForm dialog (Lian's part)
        editButton.setOnAction(e -> openEditContactDialog());

        // Delete button event handler
        deleteButton.setOnAction(e -> deleteSelectedContact());

        // Clear All button event handler - will call confirmation dialog (Lian's part)
        clearAllButton.setOnAction(e -> clearAllContacts());

        // Show Totals button event handler - will call info dialog (Lian's part)
        showTotalsButton.setOnAction(e -> showTotals());

        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));
        buttonBar.getChildren().addAll(addButton, editButton, deleteButton, clearAllButton, showTotalsButton);

        return buttonBar;
    }

    // Delete button functionality
    private void deleteSelectedContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            contactData.remove(selectedContact);
        } else {
            // Show alert if no contact is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to delete.");
            alert.showAndWait();
        }
    }

    // Clear All button functionality - will integrate with Lian's confirmation dialog
    private void clearAllContacts() {
        if (!contactData.isEmpty()) {
            // TODO: Call Lian's confirmation dialog here
            // For now, showing a simple confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Clear All");
            alert.setHeaderText("Clear All Contacts");
            alert.setContentText("Are you sure you want to delete all contacts?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    contactData.clear();
                }
            });
        } else {
            // Show info if no contacts exist
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Contacts");
            alert.setHeaderText("Contact List Empty");
            alert.setContentText("There are no contacts to clear.");
            alert.showAndWait();
        }
    }

    // Show Totals button functionality - will integrate with Lian's info dialog
    private void showTotals() {
        int totalContacts = contactData.size();
        // TODO: Call Lian's info dialog here
        // For now, showing a simple information dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Statistics");
        alert.setHeaderText("Total Contacts");
        alert.setContentText("Total number of contacts: " + totalContacts);
        alert.showAndWait();
    }

    // Add button functionality - will integrate with Lian's ContactForm dialog
    private void openAddContactDialog() {
        // TODO: Call Lian's ContactForm dialog for adding
        // For now, adding a sample contact
        System.out.println("Add button clicked - will open ContactForm dialog");
        // Temporary implementation for testing
        Contact newContact = new Contact("New Contact", "123-456-7890", "new@email.com");
        contactData.add(newContact);
    }

    // Edit button functionality - will integrate with Lian's ContactForm dialog
    private void openEditContactDialog() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // TODO: Call Lian's ContactForm dialog for editing
            System.out.println("Edit button clicked - will open ContactForm dialog for: " + selectedContact.getName());
        } else {
            // Show alert if no contact is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Contact Selected");
            alert.setContentText("Please select a contact to edit.");
            alert.showAndWait();
        }
    }

    // Add some sample data for testing
    private void addSampleData() {
        contactData.add(new Contact("John Doe", "555-0123", "john.doe@email.com"));
        contactData.add(new Contact("Jane Smith", "555-0456", "jane.smith@email.com"));
        contactData.add(new Contact("Bob Johnson", "555-0789", "bob.johnson@email.com"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
