#**This script is generated for an easy and quick test run of all the tasks**


# What it does:
#   1. Compiles all Java source files into ./out
#   2. Feeds scripted input into the Calculator and Contact Manager
#      (the console apps) and checks the output contains what we expect.
#   3. Prints PASS/FAIL for each check and a final summary.
#
# The Student Information System is a GUI (Swing) and can't be driven this
# way from a terminal — for that see the "Manual GUI checklist" printed at the end.
#
# Usage:
#   chmod +x run_tests.sh   (only required once)
#   ./run_tests.sh

set -u

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

PASS_COUNT=0
FAIL_COUNT=0

pass() {
    echo "  ✅ PASS: $1"
    PASS_COUNT=$((PASS_COUNT + 1))
}

fail() {
    echo "  ❌ FAIL: $1"
    FAIL_COUNT=$((FAIL_COUNT + 1))
}

# Runs a java class with given stdin input, checks that ALL of the given
# substrings appear somewhere in stdout+stderr.
# Usage: check_contains "test name" "class.Fully.Qualified" "input\nlines\n" "expected substring 1" "expected substring 2" ...
check_contains() {
    local test_name="$1"
    local class_name="$2"
    local input="$3"
    shift 3
    local expected=("$@")

    local output
    output="$(printf "%b" "$input" | java -cp out "$class_name" 2>&1)"

    local all_found=true
    for needle in "${expected[@]}"; do
        if ! grep -qF -- "$needle" <<< "$output"; then
            all_found=false
            echo "     (missing expected text: \"$needle\")"
        fi
    done

    if $all_found; then
        pass "$test_name"
    else
        fail "$test_name"
        echo "     --- full output ---"
        echo "$output" | sed 's/^/     /'
        echo "     --------------------"
    fi
}

echo "=============================================="
echo "Step 1: Compiling"
echo "=============================================="
rm -rf out
mkdir -p out
if javac -d out $(find src -name "*.java"); then
    echo "  ✅ Compiled successfully."
else
    echo "  ❌ Compilation failed. Fix errors above before running tests."
    exit 1
fi
echo

echo "=============================================="
echo "Step 2: Calculator tests"
echo "=============================================="

check_contains \
    "0.1 + 0.2 should equal 0.3 (BigDecimal precision)" \
    "com.shadowfox.calculator.Calculator" \
    "1\n0.1\n+\n0.2\n0\n" \
    "Result: 0.3"

check_contains \
    "Division by zero should be handled, not crash" \
    "com.shadowfox.calculator.Calculator" \
    "1\n5\n/\n0\n0\n" \
    "Cannot divide by zero"

check_contains \
    "Square root of 9 should be 3" \
    "com.shadowfox.calculator.Calculator" \
    "2\n9\n0\n" \
    "Square root: 3"

check_contains \
    "2 to the power of 10 should be 1024" \
    "com.shadowfox.calculator.Calculator" \
    "3\n2\n10\n0\n" \
    "Result: 1024"

check_contains \
    "Non-numeric input should be rejected without crashing" \
    "com.shadowfox.calculator.Calculator" \
    "1\nabc\n5\n+\n5\n0\n" \
    "Please enter a valid number"

check_contains \
    "0 Celsius should convert to 32.00 F and 273.15 K" \
    "com.shadowfox.calculator.Calculator" \
    "4\n1\n0\n0\n" \
    "Fahrenheit: 32.00" \
    "Kelvin:     273.15"

echo
echo "=============================================="
echo "Step 3: Contact Manager tests"
echo "=============================================="

check_contains \
    "Adding a valid contact should succeed" \
    "com.shadowfox.contacts.ContactManager" \
    "1\nJohn Doe\n9876543210\njohn@example.com\n0\n" \
    "Contact added."

check_contains \
    "Duplicate phone number should be rejected" \
    "com.shadowfox.contacts.ContactManager" \
    "1\nJohn Doe\n9876543210\njohn@example.com\n1\nJane Doe\n9876543210\njane@example.com\n0\n" \
    "already exists"

check_contains \
    "Invalid email should be rejected" \
    "com.shadowfox.contacts.ContactManager" \
    "1\nJohn Doe\n9876543210\nnot-an-email\n0\n" \
    "Invalid email format."

check_contains \
    "Case-insensitive search should find 'John Doe' via 'john'" \
    "com.shadowfox.contacts.ContactManager" \
    "1\nJohn Doe\n9876543210\njohn@example.com\n5\njohn\n0\n" \
    "John Doe"

echo
echo "=============================================="
echo "Summary"
echo "=============================================="
echo "  Passed: $PASS_COUNT"
echo "  Failed: $FAIL_COUNT"
echo

if [ "$FAIL_COUNT" -eq 0 ]; then
    echo "All automated checks passed."
else
    echo "Some checks failed — see details above."
fi

echo
echo "=============================================="
echo "Manual GUI checklist (Student Information System)"
echo "=============================================="
echo "  Run:  java -cp out com.shadowfox.student.StudentInfoSystemGUI"
echo "  Then verify by hand:"
echo "    [ ] Add a student with marks 95  -> grade shows A"
echo "    [ ] Add a student with marks 30  -> row highlighted red, grade F"
echo "    [ ] Click a row                  -> form fields populate"
echo "    [ ] Edit + click Update          -> row updates in place"
echo "    [ ] Click Delete                 -> confirmation dialog appears first"
echo "    [ ] Resize the window            -> buttons/table remain usable"
echo

exit $FAIL_COUNT
