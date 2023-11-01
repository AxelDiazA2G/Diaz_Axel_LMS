import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 *   Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * MainPanel
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

    /**
     * Constructor
     *
     * @param library The Library instance to interact with.
     */
    public MainPanel(Library library) {
        panel1 = new JPanel(new BorderLayout(10, 10));
        panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Padding

        tabbedPane1 = new JTabbedPane();
        JPanel deleteBooksPanel = createDeleteBooksPanel(library);
        JPanel checkInOutPanel = createCheckInOutPanel(library);

        tabbedPane1.addTab("Delete Books", deleteBooksPanel);
        tabbedPane1.addTab("Check In/Out Books", checkInOutPanel);
        tabbedPane1.setBackground(new Color(135, 206, 235));
        panel1.add(tabbedPane1, BorderLayout.NORTH);


        table1 = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(table1);
        tableScrollPane.setPreferredSize(new Dimension(600, 400));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Books Table"));
        panel1.add(tableScrollPane, BorderLayout.SOUTH);

        exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(144, 238, 144));
        exitButton.addActionListener(e -> System.exit(0));
        panel1.add(exitButton, BorderLayout.EAST);
    }

    /**
     * Creates the panel for deleting books.
     *
     * @param library The Library instance to interact with.
     * @return The delete books panel.
     */
    private JPanel createDeleteBooksPanel(Library library) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Padding
        deleteByBarcodeField = addTextFieldWithBorder(panel, "Delete by Barcode");
        deleteByTitleField = addTextFieldWithBorder(panel, "Delete by Title");
        addDeleteButtonToPanel(panel, library);
        return panel;
    }


    /**
     * Handles the deletion of books based on user input.
     *
     * @param library The Library instance to interact with.
     */
    private void handleDeleteBook(Library library) {
        String barcode = deleteByBarcodeField.getText().trim();
        String title = deleteByTitleField.getText().trim();

        if (!barcode.isEmpty()) {
            // Remove by Barcode
            boolean isRemoved = library.removeBookById(Integer.parseInt(barcode));
            if (isRemoved) {
                populateTable(library);
                JOptionPane.showMessageDialog(null, "Book removed successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to remove book. Barcode may not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (!title.isEmpty()) {
            // Remove by Title
            Map<String, List<Integer>> searchResults = library.searchByTitle(title);
            List<Integer> exactMatches = searchResults.get("exact");

            if (!exactMatches.isEmpty()) {
                if (exactMatches.size() == 1) {
                    int chosenBarcode = exactMatches.get(0);
                    boolean isRemoved = library.removeBookById(chosenBarcode);
                    if (isRemoved) {
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
     * Creates the panel for checking in/out books.
     *
     * @param library The Library instance to interact with.
     * @return The check in/out panel.
     */
    private JPanel createCheckInOutPanel(Library library) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Padding
        checkByBarcodeField = addTextFieldWithBorder(panel, "Check by Barcode");
        checkByTitleField = addTextFieldWithBorder(panel, "Check by Title");
        addContinueButtonToPanel(panel, library);
        return panel;
    }

    /**
     * Handles the check in/out of books based on user input.
     *
     * @param library The Library instance to interact with.
     */
    private void handleCheckInOut(Library library) {
        String barcodeInput = checkByBarcodeField.getText().trim();
        String title = checkByTitleField.getText().trim();

        if (!barcodeInput.isEmpty()) {
            // Handle by Barcode
            try {
                int barcode = Integer.parseInt(barcodeInput);
                reverseBookStatus(library, barcode);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid barcode. Please enter a numerical value.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (!title.isEmpty()) {
            // Handle by Title
            Map<String, List<Integer>> searchResults = library.searchByTitle(title);
            List<Integer> exactMatches = searchResults.get("exact");

            if (!exactMatches.isEmpty()) {
                if (exactMatches.size() == 1) {
                    int chosenBarcode = exactMatches.get(0);
                    reverseBookStatus(library, chosenBarcode);
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
        Integer index = library.getIndexByBarcode(barcode);
        Book book = library.getBookByBarcode(barcode);

        if (index == null || book == null) {
            JOptionPane.showMessageDialog(null, "No book found with the given barcode.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean currentStatus = book.getStatus();
        boolean isChanged = library.changeBookStatus(index, !currentStatus); // Reverse the status

        if (isChanged) {
            if (!currentStatus) {
                setDueDate(library, barcode);
            } else {
                clearDueDate(library, barcode);
            }

            String message = !currentStatus ? "Book checked out successfully!" : "Book checked in successfully!";
            JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            populateTable(library);  // Update the table to reflect changes
        } else {
            String message = !currentStatus ? "Failed to check out book." : "Failed to check in book.";
            JOptionPane.showMessageDialog(null, message + " Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sets the due date for a book based on its barcode.
     *
     * @param library The Library instance to interact with.
     * @param barcode The barcode of the book to set the due date for.
     */
    private void setDueDate(Library library, int barcode) {
        Book book = library.getBookByBarcode(barcode);
        LocalDate today = LocalDate.now();
        book.setDueDate(today.plusDays(28).toString());
    }

    /**
     * Clears the due date for a book based on its barcode.
     *
     * @param library The Library instance to interact with.
     * @param barcode The barcode of the book to clear the due date for.
     */
    private void clearDueDate(Library library, int barcode) {
        Book book = library.getBookByBarcode(barcode);
        book.setDueDate(null);
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
        Object[][] tableData = library.listAllBooks(0, 50);  // Adjust these arguments as needed
        System.out.println("Number of books fetched: " + tableData.length);  // Logging

        // Assuming 5 columns: ID, Title, Author, Status, DueDate
        String[] columnNames = {"ID", "Title", "Author", "Status", "DueDate"};

        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        table1.setModel(model);
    }

}
