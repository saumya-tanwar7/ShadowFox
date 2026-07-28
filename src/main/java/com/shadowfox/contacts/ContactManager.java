package com.shadowfox.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Task 2: Simple Contact Management System
 *
 * CRUD operations over an in-memory ArrayList of Contact objects.
 *
 * Engineering notes:
 *  - ArrayList is used (not LinkedList) because contacts are searched/read
 *    far more often than inserted/removed from the middle of the list.
 *  - Duplicate phone numbers are rejected on add.
 *  - Search is case-insensitive ("John" also matches "john").
 *  - Email format is validated with a regex before a contact is accepted.
 */
public class ContactManager {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-() ]{7,15}$");

    private final List<Contact> contacts = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ContactManager().run();
    }

    public void run() {
        System.out.println("=== ShadowFox Contact Manager ===");
        boolean keepGoing = true;

        while (keepGoing) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addContact();
                case "2" -> viewContacts();
                case "3" -> updateContact();
                case "4" -> deleteContact();
                case "5" -> searchContact();
                case "0" -> keepGoing = false;
                default -> System.out.println("Please choose a valid option.");
            }
            if (keepGoing) System.out.println();
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private void printMenu() {
        System.out.println("""
                Choose an option:
                  1) Add contact
                  2) View all contacts
                  3) Update contact
                  4) Delete contact
                  5) Search contact by name
                  0) Exit
                """);
        System.out.print("Your choice: ");
    }

    private void addContact() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        String phone = readValidPhone();
        if (phone == null) return; // validation message already printed

        if (isDuplicatePhone(phone)) {
            System.out.println("A contact with this phone number already exists. Not adding duplicate.");
            return;
        }

        String email = readValidEmail();
        if (email == null) return;

        contacts.add(new Contact(name, phone, email));
        System.out.println("Contact added.");
    }

    private String readValidPhone() {
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            System.out.println("Invalid phone number. Use digits only (7-15 characters, e.g. 9876543210).");
            return null;
        }
        return phone;
    }

    private String readValidEmail() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("Invalid email format.");
            return null;
        }
        return email;
    }

    private boolean isDuplicatePhone(String phone) {
        return contacts.stream().anyMatch(c -> c.getPhone().equals(phone));
    }

    private void viewContacts() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts saved yet.");
            return;
        }
        System.out.printf("%-20s %-15s %-30s%n", "NAME", "PHONE", "EMAIL");
        contacts.forEach(System.out::println);
    }

    private void updateContact() {
        System.out.print("Enter the name of the contact to update: ");
        String name = scanner.nextLine().trim();
        Contact contact = findByName(name);

        if (contact == null) {
            System.out.println("No contact found with that name.");
            return;
        }

        System.out.println("Leave a field blank to keep its current value.");

        System.out.print("New phone (" + contact.getPhone() + "): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("Invalid phone number, keeping the old value.");
            } else {
                contact.setPhone(phone);
            }
        }

        System.out.print("New email (" + contact.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("Invalid email, keeping the old value.");
            } else {
                contact.setEmail(email);
            }
        }

        System.out.println("Contact updated.");
    }

    private void deleteContact() {
        System.out.print("Enter the name of the contact to delete: ");
        String name = scanner.nextLine().trim();
        Contact contact = findByName(name);

        if (contact == null) {
            System.out.println("No contact found with that name.");
            return;
        }

        System.out.print("Are you sure you want to delete " + contact.getName() + "? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            contacts.remove(contact);
            System.out.println("Contact deleted.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void searchContact() {
        System.out.print("Enter a name to search for: ");
        String query = scanner.nextLine().trim().toLowerCase();

        List<Contact> matches = contacts.stream()
                .filter(c -> c.getName().toLowerCase().contains(query))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("No matching contacts found.");
        } else {
            System.out.printf("%-20s %-15s %-30s%n", "NAME", "PHONE", "EMAIL");
            matches.forEach(System.out::println);
        }
    }

    /** Case-insensitive exact-name lookup, used by update/delete. */
    private Contact findByName(String name) {
        return contacts.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
