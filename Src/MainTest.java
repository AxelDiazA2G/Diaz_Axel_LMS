import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void testAddBook() {
        Library library = new Library();
        Book book = new Book(123467890, "Test Book", "Test Author");
        library.addBook(book);
        assertNotNull(library.getBookByBarcode(123467890));
        assertEquals(book, library.getBookByBarcode(123467890));
    }

    @Test
    void testRemoveBookByBarcode() {
        Library library = new Library();
        Book book = new Book(123467890, "Test Book", "Test Author");
        library.addBook(book);
        boolean isRemoved = library.removeBookById(123467890);
        assertTrue(isRemoved);
        assertNull(library.getBookByBarcode(123467890));
    }

    @Test
    void testRemoveBookByTitle() {
        Library library = new Library();
        Book book1 = new Book(123467890, "Test Book", "Test Author");
        Book book2 = new Book(987653210, "Test Book", "Another Author");
        library.addBook(book1);
        library.addBook(book2);

        Map<String, List<Integer>> searchResults = library.searchByTitle("Test Book");
        List<Integer> exactMatches = searchResults.get("exact");

        assertFalse(exactMatches.isEmpty());

        if (exactMatches.size() > 0) {
            int barcodeToRemove = exactMatches.get(0);  // Picking the first barcode for demonstration.
            boolean isRemoved = library.removeBookById(barcodeToRemove);
            assertTrue(isRemoved);
            assertNull(library.getBookByBarcode(barcodeToRemove));
        }
    }

    @Test
    void testCheckOutBookByTitle() {
        Library library = new Library();
        Book book1 = new Book(1234567890, "Test Book", "Test Author");
        library.addBook(book1);

        Map<String, List<Integer>> searchResults = library.searchByTitle("Test Book");
        List<Integer> exactMatches = searchResults.get("exact");

        if (exactMatches.size() == 1) {
            int barcode = exactMatches.get(0);
            assertTrue(library.changeBookStatus(library.getIndexByBarcode(barcode), true));
            assertEquals("true", String.valueOf(library.getBookByBarcode(barcode).getStatus()));
            assertNotNull(library.getBookByBarcode(barcode).getDueDate());
        }
    }

    @Test
    void testCheckInBookByTitle() {
        Library library = new Library();
        Book book1 = new Book(1234567890, "Test Book", "Test Author");
        book1.setStatus(true);
        book1.setDueDate(LocalDate.now().plusDays(28).toString());
        library.addBook(book1);

        Map<String, List<Integer>> searchResults = library.searchByTitle("Test Book");
        List<Integer> exactMatches = searchResults.get("exact");

        if (exactMatches.size() == 1) {
            int barcode = exactMatches.get(0);
            assertTrue(library.changeBookStatus(library.getIndexByBarcode(barcode), false));
            assertEquals("false", String.valueOf(library.getBookByBarcode(barcode).getStatus()));
            assertNull(library.getBookByBarcode(barcode).getDueDate());
        }
    }
}
