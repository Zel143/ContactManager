package com.virtucio;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ContactForm {

    public static Contact showContactForm(Stage owner, Contact contact) {
        Stage dialog = new Stage();
        dialog.setTitle(contact == null ? "Add Contact" : "Edit Contact");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        // ===== UI COMPONENTS =====
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        if (contact != null) {
            nameField.setText(contact.getName());
            phoneField.setText(contact.getPhone());
            emailField.setText(contact.getEmail());
        }

        // Labels and fields arranged in grid
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

        // Buttons
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        HBox buttonBar = new HBox(10, saveBtn, cancelBtn);
        buttonBar.setPadding(new Insets(15, 0, 0, 0));
        grid.add(buttonBar, 1, 3);

        // ===== BUTTON LOGIC =====
        final Contact[] result = {null};

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Validation
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                showErrorDialog("Validation Error", "All fields are required.");
                return;
            }
            if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
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

    // ===== REUSABLE DIALOG HELPERS =====

    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
