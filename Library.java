import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Main
 * The Library class represents a collection of books and provides methods 
 * for managing this collection. It serves as the core data structure in a 
 * Library Management System. The class allows for adding books from a file, 
 * removing books by their ID, listing all books with pagination, and 
 * retrieving attributes of the book collection, such as the total 
 * number of books, maximum book ID, maximum title length, and maximum 
 * author name length.
 */
public class Library {
               private ArrayList<Book> books;

               
               public Library() { books = new ArrayList<>(); }

              
               public void addBook(Book book) { books.add(book); }

               /**addBooksFromFile
                * Adds books to the library from a given file.
                *
                * @param filePath  The path to the file containing book information.
                * @param batchSize The number of books to add in a single batch.
                * @return A summary of any errors encountered.
                */
               public String addBooksFromFile(String filePath, int batchSize) {
                              File inputFile = new File(filePath);
                              ArrayList<Book> bookBatch = new ArrayList<>();
                              int skippedLineCount = 0;
                              HashSet<Integer> uniqueBookIds = new HashSet<>();
                              StringBuilder errorSummary = new StringBuilder();

                              try (Scanner fileScanner = new Scanner(inputFile, "UTF-8")) {
                                             int currentLineNumber = 0;

                                             while (fileScanner.hasNextLine()) {
                                                            // Increment the line number for each new line read from the file
                                                            currentLineNumber++;

                                                            // Read the next line from the file and trim any leading/trailing whitespace
                                                            String currentLine = fileScanner.nextLine().trim();

                                                            // Skip empty lines
                                                            if (currentLine.isEmpty()) {
                                                                           continue;
                                                            }

                                                            // Split the line into parts based on commas
                                                            String[] bookInfo = currentLine.split(",");

                                                            // Skip lines that don't contain exactly 3 parts (ID, title, author)
                                                            if (bookInfo.length != 3) {
                                                                           // System.out.println("Skipping malformed line " + currentLineNumber + ": " + currentLine);
                                                                           skippedLineCount++;
                                                                           continue;
                                                            }

                                                            try {
                                                                           // Parse the book ID, title, and author from the line
                                                                           int bookId = Integer.parseInt(bookInfo[0].trim());
                                                                           String bookTitle = bookInfo[1].trim();
                                                                           String bookAuthor = bookInfo[2].trim();

                                                                           // Define a pattern for valid characters in titles and authors
                                                                           Pattern validCharsPattern = Pattern.compile("[a-zA-Z0-9 ',-]+");

                                                                           // Skip lines with invalid characters in the title or author
                                                                           if (!validCharsPattern.matcher(bookTitle).matches() || !validCharsPattern.matcher(bookAuthor).matches()) {
                                                                                          // System.out.println("Skipping line with invalid characters at line " + currentLineNumber + ": " + currentLine);
                                                                                          skippedLineCount++;
                                                                                          continue;
                                                                           }

                                                                           // Skip lines with duplicate book IDs
                                                                           if (uniqueBookIds.contains(bookId)) {
                                                                                          // System.out.println("Skipping line with duplicate ID at line " + currentLineNumber + ": " + currentLine);
                                                                                          skippedLineCount++;
                                                                                          continue;
                                                                           }

                                                                           // Add the book ID to the set of unique IDs
                                                                           uniqueBookIds.add(bookId);

                                                                           // Skip lines with empty title or author
                                                                           if (bookTitle.isEmpty() || bookAuthor.isEmpty()) {
                                                                                          // System.out.println("Skipping line with empty title or author at line " + currentLineNumber + ": " + currentLine);
                                                                                          skippedLineCount++;
                                                                                          continue;
                                                                           }

                                                                           // Create a new Book object and add it to the batch
                                                                           Book newBook = new Book(bookId, bookTitle, bookAuthor);
                                                                           bookBatch.add(newBook);

                                                                           // If the batch size is reached, add all books in the batch to the library and clear the batch
                                                                           if (bookBatch.size() == batchSize) {
                                                                                          books.addAll(bookBatch);
                                                                                          bookBatch.clear();
                                                                           }
                                                            } catch (NumberFormatException e) {
                                                                           // Skip lines with invalid book IDs
                                                                           // System.out.println("Skipping line with invalid ID at line " + currentLineNumber + ": " + currentLine);
                                                                           skippedLineCount++;
                                                            }
                                             }

                                             // Add any remaining books in the batch to the library
                                             if (!bookBatch.isEmpty()) {
                                                            books.addAll(bookBatch);
                                             }
                              } catch (FileNotFoundException e) {
                                             errorSummary.append("Error: File not found - " + filePath);
                              } catch (SecurityException e) {
                                             errorSummary.append("Error: Insufficient permissions to read the file.");
                              } catch (Exception e) {
                                             errorSummary.append("An unexpected error occurred: " + e.getMessage());
                              }

                              if (skippedLineCount > 0) {
                                             errorSummary.append("Lines with errors: ").append(skippedLineCount).append("\n");
                              }

                              return errorSummary.toString();
               }

