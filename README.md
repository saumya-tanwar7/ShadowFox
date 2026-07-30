# ShadowFox Java Development Internship — Beginner Level

I have done all the 3 tasks although only 2 were necessary to be done.

| # | Task | Package | Type |
|---|------|---------|------|
| 1 | Enhanced Console Calculator | `com.shadowfox.calculator` | Console |
| 2 | Contact Management System | `com.shadowfox.contacts` | Console |
| 3 | Student Information System | `com.shadowfox.student` | GUI (Swing) |


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
