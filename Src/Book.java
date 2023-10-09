import java.util.HashMap;

/**Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Book
 * Represents a book with attributes such as barcode, title, and author.
 */
public class Book {
               private int barcode;
               private String title;
               private String author;
               private boolean status;
               private String dueDate;
              
               public Book(int barcode, String title, String author) {
                              this.barcode = barcode;
                              this.title = title;
                              this.author = author;
                              this.status = false;
                              this.dueDate="";
               }

               /**
                * Gets the barcode of the book.
                *
                * @return The book's barcode.
                */
               public int getId() { return barcode; }

               /**
                * Gets the title of the book.
                *
                * @return The book's title.
                */
               public String getTitle() { return title; }

               /**
                * Gets the author of the book.
                *
                * @return The book's author.
                */
               public String getAuthor() { return author; }

               /**
                * Gets the author of the book.
                *
                * @return The book's author.
                */
               public boolean getStatus() { return status; }

                 /**
                * Gets the due date of the book when checked out.
                *
                * @return The book's due date.
                */
               public String getDueDate() { return dueDate; }
               /**
                * Sets the title of the book.
                *
                * @param title The new title.
                */
               public void setTitle(String title) { this.title = title; }

               /**
                * Sets the author of the book.
                *
                * @param author The new author.
                */
               public void setAuthor(String author) { this.author = author; }

               /**
                * Sets the status of the book.
                *
                * @param author The new status.
                */
               public void setStatus(boolean status) { this.status = status; }

               /**
                * Sets the due date of the book.
                *
                * @param author The new status.
                */
               public void setDueDate(String dueDate) { this.dueDate = dueDate; }

               /**
                * Converts the book attributes to a dictionary representation.
                *
                * @return A HashMap containing the book's attributes.
                */
               public HashMap<String, String> toDict() {
                              HashMap<String, String> dict = new HashMap<>();
                              dict.put("barcode", String.valueOf(barcode));
                              dict.put("title", title);
                              dict.put("author", author);
                              dict.put("status",String.valueOf(status));
                              dict.put("dueDate",String.valueOf(dueDate));
                              return dict;
               }

               /**
                * Provides a string representation of the book.
                *
                * @return A string containing the book's barcode, title, and author.
                */
               @Override
               public String toString() {
                              return "barcode: " + barcode + ", Title: " + title + ", Author: " + author + ", Checked Out? :" + status + "Due on : " + dueDate;
               }

               /**
                * Compares the equality of this book with another based on their IDs.
                *
                * @param other The other book to compare with.
                * @return True if the IDs are equal, otherwise false.
                */
               public boolean equals(Book other) { return this.barcode == other.barcode; }
}
