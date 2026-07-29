# ShadowFox Java Internship — Beginner Level (Foundations)

I have done all the 3 tasks although only 2 were necessary to be done.

| # | Task | Package | Type |
|---|------|---------|------|
| 1 | Enhanced Console Calculator | `com.shadowfox.calculator` | Console |
| 2 | Contact Management System | `com.shadowfox.contacts` | Console |
| 3 | Student Information System | `com.shadowfox.student` | GUI (Swing) |

## Requirements

- Java 17 or later (JDK, not just JRE — you need `javac`).
- No external dependencies. Everything uses the standard library only
  (`java.math.BigDecimal`, `java.util.regex`, `javax.swing`).

## How to run

### Option A — Maven

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.shadowfox.calculator.Calculator"
mvn exec:java -Dexec.mainClass="com.shadowfox.contacts.ContactManager"
mvn exec:java -Dexec.mainClass="com.shadowfox.student.StudentInfoSystemGUI"
```

(Add the `exec-maven-plugin` to `pom.xml` if it isn't already configured on
your machine, or just use Option B below — it needs no plugin at all.)

### Option B — Plain `javac` / `java` (no Maven needed)

From the project root:

```bash
# Compile everything
javac -d out $(find src -name "*.java")

# Task 1: Calculator
java -cp out com.shadowfox.calculator.Calculator

# Task 2: Contact Manager
java -cp out com.shadowfox.contacts.ContactManager

# Task 3: Student Information System (opens a window)
java -cp out com.shadowfox.student.StudentInfoSystemGUI
```

## Task 1 — Enhanced Console Calculator

- Basic arithmetic (+, -, *, /) using `BigDecimal` throughout, so classic
  floating-point bugs like `0.1 + 0.2 != 0.3` don't happen.
- Scientific operations: square root, exponentiation.
- Unit conversions: temperature (Celsius / Fahrenheit / Kelvin) and a demo
  USD ↔ INR currency conversion (fixed rate — swap in a live FX API for
  production use).
- Handles bad input (e.g. typing `abc` instead of a number) without crashing,
  and loops until the user chooses to exit.

## Task 2 — Contact Management System

- In-memory CRUD (Create, Read, Update, Delete) over an `ArrayList<Contact>`.
- `Contact` is a POJO with private fields and public getters/setters
  (encapsulation).
- Validation:
  - Phone numbers must match a digits/format regex, 7–15 characters.
  - Emails must match a standard email regex.
  - Duplicate phone numbers are rejected on add.
- Search by name is case-insensitive and matches partial names
  (`"john"` matches `"John Doe"`).
- Delete asks for confirmation before removing a contact.

## Task 3 — Student Information System (Swing GUI)

- MVC-style structure: `Student` (model), the `JTable` + form (view), and the
  `ActionListener`s in `StudentInfoSystemGUI` (controller).
- Add / Update / Delete student records through the form and table selection.
- Marks are validated to be numeric and within 0–100.
- Grades are auto-calculated from marks (A/B/C/D/F).
- Rows for failing students (grade F) are highlighted in red — conditional
  formatting driven directly off the model.
- Delete requires a confirmation dialog.
- Layout uses `BorderLayout`/`GridBagLayout` so buttons stay visible and
  usable when the window is resized.

## Project structure

```
ShadowFox-Internship/
├── pom.xml
├── README.md
├── LEARNINGS.md
└── src/main/java/com/shadowfox/
    ├── calculator/Calculator.java
    ├── contacts/
    │   ├── Contact.java
    │   └── ContactManager.java
    └── student/
        ├── Student.java
        └── StudentInfoSystemGUI.java
```
