import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Library
 * The Library class represents a collection of books and provides methods
 * for managing this collection. It serves as the core data structure in a
 * Library Management System. The class allows for adding books from a file,
 * removing books by their ID, listing all books with pagination, and
 * retrieving attributes of the book collection, such as the total
 * number of books, maximum book ID, maximum title length, and maximum
 * author name length.
 */
public class Library
{
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
     public String addBooksFromFile(String filePath, int batchSize)
     {
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

                    // Read the next line from the file and trim any leading/trailing
                    // whitespace
                    String currentLine = fileScanner.nextLine().trim();

                    // Skip empty lines
                    if (currentLine.isEmpty()) {
                         continue;
                    }

                    // Split the line into parts based on commas
                    String[] bookInfo = currentLine.split(",");

                    // Skip lines that don't contain exactly 3 parts (ID, title, author)
                    if (bookInfo.length != 3) {
                         // System.out.println("Skipping malformed line " +
                         // currentLineNumber + ": " + currentLine);
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
                         if (!validCharsPattern.matcher(bookTitle).matches() ||
                             !validCharsPattern.matcher(bookAuthor).matches()) {
                              // System.out.println("Skipping line with invalid characters
                              // at line " + currentLineNumber + ": " + currentLine);
                              skippedLineCount++;
                              continue;
                         }

                         // Skip lines with duplicate book IDs
                         if (uniqueBookIds.contains(bookId)) {
                              // System.out.println("Skipping line with duplicate ID at
                              // line " + currentLineNumber + ": " + currentLine);
                              skippedLineCount++;
                              continue;
                         }

                         // Add the book ID to the set of unique IDs
                         uniqueBookIds.add(bookId);

                         // Skip lines with empty title or author
                         if (bookTitle.isEmpty() || bookAuthor.isEmpty()) {
                              // System.out.println("Skipping line with empty title or
                              // author at line " + currentLineNumber + ": " +
                              // currentLine);
                              skippedLineCount++;
                              continue;
                         }

                         // Create a new Book object and add it to the batch
                         Book newBook = new Book(bookId, bookTitle, bookAuthor);
                         bookBatch.add(newBook);

                         // If the batch size is reached, add all books in the batch to
                         // the library and clear the batch
                         if (bookBatch.size() == batchSize) {
                              books.addAll(bookBatch);
                              bookBatch.clear();
                         }
                    } catch (NumberFormatException e) {
                         // Skip lines with invalid book IDs
                         // System.out.println("Skipping line with invalid ID at line " +
                         // currentLineNumber + ": " + currentLine);
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
               errorSummary.append("Lines with errors: ")
                 .append(skippedLineCount)
                 .append("\n");
          }

          return errorSummary.toString();
     }

     /**removeBookById
      * Removes a book by its ID.
      *
      * @param id The ID of the book to remove.
      * @return {@code true} if a book was removed, {@code false} otherwise.
      */
     public boolean removeBookById(int id)
     {
          int initialSize = books.size(); // Get the initial size of the books list
          books.removeIf(book -> book.getId() == id); // Remove the book
          int newSize = books.size(); // Get the new size of the books list

          return initialSize >
            newSize; // Return true if a book was removed, false otherwise
     }

     /**changeBookStatus
      * Changes the status of a book with the given barcode.
      *
      * This method updates the status of a book in the library's database by specifying
      * its barcode. If the status provided is different from the current status, it
      * updates the status and returns true, indicating a successful status change.
      * Otherwise, it returns false.
      *
      * @param barcode The barcode of the book to change the status for.
      * @param status  The new status to set (true for checked out, false for checked in).
      * @return True if the status change was successful, false otherwise.
      */
     public boolean changeBookStatus(int barcode, boolean status)
     {
          if (books.get(barcode).getStatus() != status) {
               books.get(barcode).setStatus(status);
               return true;
          }
          return false;
     }

