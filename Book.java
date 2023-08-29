import java.util.HashMap;

/**Axel Diaz | CEN 3024C Software Development | - CRN: 17125
 * Main
 * Represents a book with attributes such as ID, title, and author.
 */
public class Book {
               private int id;
               private String title;
               private String author;

              
               public Book(int id, String title, String author) {
                              this.id = id;
                              this.title = title;
                              this.author = author;
               }

               /**
                * Gets the ID of the book.
                *
                * @return The book's ID.
                */
               public int getId() { return id; }

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
                * Converts the book attributes to a dictionary representation.
                *
                * @return A HashMap containing the book's attributes.
                */
               public HashMap<String, String> toDict() {
                              HashMap<String, String> dict = new HashMap<>();
                              dict.put("id", String.valueOf(id));
                              dict.put("title", title);
                              dict.put("author", author);
                              return dict;
               }

               /**
                * Provides a string representation of the book.
                *
                * @return A string containing the book's ID, title, and author.
                */
               @Override
               public String toString() {
                              return "ID: " + id + ", Title: " + title + ", Author: " + author;
               }

               /**
                * Compares the equality of this book with another based on their IDs.
                *
                * @param other The other book to compare with.
                * @return True if the IDs are equal, otherwise false.
                */
               public boolean equals(Book other) { return this.id == other.id; }
}
