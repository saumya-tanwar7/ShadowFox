package com.shadowfox.contacts;

/**
 * Plain Old Java Object representing a single contact.
 * Fields are private (encapsulation) and only reachable through
 * getters/setters, so validation always happens in one place.
 */
public class Contact {

    private String name;
    private String phone;
    private String email;

    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-15s %-30s", name, phone, email);
    }
}
