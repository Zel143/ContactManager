package com.virtucio;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Contact {
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty address;

    public Contact() {
        this(null, null, null, null, null);
    }

    public Contact(String firstName, String lastName, String phoneNumber, String email, String address) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
    }

    // First Name
    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String value) {
        firstName.set(value);
    }
    public StringProperty firstNameProperty() {
        return firstName;
    }

    // Last Name
    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String value) {
        lastName.set(value);
    }
    public StringProperty lastNameProperty() {
        return lastName;
    }

    // Phone Number
    public String getPhoneNumber() {
        return phoneNumber.get();
    }
    public void setPhoneNumber(String value) {
        phoneNumber.set(value);
    }
    public StringProperty phoneNumberProperty() {
        return phoneNumber;
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

    // Address
    public String getAddress() {
        return address.get();
    }
    public void setAddress(String value) {
        address.set(value);
    }
    public StringProperty addressProperty() {
        return address;
    }
}
