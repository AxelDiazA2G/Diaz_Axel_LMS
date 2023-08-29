import java.io.*;
import java.util.*;


/**Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Main
 * The Main class serves as the entry point for a Library Management System.
 * The primary objective of this program is to provide a console-based interface
 * for managing a library of books. Users can import books from a file, remove books by ID,
 * list all books, and exit the program.
 */
public class Main {
               private static List<String> log = new ArrayList<>();
               private static final int MIN_TABLE_WIDTH = 80;
               private static final String case_path = "test_case_1.txt";

               public static void main(String[] args) {
                              Library library = new Library();
                              Scanner scanner = new Scanner(System.in);
                              addBooksFromFile(library, scanner);
                              int dynamicWidth = MIN_TABLE_WIDTH;

                              while (true) {
                                             dynamicWidth = calculateDynamicWidth(library);
                                             clearScreen();
                                             displayHeader(dynamicWidth);
                                             displayMenu(dynamicWidth);
                                             displayLog(dynamicWidth);

                                             int choice = scanner.nextInt();
                                             scanner.nextLine();

                                             switch (choice) {
                                             case 1:
                                                            addBooksFromFile(library, scanner);
                                                            break;
                                             case 2:
                                                            removeBookById(library, scanner);
                                                            break;
                                             case 3:
                                                            listAllBooks(library, scanner, dynamicWidth);
                                                            break;
                                             case 4:
                                                            exit(scanner);
                                                            return;
                                             default:
                                                            logMessage("[ERROR]", "Invalid choice. Please try again.");
                                             }
                              }
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

                              int idPadding = 3;
                              int maxIdLength = String.valueOf(library.getMaxId()).length() + idPadding;

                              int extraSpacesForSeparators = 20;
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
                              System.out.printf(formatString, "2. Remove Book by ID");
                              System.out.printf(formatString, "3. List All Books");
                              System.out.printf(formatString, "4. Exit Program");
							  
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

               /**listAllBooks
                * Lists all books in the library.
                *
                * @param library      The library instance.
                * @param scanner      The scanner for user input.
                * @param dynamicWidth The dynamic width of the table.
                */
               private static void listAllBooks(Library library, Scanner scanner, int dynamicWidth) {
                              int page = 0;
                              int pageSize = 15;
                              int totalBooks = library.getTotalBooks();
                              int totalPages = (totalBooks + pageSize - 1) / pageSize;

                              // Calculate the lengths of the longest ID, Title, and Author
                              int maxIdLength = String.valueOf(library.getMaxId()).length();
                              int maxTitleLength = library.getMaxTitleLength() - 1;
                              int maxAuthorLength = library.getMaxAuthorLength() - 2;

                              // Calculate the required table width
                              int requiredWidth = maxIdLength + maxTitleLength + maxAuthorLength + 6;

                              // Calculate extra spaces needed to fill the dynamic table width
                              int extraSpaces = dynamicWidth - requiredWidth;

                              // Distribute extra spaces equally to Title and Author fields
                              int extraSpacesForTitle = extraSpaces / 2;
                              int extraSpacesForAuthor = extraSpaces / 2;

                              // If there's an odd number of extra spaces, add the extra one to Title
                              if (extraSpaces % 2 != 0) {
                                             extraSpacesForTitle += 1;
                              }

                              // Update the max lengths for Title and Author
                              maxTitleLength += extraSpacesForTitle;
                              maxAuthorLength += extraSpacesForAuthor;

                              String rowFormat = "| %-" + maxIdLength + "s | %-" + maxTitleLength + "s | %-" + maxAuthorLength + "s |\n";
                              int tableWidth = Math.max(dynamicWidth, requiredWidth);

                              while (true) {
                                             clearScreen();
                                             displayHeader(dynamicWidth);
                                             displayMenu(dynamicWidth);
                                             displayLog(dynamicWidth);
											 String title = String.format(rowFormat, "ID", "Title", "Author");
                                             System.out.printf("%-" + (tableWidth - 3) + "s", title);
											 //Top-Bottom Border
                                             System.out.println("+"
                                                                + "-".repeat(tableWidth) + "+");
                                             library.listAllBooks(page, pageSize, dynamicWidth);
											 //Top-Bottom Border
											 System.out.println("+"
                                                                + "-".repeat(tableWidth) + "+");
                                             String stats = String.format("Total Books: %d | Total Pages: %d | Current Page: %d", totalBooks, totalPages, page + 1);
                                             System.out.printf("| %-" + (tableWidth - 2) + "s |\n", stats);
											 //Top-Bottom Border
                                             System.out.println("+"
                                                                + "-".repeat(tableWidth) + "+");
                                             System.out.printf("| %-" + (tableWidth - 2) + "s |\n", "Press 'n' for next page, 'p' for previous page, 'q' to quit listing.");
											 //Bottom Border
                                             System.out.println("+"
                                                                + "-".repeat(tableWidth) + "+");

                                             String command = scanner.nextLine().trim().toLowerCase();
                                             if (command.equals("n")) {
                                                            page = Math.min(page + 1, totalPages - 1);
                                             } else if (command.equals("p")) {
                                                            page = Math.max(0, page - 1);
                                             } else if (command.equals("q")) {
                                                            break;
                                             } else {
                                                            logMessage("[ERROR]", "Invalid choice. Please try again.");
                                             }
                              }
               }

               /**
                * Imports books from a file.
                *
                * @param library The library instance.
                * @param scanner The scanner for user input.
                */
               private static void addBooksFromFile(Library library, Scanner scanner) {
                              String errorSummary = library.addBooksFromFile(case_path, 1000);
                              if (errorSummary.isEmpty()) {
                                             logMessage("[INFO]", "Books added successfully!");
                              } else {
                                             logMessage("[ERROR]", "Books added with some errors:\n" + errorSummary);
                              }
               }

               /**
                * Removes a book from the library by ID.
                *
                * @param library The library instance.
                * @param scanner The scanner for user input.
                */
               private static void removeBookById(Library library, Scanner scanner) {
                              System.out.print("Enter book ID to remove: ");
                              int id = scanner.nextInt();
                              scanner.nextLine();
                              boolean isRemoved = library.removeBookById(id);
                              if (isRemoved) {
                                             logMessage("[INFO]", "Book removed successfully!");
                              } else {
                                             logMessage("[ERROR]", "Failed to remove book. ID may not exist.");
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