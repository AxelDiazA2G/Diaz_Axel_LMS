import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Main
 * The Main class serves as the entry point for a Library Management System.
 * This program provides a GUI-Swing interface for managing a library of books.
 * Users can import books from a MySQL Database, remove books by Barcode, list all books, and exit the program.
 */
public class Main extends JFrame {
    private static List<String> log = new ArrayList<>();
    private static final int MIN_TABLE_WIDTH = 80;


    public static void main(String[] args) {
        Library library = new Library();
        MainPanel mainPanel = new MainPanel(library);
        JFrame mainPanelFrame = new JFrame("Library Management System (LMS)");
        mainPanelFrame.setContentPane(mainPanel.getPanel1());
        mainPanelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanelFrame.pack();
        mainPanelFrame.setVisible(false);
        start(mainPanel, mainPanelFrame, library);
    }



    /**
     * Starts the main panel of the Library Management System.
     *
     * @param mainPanel       The main panel instance.
     * @param mainPanelFrame  The main panel JFrame.
     * @param library         The library instance.
     */
    private static void start(MainPanel mainPanel, JFrame mainPanelFrame, Library library) {
        mainPanelFrame.setVisible(true);
        mainPanel.populateTable(library);
    }

    /**
     * Imports books from a file into the library.
     *
     * @param library      The library instance.
     * @param selectedFile The file containing book information.
     */
    public static void addBooksFromFile(Library library, File selectedFile) {
        if (selectedFile == null || !selectedFile.exists() || selectedFile.isDirectory()) {
            JOptionPane.showMessageDialog(null, "The file you specified does not contain valid books. Exiting the program!", "Catastrophic Error", JOptionPane.ERROR_MESSAGE);
            JOptionPane.getRootFrame().dispose(); // Close the dialog
            return;
        }

        String errorSummary = library.addBooksFromFile(selectedFile.getPath(), 1000);
        if (errorSummary.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Books added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        if (errorSummary.equals("No Valid Books Added")) {
            JOptionPane.showMessageDialog(null, "The file you specified does not contain valid books. Exiting the program!", "Catastrophic Error", JOptionPane.ERROR_MESSAGE);
            JOptionPane.getRootFrame().dispose(); // Close the dialog
            System.exit(0); // Terminate the program
        } else {
            JOptionPane.showMessageDialog(null, "Books added with some errors: " + errorSummary, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
