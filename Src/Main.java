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
public class Main {
               private static List<String> log = new ArrayList<>();
               private static final int MIN_TABLE_WIDTH = 80;
               private static final String case_path = "Test Cases/test_case_1.txt";

               public static void main(String[] args) {
                    Library library = new Library();
                    Scanner scanner = new Scanner(System.in);
                    int dynamicWidth = MIN_TABLE_WIDTH;
                    
                    while (true) {
                        dynamicWidth = calculateDynamicWidth(library);
                        clearScreen();
                        menuController(dynamicWidth);
                    
                
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                
                        switch (choice) {
                            case 1:
                                System.out.println("How would you like to select the file?");
                                System.out.println("1. Type the full path");
                                System.out.println("2. Select from the current directory");
                                int initialChoice = scanner.nextInt();
                                scanner.nextLine();  // Clear the scanner
                                addBooksFromFile(library, scanner, dynamicWidth, initialChoice);
                                break;
                            case 2:
                                removeBook(library, scanner);
                                break;
                            case 3:
                                listAllBooks(library, scanner, dynamicWidth);
                                break;
                            case 4:
                                checkOutBookByTitle(library,scanner);
                                break;
                            case 5:
                                checkInBookByTitle(library,scanner);
                                break;
                            case 6:
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
               private static void listAllBooks(Library library, Scanner scanner, int dynamicWidth) {
                              int page = 0;
                              int pageSize = 15;
                              int totalBooks = library.getTotalBooks();
                              int totalPages = (totalBooks + pageSize - 1) / pageSize;

                              // Calculate the lengths of the longest Barcode, Title, and Author
                              int maxIdLength = String.valueOf(library.getMaxId()).length();
                              int maxTitleLength = library.getMaxTitleLength() - 1;
                              int maxAuthorLength = library.getMaxAuthorLength() - 2;

                             // Calculate the width of each column
                              int barcodeWidth = maxIdLength;
                              int titleWidth = maxTitleLength;
                              int authorWidth = maxAuthorLength;
                              int statusWidth = 6;
                              int dateWidth = 10;

                              // Calculate the width of the separators and spaces
                              int separatorWidth = 9;  // 5 separators '|' and 4 spaces ' '

                              // Calculate the total required width
                              int requiredWidth = barcodeWidth + titleWidth + authorWidth + statusWidth +dateWidth+ separatorWidth;

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
                              titleWidth += extraSpacesForTitle;
                              authorWidth += extraSpacesForAuthor;

                              // Create the row format
                              String rowFormat = "| %-" + barcodeWidth + "s | %-" + titleWidth + "s | %-" + authorWidth + "s | %-" + statusWidth +"s | %-"+dateWidth+ "s |\n";
                              int tableWidth = Math.max(dynamicWidth, requiredWidth);

                              while (true) {
                                             clearScreen();
                                             displayHeader(dynamicWidth);
                                             displayMenu(dynamicWidth);
                                             displayLog(dynamicWidth);
											 String title = String.format(rowFormat, "Barcode", "Title", "Author", "Status","Due Date");
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
                * @param tableWidth The width of the table precalculated.
                * @param initialChoice The users choicve between searching on the current directory or searching from one.
                */
                private static void addBooksFromFile(Library library, Scanner scanner, int tableWidth, int initialChoice) {
                    String formatString = "| %-" + (tableWidth - 2) + "s|\n";
                    String initialPath = ".";
                    Stack<String> pathStack = new Stack<>();
                
                    if (initialChoice == 1) {
                        System.out.print("Enter the full path of the file or folder: ");
                        initialPath = scanner.nextLine();
                    }
                
                    File initialFile = new File(initialPath);
                    if (initialFile.exists()) {
                        pathStack.push(initialFile.getPath());
                    } else {
                        File lastWorkingFolder = initialFile;
                        while (lastWorkingFolder != null && !lastWorkingFolder.exists()) {
                            lastWorkingFolder = lastWorkingFolder.getParentFile();
                        }
                        if (lastWorkingFolder != null && lastWorkingFolder.isDirectory()) {
                            pathStack.push(lastWorkingFolder.getPath());
                        } else {
                            logMessage("[ERROR]", "Invalid path. Please try again.");
                            return;
                        }
                    }
                
                    while (true) {
                        File folder = new File(pathStack.peek());
                        File[] listOfFilesAndFolders = folder.listFiles();
                
                        // List files and folders
                        System.out.printf(formatString, "Current Directory: " + folder.getAbsolutePath());
                        System.out.printf(formatString, "Select a file or folder:");
                        for (int i = 0; i < listOfFilesAndFolders.length; i++) {
                            String type = listOfFilesAndFolders[i].isDirectory() ? "[Folder]" : "[File]";
                            System.out.printf(formatString, (i + 1) + ". " + listOfFilesAndFolders[i].getName() + " " + type);
                        }
                        System.out.printf(formatString, (listOfFilesAndFolders.length + 1) + ". Go up a level");
                        System.out.printf(formatString, (listOfFilesAndFolders.length + 2) + ". Exit");
                
                        // Get user input
                        System.out.print("Enter the number corresponding to your choice: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine();  // Clear the scanner
                
                        // Validate user input and act accordingly
                        if (choice == listOfFilesAndFolders.length + 1) {
                            // Go up a level
                            if (pathStack.size() > 1) {
                                pathStack.pop();
                            } else {
                                System.out.printf(formatString, "You are at the root directory.");
                            }
                        } else if (choice == listOfFilesAndFolders.length + 2) {
                            // Exit
                            return;
                        } else if (choice > 0 && choice <= listOfFilesAndFolders.length) {
                            File selected = listOfFilesAndFolders[choice - 1];
                            if (selected.isDirectory()) {
                                pathStack.push(selected.getPath());
                            } else {
                                String errorSummary = library.addBooksFromFile(selected.getPath(), 1000);
                                if (errorSummary.isEmpty()) {
                                    logMessage("[INFO]", "Books added successfully!");
                                } else {
                                    logMessage("[ERROR]", "Books added with some errors:\n" + errorSummary);
                                }
                                return;
                            }
                        } else {
                            System.out.printf(formatString, "[ERROR]: Invalid selection. Please try again.");
                        }
                    }
                }
                
                
                

               /**
                * Removes a book from the library by Barcode.
                *
                * @param library The library instance.
                * @param scanner The scanner for user input.
                */
                private static void removeBook(Library library, Scanner scanner) {
                    System.out.println("Would you like to remove a book by:");
                    System.out.println("1. Barcode");
                    System.out.println("2. Title");
                    System.out.print("Enter your choice (1/2): ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                
                    if (choice == 1) {
                        // Remove by Barcode
                        System.out.print("Enter book Barcode to remove: ");
                        int barcode = scanner.nextInt();
                        scanner.nextLine();
                        boolean isRemoved = library.removeBookById(barcode);
                        if (isRemoved) {
                            logMessage("[INFO]", "Book removed successfully!");
                        } else {
                            logMessage("[ERROR]", "Failed to remove book. Barcode may not exist.");
                        }
                    } else if (choice == 2) {
                        // Remove by Title
                        System.out.print("Enter book Title to remove: ");
                        String title = scanner.nextLine();
                
                        // Search for the book by title
                        Map<String, List<Integer>> searchResults = library.searchByTitle(title);
                        List<Integer> exactMatches = searchResults.get("exact");
                        List<Integer> closeMatches = searchResults.get("close");
                
                        if (!exactMatches.isEmpty()) {
                            if (exactMatches.size() == 1) {
                                int barcode = exactMatches.get(0);
                                boolean isRemoved = library.removeBookById(barcode);
                                if (isRemoved) {
                                    logMessage("[INFO]", "Book removed successfully!");
                                } else {
                                    logMessage("[ERROR]", "Failed to remove book. Please try again later.");
                                }
                            } else {
                                System.out.println("Multiple exact matches found. Barcodes: " + exactMatches.toString());
                                System.out.print("Enter the Barcode of the book you want to remove, or type 'cancel' to cancel: ");
                                String input = scanner.nextLine();
                
                                if ("cancel".equalsIgnoreCase(input)) {
                                    logMessage("[INFO]", "Operation cancelled.");
                                    return;
                                }
                
                                try {
                                    int chosenBarcode = Integer.parseInt(input);
                                    if (exactMatches.contains(chosenBarcode)) {
                                        boolean isRemoved = library.removeBookById(chosenBarcode);
                                        if (isRemoved) {
                                            logMessage("[INFO]", "Book removed successfully!");
                                        } else {
                                            logMessage("[ERROR]", "Failed to remove book. Please try again later.");
                                        }
                                    } else {
                                        logMessage("[ERROR]", "Invalid Barcode selected.");
                                    }
                                } catch (NumberFormatException e) {
                                    logMessage("[ERROR]", "Invalid input. Please enter a valid Barcode.");
                                }
                            }
                        } else {
                            logMessage("[INFO]", "No books with that title exist.");
                        }
                    } else {
                        logMessage("[ERROR]", "Invalid choice. Please enter 1 or 2.");
                    }
                }
                

               
               /**checkOutBookByTitle
             * Checks out a book by title.
             *
             * This method allows a user to check out a book from the library by providing the book's title.
             * It searches for the book by title, handles multiple matches, and performs the check-out operation.
             *
             * @param library  The Library object representing the library's database.
             * @param scanner  The Scanner for user input.
             */
                private static void checkOutBookByTitle(Library library, Scanner scanner) {
                    // Prompt the user to enter the book title they want to check out.
                    System.out.print("Enter book Title to Check Out: ");
                    String title = scanner.nextLine();
                
                    // Search for the book by title and retrieve exact and close matches.
                    Map<String, List<Integer>> searchResults = library.searchByTitle(title);
                    List<Integer> exactMatches = searchResults.get("exact");
                    List<Integer> closeMatches = searchResults.get("close");
                
                    // Remove books that are already checked out from the exact matches.
                    exactMatches.removeIf(barcode -> {
                        Book book = library.getBookByBarcode(barcode);
                        // Check if the book exists and is currently checked out.
                        return book != null && book.getStatus();
                    });
                
                    // Remove books that are already checked out from the close matches.
                    closeMatches.removeIf(barcode -> {
                        Book book = library.getBookByBarcode(barcode);
                        // Check if the book exists and is currently checked out.
                        return book != null && book.getStatus();
                    });
                
                    if (!exactMatches.isEmpty()) {
                        if (exactMatches.size() == 1) {
                            // If there is only one exact match, check it out.
                            int barcode = exactMatches.get(0);
                            if (library.changeBookStatus(library.getIndexByBarcode(barcode), true)) {
                                logMessage("[INFO]", "Book checked out successfully!");
                                
                                return;
                            } else {
                                logMessage("[ERROR]", "Failed to check out book. Please try again later.");
                            }
                        } else {
                            // If there are multiple exact matches, prompt the user to choose one.
                            System.out.println("Multiple exact matches found. Barcodes: " + exactMatches.toString());
                            System.out.print("Enter the Barcode of the book you want to check out, or type 'cancel' to cancel: ");
                            String input = scanner.nextLine();
                
                            if ("cancel".equalsIgnoreCase(input)) {
                                logMessage("[INFO]", "Operation cancelled.");
                                return;
                            }
                
                            try {
                                int chosenBarcode = Integer.parseInt(input);
                                if (exactMatches.contains(chosenBarcode)) {
                                    // Check out the chosen book by its barcode.
                                    if (library.changeBookStatus(library.getIndexByBarcode(chosenBarcode), true)) {
                                        logMessage("[INFO]", "Book checked out successfully!");
                                        LocalDate today = LocalDate.now();
                                library.getBookByBarcode(chosenBarcode).setDueDate(today.plusDays(28).toString());
                                        return;
                                    } else {
                                        logMessage("[ERROR]", "Failed to check out book. Please try again later.");
                                    }
                                } else {
                                    logMessage("[ERROR]", "Invalid Barcode selected.");
                                }
                            } catch (NumberFormatException e) {
                                logMessage("[ERROR]", "Invalid input. Please enter a valid Barcode.");
                            }
                        }
                    } else if (!closeMatches.isEmpty()) {
                        // If there are close matches, prompt the user to choose from them.
                        System.out.println("Multiple close matches found. Barcodes: " + closeMatches.toString());
                        System.out.print("Enter the Barcode of the book you want to check out from the close matches, or type 'cancel' to cancel: ");
                        String input = scanner.nextLine();
                
                        if ("cancel".equalsIgnoreCase(input)) {
                            logMessage("[INFO]", "Operation cancelled.");
                            return;
                        }
                
                        try {
                            int chosenBarcode = Integer.parseInt(input);
                            if (closeMatches.contains(chosenBarcode)) {
                                // Check out the chosen book from the close matches by its barcode.
                                if (library.changeBookStatus(library.getIndexByBarcode(chosenBarcode), true)) {
                                    logMessage("[INFO]", "Book checked out successfully!");
                                    LocalDate today = LocalDate.now();
                                    library.getBookByBarcode(chosenBarcode).setDueDate(today.plusDays(28).toString());
                                } else {
                                    logMessage("[ERROR]", "Failed to check out book. Please try again later.");
                                }
                            } else {
                                logMessage("[ERROR]", "Invalid Barcode selected.");
                            }
                        } catch (NumberFormatException e) {
                            logMessage("[ERROR]", "Invalid input. Please enter a valid Barcode.");
                        }
                    } else {
                        // If no matches were found, inform the user.
                        logMessage("[INFO]", "No available books with that title at this time.");
                    }
                }
                

                /**checkInBookByTitle
                 * Checks in a book by title.
                 *
                 * This method allows a user to check in a book to the library by providing the book's title.
                 * It searches for the book by title, handles multiple matches, and performs the check-in operation.
                 *
                 * @param library  The Library object representing the library's database.
                 * @param scanner  The Scanner for user input.
                 */
                private static void checkInBookByTitle(Library library, Scanner scanner) {
                    // Prompt the user to enter the book title they want to check in.
                    System.out.print("Enter book Title to Check In: ");
                    String title = scanner.nextLine();
                
                    // Search for the book by title and retrieve exact and close matches.
                    Map<String, List<Integer>> searchResults = library.searchByTitle(title);
                    List<Integer> exactMatches = searchResults.get("exact");
                    List<Integer> closeMatches = searchResults.get("close");
                
                    // Remove books that are already checked in from the exact matches.
                    exactMatches.removeIf(barcode -> {
                        Book book = library.getBookByBarcode(barcode);
                        // Check if the book exists and is currently checked in.
                        return book != null && !book.getStatus();
                    });
                
                    // Remove books that are already checked in from the close matches.
                    closeMatches.removeIf(barcode -> {
                        Book book = library.getBookByBarcode(barcode);
                        // Check if the book exists and is currently checked in.
                        return book != null && !book.getStatus();
                    });
                
                    if (!exactMatches.isEmpty()) {
                        if (exactMatches.size() == 1) {
                            // If there is only one exact match, check it in.
                            int barcode = exactMatches.get(0);
                            if (library.changeBookStatus(library.getIndexByBarcode(barcode), false)) {
                                logMessage("[INFO]", "Book checked in successfully!");
                                library.getBookByBarcode(barcode).setDueDate("");
                                return;
                            } else {
                                logMessage("[ERROR]", "Failed to check in book. Please try again later.");
                            }
                        } else {
                            // If there are multiple exact matches, prompt the user to choose one.
                            System.out.println("Multiple exact matches found. Barcodes: " + exactMatches.toString());
                            System.out.print("Enter the Barcode of the book you want to check in, or type 'cancel' to cancel: ");
                            String input = scanner.nextLine();
                
                            if ("cancel".equalsIgnoreCase(input)) {
                                logMessage("[INFO]", "Operation cancelled.");
                                return;
                            }
                
                            try {
                                int chosenBarcode = Integer.parseInt(input);
                                if (exactMatches.contains(chosenBarcode)) {
                                    // Check in the chosen book by its barcode.
                                    if (library.changeBookStatus(library.getIndexByBarcode(chosenBarcode), false)) {
                                        logMessage("[INFO]", "Book checked in successfully!");
                                library.getBookByBarcode(chosenBarcode).setDueDate("");
                                        return;
                                    } else {
                                        logMessage("[ERROR]", "Failed to check in book. Please try again later.");
                                    }
                                } else {
                                    logMessage("[ERROR]", "Invalid Barcode selected.");
                                }
                            } catch (NumberFormatException e) {
                                logMessage("[ERROR]", "Invalid input. Please enter a valid Barcode.");
                            }
                        }
                    } else if (!closeMatches.isEmpty()) {
                        // If there are close matches, prompt the user to choose from them.
                        System.out.println("Multiple close matches found. Barcodes: " + closeMatches.toString());
                        System.out.print("Enter the Barcode of the book you want to check in from the close matches, or type 'cancel' to cancel: ");
                        String input = scanner.nextLine();
                
                        if ("cancel".equalsIgnoreCase(input)) {
                            logMessage("[INFO]", "Operation cancelled.");
                            return;
                        }
                
                        try {
                            int chosenBarcode = Integer.parseInt(input);
                            if (closeMatches.contains(chosenBarcode)) {
                                // Check in the chosen book from the close matches by its barcode.
                                if (library.changeBookStatus(library.getIndexByBarcode(chosenBarcode), false)) {
                                    logMessage("[INFO]", "Book checked in successfully!");
                                library.getBookByBarcode(chosenBarcode).setDueDate("");
                                } else {
                                    logMessage("[ERROR]", "Failed to check in book. Please try again later.");
                                }
                            } else {
                                logMessage("[ERROR]", "Invalid Barcode selected.");
                            }
                        } catch (NumberFormatException e) {
                            logMessage("[ERROR]", "Invalid input. Please enter a valid Barcode.");
                        }
                    } else {
                        // If no matches were found, inform the user.
                        logMessage("[INFO]", "No available books with that title at this time.");
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