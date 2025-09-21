package com.virtucio;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    // Name
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
    }

    // Phone
    public String getPhone() {
        return phone.get();
    }
    public void setPhone(String value) {
        phone.set(value);
    }
    public StringProperty phoneProperty() {
        return phone;
    }

    // Email
    public String getEmail() {
        return email.get();
    }
    public void setEmail(String value) {
        email.set(value);
    }
    public StringProperty emailProperty() {
        return email;
    }
}