               /**removeBookById
                * Removes a book by its ID.
                *
                * @param id The ID of the book to remove.
                * @return {@code true} if a book was removed, {@code false} otherwise.
                */
               public boolean removeBookById(int id) {
                              int initialSize = books.size();             // Get the initial size of the books list
                              books.removeIf(book -> book.getId() == id); // Remove the book
                              int newSize = books.size();                 // Get the new size of the books list

                              return initialSize > newSize; // Return true if a book was removed, false otherwise
               }

               /**listAllBooks
                * Lists all books with pagination.
                *
                * @param page       The page number.
                * @param pageSize   The number of books to display per page.
                * @param rowFormat  The format for displaying each book row.
                */
               public void listAllBooks(int page, int pageSize, int dynamicWidth) {
                              int start = page * pageSize;
                              int end = Math.min(start + pageSize, books.size());

                              int idWidth = String.valueOf(getMaxId()).length();
                              int titleWidth = getMaxTitleLength();
                              int authorWidth = getMaxAuthorLength();

                              // Adding 8 for the four '|' separators and spaces
                              int contentWidth = idWidth + titleWidth + authorWidth + 8;
                              int remainingSpace = dynamicWidth - contentWidth;

                              int extraSpaceForTitle = remainingSpace / 2;
                              int extraSpaceForAuthor = remainingSpace / 2;

                              if (remainingSpace % 2 != 0) {
                                             extraSpaceForTitle += 1;
                              }

                              // Create a format string that respects the dynamicWidth
                              String rowFormat = "| %-" + idWidth + "s | %-" + (titleWidth + extraSpaceForTitle) + "s | %-" + (authorWidth + extraSpaceForAuthor) + "s |";

                              for (int i = start; i < end; i++) {
                                             Book book = books.get(i);
                                             // Use String.format to ensure extra spaces are added at the correct positions
                                             String formattedTitle = String.format("%-" + (titleWidth + extraSpaceForTitle) + "s", book.getTitle());
                                             String formattedAuthor = String.format("%-" + (authorWidth + extraSpaceForAuthor) + "s", book.getAuthor());
                                             System.out.printf(rowFormat, book.getId(), formattedTitle, formattedAuthor);
                                             System.out.println();
                              }
               }
               /**getBookByIndex
                * Gets a book by its index in the ArrayList.
                *
                * @param index The index of the book.
                * @return The book, or {@code null} if the index is out of bounds.
                */
               public Book getBookByIndex(int index) {
                              if (index >= 0 && index < books.size()) {
                                             return books.get(index);
                              }
                              return null; // Return null if the index is out of bounds
               }

               /**
                * Gets the total number of books in the library.
                *
                * @return The total number of books.
                */
               public int getTotalBooks() { return books.size(); }

               /**getMaxId
                * Gets the maximum book ID in the library.
                *
                * @return The maximum book ID, or 0 if the library is empty.
                */
               public int getMaxId() {
                              int maxId = 0; // Initialize maxId to 0

                              // Iterate through all books in the library
                              for (Book book : books) {
                                             // Update maxId if the current book's ID is greater
                                             if (book.getId() > maxId) {
                                                            maxId = book.getId();
                                             }
                              }

                              return maxId; // Return the maximum ID found
               }

               /**getMaxTitleLength
                * Gets the maximum length of all book titles in the library.
                *
                * @return The maximum title length, or 20 if the library is empty.
                */
               public int getMaxTitleLength() {
                              // Stream through all books, get their titles, and find the maximum length
                              return books.stream()
                                  .map(Book::getTitle)      // Extract the titles from the books
                                  .mapToInt(String::length) // Convert titles to their lengths
                                  .max()                    // Find the maximum length
                                  .orElse(20);              // Return 20 if no maximum is found
               }

               /**getMaxAuthorLengt
                * Gets the maximum length of all book authors in the library.
                *
                * @return The maximum author name length, or 20 if the library is empty.
                */
               public int getMaxAuthorLength() {
                              // Stream through all books, get their authors, and find the maximum length
                              return books.stream()
                                  .map(Book::getAuthor)     // Extract the authors from the books
                                  .mapToInt(String::length) // Convert authors to their lengths
                                  .max()                    // Find the maximum length
                                  .orElse(20);              // Return 20 if no maximum is found
               }
}