     /**searchByTitle
      * Searches for books by title.
      *
      * This method searches the library's database for books with titles matching the
      * given targetTitle. It returns a map containing two lists: "exact" matches and
      * "close" matches based on title similarity.
      *
      * @param targetTitle The title to search for.
      * @return A map containing "exact" and "close" matches by barcode.
      */
     public Map<String, List<Integer>> searchByTitle(String targetTitle)
     {
          Map<String, List<Integer>> resultMap = new HashMap<>();
          List<Integer> closeMatches = new ArrayList<>();
          List<Integer> exactMatches = new ArrayList<>();

          for (Book book : books) {
               if (book.getTitle().equals(targetTitle)) {
                    exactMatches.add(book.getId());
               } else {
                    int distance = levenshteinDistance(book.getTitle(), targetTitle);
                    if (distance <= 3) {
                         closeMatches.add(book.getId());
                    }
               }
          }

          resultMap.put("exact", exactMatches);
          resultMap.put("close", closeMatches);
          return resultMap;
     }

     /**sortByTitle
      * Sorts the list of books by title in non-decreasing order.
      */
     public void sortByTitle() { mergeSort(0, books.size() - 1); }

     /**mergeSort
      * Sorts the list of books by title in non-decreasing order.
      */
     private void mergeSort(int left, int right)
     {
          if (left < right) {
               int mid = (left + right) / 2;

               // Sort the left and right halves
               mergeSort(left, mid);
               mergeSort(mid + 1, right);

               // Merge the sorted halves
               merge(left, mid, right);
          }
     }

     /**merge
      * Merges two sub-arrays into a single sorted array.
      *
      * @param left  The left index of the first sub-array.
      * @param mid   The middle index dividing the two sub-arrays.
      * @param right The right index of the second sub-array.
      */
     private void merge(int left, int mid, int right)
     {
          // Calculate the sizes of the two sub-arrays to be merged
          int n1 = mid - left + 1;
          int n2 = right - mid;

          // Create temporary arrays to hold the values of the sub-arrays
          ArrayList<Book> leftArray = new ArrayList<>();
          ArrayList<Book> rightArray = new ArrayList<>();

          // Copy the values to the temporary arrays
          for (int i = 0; i < n1; i++) {
               leftArray.add(books.get(left + i));
          }
          for (int j = 0; j < n2; j++) {
               rightArray.add(books.get(mid + 1 + j));
          }

          // Merge the temporary arrays back into the original array
          int i = 0, j = 0, k = left;
          while (i < n1 && j < n2) {
               if (leftArray.get(i).getTitle().compareTo(rightArray.get(j).getTitle()) <=
                   0) {
                    books.set(k, leftArray.get(i));
                    i++;
               } else {
                    books.set(k, rightArray.get(j));
                    j++;
               }
               k++;
          }

          // Copy any remaining elements in leftArray, if any
          while (i < n1) {
               books.set(k, leftArray.get(i));
               i++;
               k++;
          }

          // Copy any remaining elements in rightArray, if any
          while (j < n2) {
               books.set(k, rightArray.get(j));
               j++;
               k++;
          }
     }

