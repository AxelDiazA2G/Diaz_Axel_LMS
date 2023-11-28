import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * The MainPanel class represents the graphical user interface (GUI) panel for managing books
 * within a Library Management System (LMS). It provides functionality for deleting and checking
 * in/out books, displaying book information in a table, and handling user interactions.
 */
public class MainPanel {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JButton exitButton;
    private JTextArea logTextArea;
    private JTextField searchTextField;
    private JTextField deleteByBarcodeField;
    private JTextField deleteByTitleField;
    private JTextField checkByBarcodeField;
    private JTextField checkByTitleField;
    private JLabel statusSummaryLabel;
    // Constants for Color Scheme and Fonts
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color PRIMARY_COLOR = new Color(105, 105, 105); // A soft dark color
    private static final Color SECONDARY_COLOR = new Color(169, 169, 169); // A soft light color
    private static final Color BUTTON_COLOR = new Color(192, 192, 192); // A gentle grey
    private static final Color TAB_COLOR = new Color(225, 225, 225); // Off white for tabs
    private static final Font GLOBAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    /**
     * Constructor
     *
     * @param library The Library instance to interact with.
     */
    public MainPanel(Library library) {
        panel1 = new JPanel(new BorderLayout(10, 10));
        panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Padding

        // Apply global font
        UIManager.put("Button.font", GLOBAL_FONT);
        UIManager.put("Table.font", GLOBAL_FONT);
        UIManager.put("TabbedPane.font", GLOBAL_FONT);
        UIManager.put("Label.font", GLOBAL_FONT);

        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setFont(GLOBAL_FONT);
        tabbedPane1.setForeground(PRIMARY_COLOR);
        tabbedPane1.setBackground(TAB_COLOR);

        // Rounded corners for tabs
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(4, 4, 4, 4));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);

        JPanel deleteBooksPanel = createDeleteBooksPanel(library);
        JPanel checkInOutPanel = createCheckInOutPanel(library);

        tabbedPane1.addTab("Delete Books", deleteBooksPanel);
        tabbedPane1.addTab("Check In/Out Books", checkInOutPanel);

        // Apply a flat look and remove the border from the tabs
        for (int i = 0; i < tabbedPane1.getTabCount(); i++) {
            JLabel label = new JLabel(tabbedPane1.getTitleAt(i));
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabbedPane1.setTabComponentAt(i, label);
        }
        panel1.add(tabbedPane1,BorderLayout.NORTH);


        table1 = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(table1);
        tableScrollPane.setPreferredSize(new Dimension(600, 400));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Books Table"));
        panel1.add(tableScrollPane, BorderLayout.SOUTH);

        exitButton = createStyledButton("Exit");
        exitButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding for the button
        exitButton.setBackground(new Color(144, 238, 144));
        exitButton.addActionListener(e -> System.exit(0));
        panel1.add(exitButton, BorderLayout.EAST);

        // Initialize the status summary label
        statusSummaryLabel = new JLabel();
        updateStatusSummary(library);
        panel1.add(statusSummaryLabel, BorderLayout.WEST);

        // Table styling for a modern look
        table1.setShowHorizontalLines(false);
        table1.setShowVerticalLines(false);
        table1.setFillsViewportHeight(true);
        table1.setIntercellSpacing(new Dimension(0, 0));
        table1.setBorder(BorderFactory.createEmptyBorder());

        // Modernize the scroll pane
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
    }

    /**
     * Handles the deletion of books based on user input.
     *
     * @param library The Library instance to interact with.
     */
    private void handleDeleteBook(Library library) {
        String barcode = deleteByBarcodeField.getText().trim();
        String title = deleteByTitleField.getText().trim();

        if (!barcode.isEmpty() && !barcode.equals("Enter Barcode...")) {
            // Remove by Barcode
            boolean isRemoved = library.removeBookById(Integer.parseInt(barcode));
            if (isRemoved) {
                populateTable(library);
                deleteByBarcodeField.setText("");
                JOptionPane.showMessageDialog(null, "Book removed successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to remove book. Barcode may not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (!title.isEmpty() && !title.equals("Enter Title...")) {
            // Remove by Title
            Map<String, List<Integer>> searchResults = library.searchByTitle(title);
            List<Integer> exactMatches = searchResults.get("exact");

            if (!exactMatches.isEmpty()) {
                if (exactMatches.size() == 1) {
                    int chosenBarcode = exactMatches.get(0);
                    boolean isRemoved = library.removeBookById(chosenBarcode);
                    if (isRemoved) {
                        populateTable(library);
                        deleteByTitleField.setText("");
                        JOptionPane.showMessageDialog(null, "Book removed successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to remove book. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    Object[] options = exactMatches.toArray();
                    Integer chosenBarcode = (Integer) JOptionPane.showInputDialog(null,
                            "Multiple exact matches found. Select the Barcode of the book you want to remove.",
                            "Select Barcode",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (chosenBarcode != null) {
                        boolean isRemoved = library.removeBookById(chosenBarcode);
                        if (isRemoved) {
                            populateTable(library);
                            deleteByTitleField.setText("");
                            deleteByBarcodeField.setText("");
                            JOptionPane.showMessageDialog(null, "Book removed successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to remove book. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No books with that title exist.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in either Barcode or Title to remove a book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sets up auto-complete feature for a text field with a given list of items.
     *
     * @param textField The text field to apply auto-complete on.
     * @param items     The list of items to use for auto-complete.
     */
    private void setupAutoComplete(JTextField textField, List<String> items) {
        final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        final JComboBox<String> comboBox = new JComboBox<>(model) {
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };

        comboBox.setEditable(false);
        comboBox.setFocusable(false);
        comboBox.setLightWeightPopupEnabled(true);

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(Color.LIGHT_GRAY); // Color when the item is selected
                } else {
                    setBackground(Color.WHITE); // Default color
                }
                return this;
            }
        });

        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (comboBox.getSelectedItem() != null) {
                    textField.setText(comboBox.getSelectedItem().toString());
                    comboBox.setPopupVisible(false);
                }
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String input = textField.getText();
                    int selectedIndex = comboBox.getSelectedIndex();

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (selectedIndex != -1) {
                            textField.setText(comboBox.getSelectedItem().toString());
                        }
                        comboBox.setPopupVisible(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        // Manually change the selected index if necessary. comboBox.dispatchEvent(e);
                        // is not the best practice.
                        changeComboBoxSelection(e.getKeyCode());
                    } else {
                        updateModelBasedOnInput(input);
                    }
                });
            }

            private void changeComboBoxSelection(int keyCode) {
                int currentIndex = comboBox.getSelectedIndex();
                int newIndex = currentIndex + (keyCode == KeyEvent.VK_UP ? -1 : 1);
                comboBox.setSelectedIndex(Math.max(0, Math.min(newIndex, comboBox.getItemCount() - 1)));
            }

            private void updateModelBasedOnInput(String input) {
                model.removeAllElements();
                items.stream()
                        .filter(item -> item.toLowerCase().startsWith(input.toLowerCase()))
                        .forEach(model::addElement);

                comboBox.setPopupVisible(model.getSize() > 0);
            }
        });

        textField.setLayout(new BorderLayout());
        textField.add(comboBox, BorderLayout.SOUTH);
    }

    /**
     * Handles the check in/out of books based on user input.
     *
     * @param library The Library instance to interact with.
     */
    private void handleCheckInOut(Library library) {
        String barcodeInput = checkByBarcodeField.getText().trim();
        String title = checkByTitleField.getText().trim();

        if (!barcodeInput.isEmpty() && !barcodeInput.equals("Check by Barcode...")) {
            // Handle by Barcode
            try {
                int barcode = Integer.parseInt(barcodeInput);
                reverseBookStatus(library, barcode);
                checkByBarcodeField.setText("");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid barcode. Please enter a numerical value.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (!title.isEmpty() && !title.equals("Check by Title...")) {
            // Handle by Title
            Map<String, List<Integer>> searchResults = library.searchByTitle(title);
            List<Integer> exactMatches = searchResults.get("exact");

            if (!exactMatches.isEmpty()) {
                if (exactMatches.size() == 1) {
                    int chosenBarcode = exactMatches.get(0);
                    reverseBookStatus(library, chosenBarcode);
                    checkByTitleField.setText("");
                } else {
                    Object[] options = exactMatches.toArray();
                    Integer chosenBarcode = (Integer) JOptionPane.showInputDialog(null,
                            "Multiple exact matches found. Select the Barcode of the book you want to handle.",
                            "Select Barcode",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (chosenBarcode != null) {
                        reverseBookStatus(library, chosenBarcode);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No books with that title exist.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in either Barcode or Title to proceed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reverses the status of a book based on its barcode.
     *
     * @param library The Library instance to interact with.
     * @param barcode The barcode of the book to reverse the status of.
     */
    private void reverseBookStatus(Library library, int barcode) {
        Book book = library.getBookByBarcode(barcode);

        if (book == null) {
            JOptionPane.showMessageDialog(null, "No book found with the given barcode.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean currentStatus = book.getStatus();
        boolean isChanged = library.changeBookStatus(barcode); // Reverse the status

        if (isChanged) {
            String message;
            if (currentStatus) {
                message = "Book checked in successfully!";
            } else {
                message = "Book checked out successfully!";
            }

            JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            populateTable(library);  // Update the table to reflect changes
        } else {
            String errorMessage = currentStatus ? "Failed to check in book." : "Failed to check out book.";
            JOptionPane.showMessageDialog(null, errorMessage + " Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }





    /**
     * Adds a text field with a titled border to a JPanel.
     *
     * @param panel The JPanel to add the text field to.
     * @param title The title for the text field border.
     * @return The added text field.
     */
    private JTextField addTextFieldWithBorder(JPanel panel, String title) {
        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(textField);
        return textField;
    }

    /**
     * Adds a "Delete" button to a JPanel and attaches an action listener to it.
     *
     * @param panel   The JPanel to add the button to.
     * @param library The Library instance to interact with.
     */
    private void addDeleteButtonToPanel(JPanel panel, Library library) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> handleDeleteBook(library));
        panel.add(deleteButton);
    }

    /**
     * Adds a "Continue" button to a JPanel and attaches an action listener to it.
     *
     * @param panel   The JPanel to add the button to.
     * @param library The Library instance to interact with.
     */
    private void addContinueButtonToPanel(JPanel panel, Library library) {
        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> handleCheckInOut(library));
        panel.add(continueButton);
    }

    /**
     * Gets the main panel of the GUI.
     *
     * @return The main panel.
     */
    public JPanel getPanel1() {
        return panel1;
    }

    /**
     * Populates the table with book information from the Library.
     *
     * @param library The Library instance to fetch book data from.
     */
    public void populateTable(Library library) {
        Object[][] tableData = library.listAllBooks(0, 5000);  // Adjust these arguments as needed
        String[] columnNames = {"ID", "Title", "Author", "Status", "DueDate"};

        DefaultTableModel model = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table cells non-editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Define the data type for each column
                if (columnIndex == 0) {
                    return Integer.class;
                } else if (columnIndex == 3) {
                    return Boolean.class;
                } else if (columnIndex == 4) {
                    return String.class;
                } else {
                    return String.class;
                }
            }
        };

        table1.setModel(model);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table1.setRowSorter(sorter);

        sorter.setComparator(4, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {

                try {
                    LocalDate d1 = LocalDate.parse(date1);
                    LocalDate d2 = LocalDate.parse(date2);
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        // Set initial sort order for status column (index 3) to descending
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));  // Status column
        sorter.setSortKeys(sortKeys);

        List<String> bookTitles = library.getAllBookTitles();
        setupAutoComplete(deleteByTitleField, bookTitles);
        setupAutoComplete(checkByTitleField, bookTitles);
        updateStatusSummary(library);
    }

    /**
     * Creates a styled button with the given text.
     *
     * @param text The text for the button.
     * @return The styled button.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(GLOBAL_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15))); // Compound border with line and padding
        return button;
    }

    /**
     * Creates the panel for deleting books.
     *
     * @param library The Library instance to interact with.
     * @return The panel for deleting books.
     */
    private JPanel createDeleteBooksPanel(Library library) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Delete Books"));

        // Styling the text fields
        deleteByBarcodeField = new JTextField();
        deleteByTitleField = new JTextField();
        styleTextField(deleteByBarcodeField, "Enter Barcode...");
        styleTextField(deleteByTitleField, "Enter Title...");

        // Adjust the preferred size to make text fields larger
        Dimension textFieldSize = new Dimension(300, 30); // Width can be any value, height is set larger than default
        deleteByBarcodeField.setPreferredSize(textFieldSize);
        deleteByTitleField.setPreferredSize(textFieldSize);

        // Adding fields to the panel with increased size
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(deleteByBarcodeField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(deleteByTitleField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Styling the delete button
        JButton deleteButton = createStyledButton("Delete");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.addActionListener(e -> handleDeleteBook(library));

        panel.add(deleteButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        return panel;
    }

    /**
     * Styles a text field with placeholder text.
     *
     * @param textField  The text field to style.
     * @param placeholder The placeholder text.
     */
    private void styleTextField(JTextField textField, String placeholder) {
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 30));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Width is maxed out, height is fixed.
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.setOpaque(false); // Gives a modern flat look by removing the text field background
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Modern font

        // Placeholder functionality with improved focus listener for modern UI feedback
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    textField.setOpaque(true); // Make the field opaque when focused
                    textField.setBackground(Color.WHITE); // Set background to white when focused
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setOpaque(false); // Revert to non-opaque when focus is lost
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    /**
     * Creates the panel for checking in/out books.
     *
     * @param library The Library instance to interact with.
     * @return The panel for checking in/out books.
     */
    private JPanel createCheckInOutPanel(Library library) {
        // Use BoxLayout for consistency
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Check In/Out Books"));

        // Create and style text fields
        checkByBarcodeField = new JTextField();
        checkByTitleField = new JTextField();
        styleTextField(checkByBarcodeField, "Check by Barcode...");
        styleTextField(checkByTitleField, "Check by Title...");

        // Adjust the preferred size to make text fields larger
        Dimension textFieldSize = new Dimension(300, 30); // Width can be any value, height is set larger than default
        checkByBarcodeField.setPreferredSize(textFieldSize);
        checkByTitleField.setPreferredSize(textFieldSize);

        // Adding fields to the panel with increased size
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(checkByBarcodeField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(checkByTitleField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Styling and adding the continue button
        JButton continueButton = createStyledButton("Continue");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.addActionListener(e -> handleCheckInOut(library));
        panel.add(continueButton);
        panel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer for bottom margin

        return panel;
    }

    /**
     * Updates the status summary label.
     *
     * @param library The Library instance to fetch book data from.
     */
    private void updateStatusSummary(Library library) {
        if (library == null) {
            statusSummaryLabel.setText("<html><span style='color:#D32F2F;'>Library data is unavailable.</span></html>");
            return;
        }

        int totalBooks = library.getTotalBooks();
        int checkedOutBooks = library.getCheckedOutBooksCount();
        int availableBooks = totalBooks - checkedOutBooks;

        StringBuilder summaryBuilder = new StringBuilder("<html>");
        summaryBuilder.append("<style>")
                .append("body { font-family: 'Segoe UI', sans-serif; font-size: 13px; }")
                .append(".label { font-weight: bold; color: #333; margin-right: 12px; }") // Increased margin-right
                .append(".value { font-weight: normal; margin-right: 24px; }") // Added margin-right for value spacing
                .append(".available { color: #388E3C; margin-right: 12px; }") // Added margin-right for label spacing
                .append(".checked-out { color: #D32F2F; margin-right: 12px; }") // Added margin-right for label spacing
                .append(".total { color: #303F9F; }")
                .append(".status-container { display: flex; justify-content: start; align-items: baseline; }") // Ensure alignment at baseline
                .append(".status-block { margin-right: 16px; }") // Increased margin-right for block spacing
                .append(".summary-block { display: block; margin-bottom: 4px; }") // Display as block and add margin-bottom
                .append("</style>")
                .append("<body>")
                .append("<div style='border: 1px solid #ccc; padding: 8px; background-color: #f7f7f7;'>")
                .append("<div class='summary-block'>")
                .append("<span class='label'>Total Books:</span> ")
                .append("<span class='value total'>").append(totalBooks).append(" </span>") // Add extra space after the value
                .append("</div>")
                .append("<div class='status-container'>")
                .append("<span class='status-block'>")
                .append("<span class='label'>Available:</span> ")
                .append("<span class='value available'>").append(availableBooks).append(" </span>| ") // Add extra space after the value
                .append("</span>")
                .append("<span class='status-block'>")
                .append("<span class='label'>Checked Out:</span> ")
                .append("<span class='value checked-out'>").append(checkedOutBooks).append(" </span>") // Add extra space after the value
                .append("</span>")
                .append("</div>")
                .append("</div>")
                .append("</body></html>");

        statusSummaryLabel.setText(summaryBuilder.toString());
    }
}