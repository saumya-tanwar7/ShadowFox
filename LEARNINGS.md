# Learnings — ShadowFox Java Internship (Beginner Level)

## 1. What was the hardest bug?

In the Student Information System GUI, selecting a row in the `JTable` to
edit it kept firing the selection listener multiple times per click (once
while the mouse was still down, and again on release), which briefly loaded
stale data into the form fields and made the "Update" button edit the wrong
row right after a delete.

A related issue in the Calculator: an early version used `double` for all
arithmetic, and the classic `0.1 + 0.2` check produced `0.30000000000000004`
instead of `0.3` — exactly the kind of rounding bug the task brief warns
about for financial/scientific calculations.

## 2. How did I fix it?

- **Table selection bug:** `ListSelectionEvent` fires while the selection is
  still changing (e.g. during a drag). Guarding the handler with
  `if (!e.getValueIsAdjusting())` ensures the form only refreshes once the
  selection has settled, and `table.clearSelection()` after a delete prevents
  a stale index from pointing at the wrong row.
- **Floating-point bug:** Replaced every `double`/`float` calculation in the
  calculator with `java.math.BigDecimal`, using a fixed `MathContext` for
  rounding on division and square roots. This is the standard fix recommended
  for any app that touches money or precise scientific values.

## Other notes

- Used `try/catch` around all user input parsing (`NumberFormatException`,
  `ArithmeticException`) so a bad input (letters instead of numbers, division
  by zero) is reported to the user instead of crashing the app.
- Used regex validation (`Pattern.matcher(...).matches()`) for phone numbers
  and emails in the Contact Manager rather than trusting raw input.
- Kept each task in its own package (`com.shadowfox.calculator`,
  `com.shadowfox.contacts`, `com.shadowfox.student`) to mirror how a real
  multi-feature codebase is organized.