     /**levenshteinDistance
      * Flexible string comparison
      *
      * @param a     string a
      * @param b   string b
      */
     private static int levenshteinDistance(String a, String b)
     {
          int[][] dp = new int[a.length() + 1][b.length() + 1];

          for (int i = 0; i <= a.length(); i++) {
               for (int j = 0; j <= b.length(); j++) {
                    if (i == 0) {
                         dp[0][j] = j;
                    } else if (j == 0) {
                         dp[i][0] = i;
                    } else {
                         dp[i][j] = Math.min(
                           Math.min(dp[i - 1][j - 1] +
                                      (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                                    dp[i - 1][j] + 1),
                           dp[i][j - 1] + 1);
                    }
               }
          }

          return dp[a.length()][b.length()];
     }

     /**listAllBooks
      * Lists all books with pagination.
      *
      * @param page       The page number.
      * @param pageSize   The number of books to display per page.
      * @param rowFormat  The format for displaying each book row.
      */
     public void listAllBooks(int page, int pageSize, int dynamicWidth)
     {
          int start = page * pageSize;
          int end = Math.min(start + pageSize, books.size());

          int idWidth;
          if (String.valueOf(getMaxId()).length() > 10) {
               idWidth = String.valueOf(getMaxId()).length();
          } else {
               idWidth = 7;
          };
          int titleWidth = getMaxTitleLength();
          int authorWidth = getMaxAuthorLength();
          int statusWidth = 5;
          int dueDateWidth = 10;

          // Adding 8 for the four '|' separators and spaces
          int contentWidth =
            idWidth + titleWidth + authorWidth + statusWidth + dueDateWidth + 8;
          int remainingSpace = dynamicWidth - contentWidth;

          int extraSpaceForTitle = remainingSpace / 2;
          int extraSpaceForAuthor = remainingSpace / 2;

          if (remainingSpace % 2 != 0) {
               extraSpaceForTitle += 1;
          }

          // Create a format string that respects the dynamicWidth
          String rowFormat = "| %-" + idWidth + "s | %-" +
                             (titleWidth + extraSpaceForTitle) + "s | %-" +
                             (authorWidth + extraSpaceForAuthor) + "s | %-" +
                             statusWidth + "s | %-" + dueDateWidth + "s |";

          for (int i = start; i < end; i++) {
               Book book = books.get(i);
               // Use String.format to ensure extra spaces are added at the correct
               // positions
               String formattedTitle = String.format(
                 "%-" + (titleWidth + extraSpaceForTitle) + "s", book.getTitle());
               String formattedAuthor = String.format(
                 "%-" + (authorWidth + extraSpaceForAuthor) + "s", book.getAuthor());
               String formattedStatus = String.format("%-" + statusWidth + "s",
                                                      String.valueOf(book.getStatus()));
               String formattedDueDate =
                 String.format("%-" + dueDateWidth + "s", book.getDueDate());
               System.out.printf(rowFormat,
                                 book.getId(),
                                 formattedTitle,
                                 formattedAuthor,
                                 formattedStatus,
                                 formattedDueDate);
               System.out.println();
          }
     }
     /**getBookByIndex
      * Gets a book by its index in the ArrayList.
      *
      * @param index The index of the book.
      * @return The book, or {@code null} if the index is out of bounds.
      */
     public Book getBookByIndex(int index)
     {
          if (index >= 0 && index < books.size()) {
               return books.get(index);
          }
          return null; // Return null if the index is out of bounds
     }

     /**getBookByBarcode
      * Gets a book by its Barcode in the ArrayList.
      *
      * @param index The index of the book.
      * @return The book, or {@code null} if the index is out of bounds.
      */
     public Book getBookByBarcode(int barcode)
     {
          for (Book book : books) {
               if (book.getId() == barcode) {
                    return book;
               }
          }
          return null; // Return null if no book with the given barcode is found
     }

     public Integer getIndexByBarcode(int barcode)
     {
          for (int i = 0; i < books.size(); i++) {
               if (books.get(i).getId() == barcode) {
                    return i; // Return the index of the book with the given barcode
               }
          }
          return null; // Return null if no book with the given barcode is found
     }

     /**getTotalBooks
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
     public int getMaxId()
     {
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
     public int getMaxTitleLength()
     {
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
     public int getMaxAuthorLength()
     {
          // Stream through all books, get their authors, and find the maximum length
          return books.stream()
            .map(Book::getAuthor)     // Extract the authors from the books
            .mapToInt(String::length) // Convert authors to their lengths
            .max()                    // Find the maximum length
            .orElse(20);              // Return 20 if no maximum is found
     }
}
