package com.virtucio;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

            // Validation: all fields required
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                showErrorDialog("Validation Error", "All fields are required.");
                return;
            }
            // Validation: email must contain '@' and be a valid format
            if (!isValidEmail(email)) {
                showErrorDialog("Validation Error", "Please enter a valid email address.");
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
     * Checks if the email is in a valid format.
     */
    private static boolean isValidEmail(String email) {
        // Basic regex for email validation
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
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