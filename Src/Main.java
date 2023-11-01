import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Main
 * The Main class serves as the entry point for a Library Management System.
 * This program provides a GUI-Swing interface for managing a library of books.
 * Users can import books from a file, remove books by Barcode, list all books, and exit the program.
 */
public class Main extends JFrame {
    private static List<String> log = new ArrayList<>();
    private static final int MIN_TABLE_WIDTH = 80;
    private static final String case_path = "Test Cases/test_case_1.txt";

    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        Introduction_Screen introductionScreen = new Introduction_Screen();

        // Initialize JFrame for Introduction_Screen
        JFrame introFrame = new JFrame("Introduction Screen");
        introFrame.setContentPane(introductionScreen.getPanel1());
        introFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        introFrame.pack();
        introFrame.setVisible(true);

        TestCaseSelector testCaseSelector = new TestCaseSelector();
        JFrame testCaseFrame = new JFrame("Test Case Selector");
        testCaseFrame.setContentPane(testCaseSelector.getPanel1());
        testCaseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        testCaseFrame.pack();
        testCaseFrame.setVisible(false);

        FileExplorer fileExplorer = new FileExplorer();
        JFrame fileExplorerFrame = new JFrame("File Explorer");
        fileExplorerFrame.setContentPane(fileExplorer.getPanel1());
        fileExplorerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fileExplorerFrame.pack();
        fileExplorerFrame.setVisible(false);

        MainPanel mainPanel = new MainPanel(library);
        JFrame mainPanelFrame = new JFrame("Library Management System (LMS)");
        mainPanelFrame.setContentPane(mainPanel.getPanel1());
        mainPanelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanelFrame.pack();
        mainPanelFrame.setVisible(false);

        init(introductionScreen, testCaseSelector, fileExplorer, introFrame, testCaseFrame, fileExplorerFrame, library);

        start(mainPanel, mainPanelFrame, library);
    }

    /**
     * Initializes the application based on user choices from the introduction screen.
     *
     * @param introductionScreen The introduction screen instance.
     * @param testCaseSelector  The test case selector instance.
     * @param fileExplorer      The file explorer instance.
     * @param introFrame        The introduction screen JFrame.
     * @param testCaseFrame     The test case selector JFrame.
     * @param fileExplorerFrame The file explorer JFrame.
     * @param library           The library instance.
     */
    private static void init(Introduction_Screen introductionScreen, TestCaseSelector testCaseSelector, FileExplorer fileExplorer, JFrame introFrame, JFrame testCaseFrame, JFrame fileExplorerFrame, Library library) {
        while (introductionScreen.getSelectedChoice() == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int initialChoice = introductionScreen.getSelectedChoice();

        introFrame.dispose();  // Close the introduction screen
        File selectedFile = null;

        // Show the Test Case Selector JFrame if user chooses to load a test case
        if (initialChoice == 2) {
            testCaseFrame.setVisible(true);
            // Wait for a file to be selected
            while (testCaseSelector.getSelectedFile() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            selectedFile = testCaseSelector.getSelectedFile();
            testCaseFrame.dispose();
        }
        // Show the File Explorer JFrame if user chooses to load a custom file
        if (initialChoice == 1) {
            fileExplorerFrame.setVisible(true);
            // Wait for a file to be selected
            while (fileExplorer.getSelectedFile() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            selectedFile = fileExplorer.getSelectedFile();
            fileExplorerFrame.dispose();
        }
        // If a file is selected, attempt to add books from the file to the library
        if (selectedFile != null) {
            addBooksFromFile(library, selectedFile);
        }
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
