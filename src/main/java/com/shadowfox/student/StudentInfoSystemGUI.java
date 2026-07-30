package com.shadowfox.student;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 3: Student Information System with Swing.
 *
 * Architecture:
 *  - Model:      the `students` list of Student objects + the JTable's
 *                DefaultTableModel that mirrors it for display.
 *  - View:       the JTable, text fields, and buttons built in the constructor.
 *  - Controller: the ActionListeners that translate button clicks into
 *                model updates and then refresh the view.
 *
 * Features:
 *  - Add / Update / Delete student records
 *  - Numeric-only validation on ID and Marks fields
 *  - Confirmation dialog before delete
 *  - BorderLayout so the button panel stays visible and usable on resize
 *  - Rows for failing students (grade F) are highlighted in red
 */
public class StudentInfoSystemGUI extends JFrame {

    private final List<Student> students = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField idField = new JTextField(6);
    private final JTextField nameField = new JTextField(15);
    private final JTextField marksField = new JTextField(6);

    private int nextId = 1;

    public StudentInfoSystemGUI() {
        super("ShadowFox Student Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Marks", "Grade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // edits go through the form + Update button, not inline
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowIntoForm();
        });
        applyFailRowHighlighting();

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(560, 380));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panel.add(idField, gbc);
        idField.setEditable(false); // ID is auto-generated

        gbc.gridx = 2; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3; panel.add(nameField, gbc);

        gbc.gridx = 4; panel.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 5; panel.add(marksField, gbc);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear Form");

        addButton.addActionListener(e -> onAdd());
        updateButton.addActionListener(e -> onUpdate());
        deleteButton.addActionListener(e -> onDelete());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        return panel;
    }

    // ----- Controller logic -----

    private void onAdd() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        Double marks = parseMarks(marksField.getText().trim());
        if (marks == null) return;

        Student student = new Student(nextId++, name, marks);
        students.add(student);
        refreshTable();
        clearForm();
    }

    private void onUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select a student in the table first.");
            return;
        }

        Student student = students.get(selectedRow);
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        Double marks = parseMarks(marksField.getText().trim());
        if (marks == null) return;

        student.setName(name);
        student.setMarks(marks);
        refreshTable();
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select a student in the table first.");
            return;
        }

        Student student = students.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + student.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(selectedRow);
            refreshTable();
            clearForm();
        }
    }

    private Double parseMarks(String text) {
        // Input validation: only numeric marks between 0 and 100 are accepted,
        // so something like "ABCD" in the marks field never corrupts the data.
        try {
            double value = Double.parseDouble(text);
            if (value < 0 || value > 100) {
                showError("Marks must be between 0 and 100.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            showError("Marks must be a valid number.");
            return null;
        }
    }

    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Student student = students.get(row);
        idField.setText(String.valueOf(student.getId()));
        nameField.setText(student.getName());
        marksField.setText(String.valueOf(student.getMarks()));
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        marksField.setText("");
        table.clearSelection();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getMarks(), s.getGrade()});
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    /** Conditional formatting: highlight failing students (grade F) in red. */
    private void applyFailRowHighlighting() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                Object gradeValue = tbl.getModel().getValueAt(row, 3);
                boolean failing = "F".equals(gradeValue);
                if (!isSelected) {
                    c.setBackground(failing ? new Color(255, 205, 205) : Color.WHITE);
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentInfoSystemGUI().setVisible(true));
    }
}
