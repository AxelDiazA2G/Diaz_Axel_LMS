import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
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

     /**
      * Adds a new book to the library.
      *
      * @param book The book to be added to the library.
      */
     public void addBook(Book book) {
          String sql = "INSERT INTO books (id, title, author) VALUES (?, ?, ?)";
          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setInt(1, book.getId());
               pstmt.setString(2, book.getTitle());
               pstmt.setString(3, book.getAuthor());
               pstmt.executeUpdate();
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }
     }


     /**
      * Adds books to the library from a given file.
      *
      * @param filePath  The path to the file containing book information.
      * @param batchSize The number of books to add in a single batch.
      * @return A summary of any errors encountered during the process.
      */
     public String addBooksFromFile(String filePath, int batchSize) {
     File inputFile = new File(filePath);
     int skippedLineCount = 0;
     int validBooks = 0;
     HashSet<Integer> uniqueBookIds = new HashSet<>();
     StringBuilder errorSummary = new StringBuilder();
     String insertSql = "INSERT INTO books (id, title, author) VALUES (?, ?, ?)";
     Connection conn = null;

     try {
          conn = getDatabaseConnection();
          conn.setAutoCommit(false); // Use transaction to batch insert
          PreparedStatement pstmt = conn.prepareStatement(insertSql);
          Scanner fileScanner = new Scanner(inputFile, "UTF-8");
          int currentLineNumber = 0;

          while (fileScanner.hasNextLine()) {
               // ... rest of the code to process each line ...

               if (validBooks % batchSize == 0 && validBooks > 0) {
                    pstmt.executeBatch(); // Execute batch insert
                    conn.commit(); // Commit transaction
               }
          }

          if (validBooks % batchSize != 0) {
               pstmt.executeBatch(); // Execute the final batch
               conn.commit(); // Commit the final transaction
          }

          pstmt.close();
          fileScanner.close();
     } catch (FileNotFoundException e) {
          errorSummary.append("Error: File not found - ").append(filePath);
     } catch (SecurityException e) {
          errorSummary.append("Error: Insufficient permissions to read the file.");
     } catch (SQLException e) {
          errorSummary.append("SQL Error: ").append(e.getMessage());
          try {
               if (conn != null) {
                    conn.rollback(); // Rollback in case of error
               }
          } catch (SQLException ex) {
               errorSummary.append("\nFailed to rollback: ").append(ex.getMessage());
          }
     } catch (Exception e) {
          errorSummary.append("An unexpected error occurred: ").append(e.getMessage());
     } finally {
          try {
               if (conn != null && !conn.isClosed()) {
                    conn.close();
               }
          } catch (SQLException e) {
               errorSummary.append("\nFailed to close database connection: ").append(e.getMessage());
          }
     }

     // ... handle skippedLineCount and error summary ...

     return errorSummary.toString();
}


     /**
      * Establishes a connection to the database.
      *
      * @return A Connection object to the configured database.
      * @throws SQLException If a database access error occurs.
      */
     private Connection getDatabaseConnection() throws SQLException {
          String url = "jdbc:mysql://127.0.0.1:3306/lms";
          String user = "root";
          String password = "password";
          return DriverManager.getConnection(url, user, password);
     }


     /**removeBookById
      * Removes a book by its ID.
      *
      * @param id The ID of the book to remove.
      * @return {@code true} if a book was removed, {@code false} otherwise.
      */
     public boolean removeBookById(int id) {
          String sql = "DELETE FROM books WHERE barcode = ?";
          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setInt(1, id);
               int affectedRows = pstmt.executeUpdate();
               return affectedRows > 0;
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
               return false;
          }
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
      * @return True if the status change was successful, false otherwise.
      */
     public boolean changeBookStatus(int barcode) {
          String getStatusSql = "SELECT status FROM books WHERE barcode = ?";
          String updateStatusSql = "UPDATE books SET status = ?, dueDate = CASE WHEN status = 1 THEN NOW() ELSE NULL END WHERE barcode = ?";

          try (Connection conn = getDatabaseConnection();
               PreparedStatement getStatusStmt = conn.prepareStatement(getStatusSql);
               PreparedStatement updateStatusStmt = conn.prepareStatement(updateStatusSql)) {

               // Check the current status of the book
               getStatusStmt.setInt(1, barcode);
               ResultSet rs = getStatusStmt.executeQuery();
               if (rs.next()) {
                    boolean currentStatus = rs.getBoolean("status");
                    boolean newStatus = !currentStatus;

                    // Update the status and due date based on the new status
                    updateStatusStmt.setBoolean(1, newStatus);
                    updateStatusStmt.setInt(2, barcode);
                    int affectedRows = updateStatusStmt.executeUpdate();

                    return affectedRows > 0;
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }
          return false;
     }

     /**
      * Gets the total number of books in the library.
      *
      * @return The total number of books.
      */
     public int getTotalBooks() {
          String sql = "SELECT COUNT(*) FROM books";
          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql);
               ResultSet rs = pstmt.executeQuery()) {

               if (rs.next()) {
                    return rs.getInt(1);
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }
          return 0;
     }

     /**
      * Retrieves all book titles from the database.
      *
      * @return A list of all book titles.
      */
     public List<String> getAllBookTitles() {
          List<String> titles = new ArrayList<>();
          String sql = "SELECT title FROM books";

          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql);
               ResultSet rs = pstmt.executeQuery()) {

               while (rs.next()) {
                    String title = rs.getString("title");
                    titles.add(title);
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }
          return titles;
     }

     /**
      * Gets the count of books that are currently checked out.
      *
      * @return The number of checked out books.
      */
     public int getCheckedOutBooksCount() {
          String sql = "SELECT COUNT(*) FROM books WHERE status = true";  // Adjust the condition based on your schema
          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql);
               ResultSet rs = pstmt.executeQuery()) {

               if (rs.next()) {
                    return rs.getInt(1);
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }
          return 0;
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
     public Map<String, List<Integer>> searchByTitle(String targetTitle) {
          Map<String, List<Integer>> resultMap = new HashMap<>();
          List<Integer> exactMatches = new ArrayList<>();
          List<Integer> closeMatches = new ArrayList<>();
          String sql = "SELECT barcode, title FROM books WHERE title LIKE ?";

          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {

               pstmt.setString(1, "%" + targetTitle + "%");
               ResultSet rs = pstmt.executeQuery();

               while (rs.next()) {
                    int id = rs.getInt("barcode");
                    String title = rs.getString("title");

                    if (title.equalsIgnoreCase(targetTitle)) {
                         exactMatches.add(id);
                    } else {
                         closeMatches.add(id);
                    }
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }

          resultMap.put("exact", exactMatches);
          resultMap.put("close", closeMatches);
          return resultMap;
     }


     /**listAllBooks
      * Lists all books with pagination.
      *
      * @param page       The page number.
      * @param pageSize   The number of books to display per page.
      */
     public Object[][] listAllBooks(int page, int pageSize) {
          String sql = "SELECT * FROM books ORDER BY title LIMIT ? OFFSET ?";
          ArrayList<Book> pageBooks = new ArrayList<>();
          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setInt(1, pageSize);
               pstmt.setInt(2, page * pageSize);
               ResultSet rs = pstmt.executeQuery();
               while (rs.next()) {
                    int id = rs.getInt("barcode");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean status = rs.getBoolean("status"); // Assuming there's a status column
                    String dueDate = String.valueOf(rs.getDate("dueDate"));    // Assuming there's a due_date column

                    pageBooks.add(new Book(id, title, author, status, dueDate));
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }

          Object[][] tableData = new Object[pageBooks.size()][5];
          for (int i = 0; i < pageBooks.size(); i++) {
               Book book = pageBooks.get(i);
               tableData[i][0] = book.getId();
               tableData[i][1] = book.getTitle();
               tableData[i][2] = book.getAuthor();
               tableData[i][3] = book.getStatus();
               tableData[i][4] = book.getDueDate();
          }

          return tableData;
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
      * @return The book, or {@code null} if the index is out of bounds.
      */
     public Book getBookByBarcode(int barcode) {
          String sql = "SELECT * FROM books WHERE barcode = ?";
          try (Connection conn = getDatabaseConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {

               pstmt.setInt(1, barcode);
               ResultSet rs = pstmt.executeQuery();

               if (rs.next()) {
                    int id = rs.getInt("barcode"); // Adjust these column names as per your database schema
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean status = rs.getBoolean("status");
                    String dueDate = rs.getString("dueDate");

                    return new Book(id, title, author, status, dueDate); // Create and return the Book object
               }
          } catch (SQLException e) {
               System.out.println("SQL Error: " + e.getMessage());
          }
          return null; // Return null if no book is found with the given barcode
     }
}
