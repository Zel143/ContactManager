package com.virtucio;

import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Contact - Data Model
 * 
 * Represents a single contact with name, phone, and email.
 * Uses JavaFX StringProperty for automatic UI binding and updates.
 * Implements equals/hashCode based on email (unique identifier).
 */
public class Contact {
    private final StringProperty name;
    private final StringProperty phone;
    private final StringProperty email;

    public Contact() {
        this(null, null, null);
    }

    public Contact(String name, String phone, String email) {
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
    }

    // ===== NAME PROPERTY =====
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
    }

    // ===== PHONE PROPERTY =====
    public String getPhone() {
        return phone.get();
    }
    public void setPhone(String value) {
        phone.set(value);
    }
    public StringProperty phoneProperty() {
        return phone;
    }

    // ===== EMAIL PROPERTY =====
    public String getEmail() {
        return email.get();
    }
    public void setEmail(String value) {
        email.set(value);
    }
    public StringProperty emailProperty() {
        return email;
    }

    // ===== OBJECT EQUALITY & IDENTITY =====
    
    /**
     * Two contacts are equal if they have the same email address.
     * Email is chosen as the unique identifier since it's most distinctive.
     * 
     * @param obj Object to compare with
     * @return true if emails match (case-insensitive)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        // Email comparison is case-insensitive for better UX
        return Objects.equals(
            getEmail() != null ? getEmail().toLowerCase() : null,
            contact.getEmail() != null ? contact.getEmail().toLowerCase() : null
        );
    }

    /**
     * Hash code based on email (lowercase for consistency with equals).
     * Required contract: equal objects must have equal hash codes.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getEmail() != null ? getEmail().toLowerCase() : null);
    }

    /**
     * String representation for debugging and logging.
     * Format: Contact{name='...', phone='...', email='...'}
     */
    @Override
    public String toString() {
        return String.format("Contact{name='%s', phone='%s', email='%s'}",
                getName(), getPhone(), getEmail());
    }
}
