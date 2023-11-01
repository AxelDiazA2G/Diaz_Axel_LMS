import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;


/**Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Main
 * The Main class serves as the entry point for a Library Management System.
 * The primary objective of this program is to provide a console-based interface
 * for managing a library of books. Users can import books from a file, remove books by Barcode,
 * list all books, and exit the program.
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
        JFrame mainPanelFrame = new JFrame("LMS");
        mainPanelFrame.setContentPane(mainPanel.getPanel1());
        mainPanelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanelFrame.pack();
        mainPanelFrame.setVisible(false);

        int dynamicWidth = MIN_TABLE_WIDTH;  // Assume MIN_TABLE_WIDTH is defined

        init(introductionScreen,testCaseSelector,fileExplorer,introFrame,testCaseFrame,fileExplorerFrame,library);

        start(mainPanel,mainPanelFrame,library);


    }

    private static void init(Introduction_Screen introductionScreen,TestCaseSelector testCaseSelector,FileExplorer fileExplorer,JFrame introFrame,JFrame testCaseFrame,JFrame fileExplorerFrame,Library library){
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

        // Show the Test Case Selector JFrame
        if (initialChoice==2){
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
        // Show the Test Case Selector JFrame
        if (initialChoice==1){
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
        if (selectedFile!=null) addBooksFromFile(library, selectedFile);
    }

    private static void start(MainPanel mainPanel,JFrame mainPanelFrame,Library library){
        mainPanelFrame.setVisible(true);
        mainPanel.populateTable(library);
    }
    private static void clearScreen() {
                              System.out.print("\033[H\033[2J");
                              System.out.flush();
               }
               /**calculateDynamicWidth
                * Calculates the dynamic width of the table based on the maximum title length and author length in the library.
                *
                * @param library The library instance.
                * @return The dynamic width.
                */
               private static int calculateDynamicWidth(Library library) {
                              int maxTitleLength = library.getMaxTitleLength();
                              int maxAuthorLength = library.getMaxAuthorLength();

                              int idPadding =35;
                              int maxIdLength = String.valueOf(library.getMaxId()).length() + idPadding;

                              int extraSpacesForSeparators = 5;
                              int bookRowWidth = maxTitleLength + maxAuthorLength + maxIdLength + extraSpacesForSeparators;

                              int statsWidth = String.format("Total Books: %d | Total Pages: %d | Current Page: %d", library.getTotalBooks(), (library.getTotalBooks() / 15) + 1, (library.getTotalBooks() / 15) + 1).length();

                              int menuPadding = 2;
                              int menuWidth = "4. Exit Program".length() + menuPadding;

                              int logPadding = 2;
                              int logWidth = "Books added with some errors:".length() + logPadding;

                              int navPromptPadding = 2;
                              int navPromptWidth = "Press 'n' for next page, 'p' for previous page, 'q' to quit listing.".length() + navPromptPadding;

                              int uniformWidth = Math.max(Math.max(Math.max(Math.max(bookRowWidth, menuWidth), logWidth), navPromptWidth), statsWidth);
                              return uniformWidth;
               }

               /**displayMenu
                * Displays the menu options.
                *
                * @param tableWidth The width of the table.
                */
               private static void displayMenu(int tableWidth) {
                              String formatString = "| %-" + (tableWidth - 2) + "s|\n";
                              System.out.printf(formatString, "Menu Options");
                              System.out.printf(formatString, "1. Import Books from a File");
                              System.out.printf(formatString, "2. Remove Book");
                              System.out.printf(formatString, "3. List All Books");
                              System.out.printf(formatString, "4. Check Out Book");
                              System.out.printf(formatString, "5. Check In Book");
                              System.out.printf(formatString, "6. Exit Program");
                              //Used to debug if JAVA was recognizing correct directory for Test Cases
                                   //System.out.println("Working Directory = " + System.getProperty("user.dir"));


               }
               /**displayHeader
                * Displays the header of the library management system.
                *
                * @param dynamicWidth The dynamic width of the table.
                */
               private static void displayHeader(int dynamicWidth) {
                              System.out.println("+"
                                                 + "-".repeat(dynamicWidth - 2) + "+");
                              System.out.printf("| %-" + (dynamicWidth - 2) + "s|\n", "Library Management System");
                              System.out.println("+"
                                                 + "-".repeat(dynamicWidth - 2) + "+");
               }
               /**displayLog
                * Displays the log of messages.
                *
                * @param dynamicWidth The dynamic width of the table.
                */
               private static void displayLog(int dynamicWidth) {

							//Border
							System.out.println("+"
                                                 + "-".repeat(dynamicWidth - 2) + "+");
                              String formatString = "| %-" + (dynamicWidth - 2) + "s|\n";
                              int logCount = 0;
                              for (String message : log) {
                                             String[] logStrings = message.split("\n");
                                             for (String string : logStrings) {
                                                            System.out.printf(formatString, string);
                                                            logCount++;
                                             }
                              }
                              for (int i = logCount; i < 5; i++) {
                                             System.out.printf(formatString, " ");
                              }
							  System.out.println("+"
                                                 + "-".repeat(dynamicWidth - 2) + "+");
               }

               /**
                * Logs a message to the log.
                *
                * @param type    The type of message.
                * @param message The message to log.
                */
               private static void logMessage(String type, String message) {
                              String logMsg = type + ": " + message;
                              // Preserve the last 5 distinct messages, not lines
                              if (log.size() >= 5) {
                                             log.remove(0);
                              }
                              log.add(logMsg);
               }

               /**menuController
                * Creates a controller for the GUI to have all methods on the same place.
                */
               private static void menuController(int dynamicWidth){
                    displayHeader(dynamicWidth);
                    displayMenu(dynamicWidth);
                    displayLog(dynamicWidth);
               }

               /**listAllBooks
                * Lists all books in the library.
                *
                * @param library      The library instance.
                * @param scanner      The scanner for user input.
                * @param dynamicWidth The dynamic width of the table.
                */


               /**
                * Imports books from a file.
                *
                * @param library The library instance.
                */
               public static void addBooksFromFile(Library library, File selectedFile) {
                   if (selectedFile == null || !selectedFile.exists() || selectedFile.isDirectory()) {
                       logMessage("[ERROR]", "Invalid file. Please try again.");
                       return;
                   }

                   String errorSummary = library.addBooksFromFile(selectedFile.getPath(), 1000);
                   if (errorSummary.isEmpty()) {
                       logMessage("[INFO]", "Books added successfully!");
                       JOptionPane.showMessageDialog(null,"Books added successfully!","Success",JOptionPane.INFORMATION_MESSAGE);

                   } if(errorSummary.toString().equals("No Valid Books Added")){
                       JOptionPane.showMessageDialog(null,"The file you specified does not contain valid books. Exiting the program!","Catastrophic Error",JOptionPane.ERROR_MESSAGE);
                       JOptionPane.getRootFrame().dispose(); // Close the dialog
                       System.exit(0); // Terminate the program
                   }
                   else{
                       logMessage("[ERROR]", "Books added with some errors:\n" + errorSummary);
                       JOptionPane.showMessageDialog(null,"Books added with some errors:"+ errorSummary,"Warning",JOptionPane.WARNING_MESSAGE);

                   }
               }



                

               /**
                * Exits the program.
                *
                * @param scanner The scanner for user input.
                */
               private static void exit(Scanner scanner) {
                              logMessage("[INFO]", "Exiting...");
                              scanner.close();
               }
}