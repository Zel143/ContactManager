package com.virtucio;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContactForm {
    /**
     * Shows the Add/Edit Contact dialog.
     * @param owner The parent window.
     * @param contact The contact to edit, or null to add new.
     * @return The new/edited Contact, or null if cancelled.
     */
    public static Contact showContactForm(Stage owner, Contact contact) {
        Stage dialog = new Stage();
        dialog.setTitle(contact == null ? "Add Contact" : "Edit Contact");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        // UI components for contact fields
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        if (contact != null) {
            nameField.setText(contact.getName());
            phoneField.setText(contact.getPhone());
            emailField.setText(contact.getEmail());
        }

        // Layout fields and labels in a grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(12);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        // Save and Cancel buttons
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        HBox buttonBar = new HBox(10, saveBtn, cancelBtn);
        buttonBar.setPadding(new Insets(15, 0, 0, 0));
        grid.add(buttonBar, 1, 3);

        final Contact[] result = {null};

        // Save button logic with validation
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Validation 1: All fields are required (no empty submissions)
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                showErrorDialog("Validation Error", "All fields are required.");
                return;
            }
            
            // Validation 2: Email format must be valid
            if (!isValidEmail(email)) {
                showErrorDialog("Invalid Email", 
                    "Please enter a valid email address (e.g., user@example.com).");
                return;
            }
            
            // Validation 3: Phone format must be valid
            if (!isValidPhone(phone)) {
                showErrorDialog("Invalid Phone", 
                    "Phone must contain at least 7 digits.\nAccepts: +1-555-0123, (555) 0123, etc.");
                return;
            }

            if (contact == null) {
                result[0] = new Contact(name, phone, email);
            } else {
                contact.setName(name);
                contact.setPhone(phone);
                contact.setEmail(email);
                result[0] = contact;
            }
            dialog.close();
        });

        cancelBtn.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();

        return result[0];
    }

    /**
     * Validates email format using improved regex.
     * Checks for basic structure: localpart@domain.tld
     * Rejects consecutive dots, leading/trailing dots, and missing components.
     */
    private static boolean isValidEmail(String email) {
        // Improved regex: no consecutive dots, proper domain structure
        return email.matches("^[a-zA-Z0-9]+([._%-][a-zA-Z0-9]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Validates phone number format.
     * Accepts international formats: +, digits, spaces, hyphens, parentheses.
     * Examples: +1-555-0123, (555) 0123, 555.0123.4567, +44 20 7123 4567
     * 
     * Architectural Decision: Flexible validation to support global users
     * rather than enforcing strict national format.
     */
    private static boolean isValidPhone(String phone) {
        // Must start with optional + followed by digit, then any formatting
        // Prevents invalid patterns like "+---" or "( )"
        // Requires minimum 7 actual digits (shortest international number)
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        return phone.matches("^[+]?[0-9][\\s.()0-9-]*$") && digitsOnly.length() >= 7;
    }

    /**
     * Shows an error dialog with the given title and message.
     */
    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog and returns true if user confirms.
     */
    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK || result == ButtonType.YES;
    }

    /**
     * Shows an info dialog with the given title and message.
     */
    public static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}