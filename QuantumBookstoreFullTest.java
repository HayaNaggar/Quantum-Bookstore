import java.util.*;

// Base Book class 
abstract class Book {
    protected String isbn;
    protected String title;
    protected String author;
    protected int yearPublished;
    protected double price;
    
    public Book(String isbn, String title, String author, int yearPublished, double price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.price = price;
    }
    
    //  to be implemented by each book type
    public abstract void processPurchase(int quantity, String email, String address);
    
    //  to check if book can be purchased
    public abstract boolean isAvailable(int quantity);
    
    //to reduce inventory after purchase
    public abstract void reduceInventory(int quantity);
    
    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYearPublished() { return yearPublished; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return String.format("ISBN: %s, Title: %s, Author: %s, Year: %d, Price: $%.2f", 
               isbn, title, author, yearPublished, price);
    }
}

// Paper Book implementation
class PaperBook extends Book {
    private int stock;
    
    public PaperBook(String isbn, String title, String author, int yearPublished, double price, int stock) {
        super(isbn, title, author, yearPublished, price);
        this.stock = stock;
    }
    
    @Override
    public boolean isAvailable(int quantity) {
        return stock >= quantity;
    }
    
    @Override
    public void reduceInventory(int quantity) {
        stock -= quantity;
    }
    
    @Override
    public void processPurchase(int quantity, String email, String address) {
        ShippingService.ship(this, quantity, address);
        System.out.println("Quantum book store: Paper book shipped to " + address);
    }
    
    public int getStock() { return stock; }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", Stock: %d", stock);
    }
}

// EBook implementation
class EBook extends Book {
    private String fileType;
    
    public EBook(String isbn, String title, String author, int yearPublished, double price, String fileType) {
        super(isbn, title, author, yearPublished, price);
        this.fileType = fileType;
    }
    
    @Override
    public boolean isAvailable(int quantity) {
        return true; // EBooks are always available
    }
    
    @Override
    public void reduceInventory(int quantity) {
        // No need to reduce inventory for digital books
    }
    
    @Override
    public void processPurchase(int quantity, String email, String address) {
        MailService.sendEBook(this, email);
        System.out.println("Quantum book store: EBook sent to " + email);
    }
    
    public String getFileType() { return fileType; }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", File Type: %s", fileType);
    }
}

// Showcase/Demo Book implementation
class ShowcaseBook extends Book {
    
    public ShowcaseBook(String isbn, String title, String author, int yearPublished, double price) {
        super(isbn, title, author, yearPublished, price);
    }
    
    @Override
    public boolean isAvailable(int quantity) {
        return false; // Showcase books are not for sale
    }
    
    @Override
    public void reduceInventory(int quantity) {
        // No inventory to reduce for showcase books
    }
    
    @Override
    public void processPurchase(int quantity, String email, String address) {
        throw new IllegalStateException("Quantum book store: Showcase books are not for sale");
    }
    
    @Override
    public String toString() {
        return super.toString() + " (Showcase - Not for Sale)";
    }
}

// External service interfaces (no implementation required as per requirements)
class ShippingService {
    public static void ship(Book book, int quantity, String address) {
        
        System.out.println("Quantum book store: Shipping service called for " + book.getTitle());
    }
}

class MailService {
    public static void sendEBook(Book book, String email) {
      
        System.out.println("Quantum book store: Mail service called for " + book.getTitle());
    }
}

// Custom exception for bookstore operations
class BookstoreException extends Exception {
    public BookstoreException(String message) {
        super(message);
    }
}

// TODO:Main Bookstore class
class QuantumBookstore {
    private Map<String, Book> inventory;
    
    public QuantumBookstore() {
        this.inventory = new HashMap<>();
    }
    
    // Add a book to inventory
    public void addBook(Book book) {
        inventory.put(book.getIsbn(), book);
        System.out.println("Quantum book store: Added book - " + book.getTitle());
    }
    
    // Remove outdated books
    public List<Book> removeOutdatedBooks(int currentYear, int maxAge) {
        List<Book> removedBooks = new ArrayList<>();
        Iterator<Map.Entry<String, Book>> iterator = inventory.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, Book> entry = iterator.next();
            Book book = entry.getValue();
            
            if (currentYear - book.getYearPublished() > maxAge) {
                removedBooks.add(book);
                iterator.remove();
                System.out.println("Quantum book store: Removed outdated book - " + book.getTitle());
            }
        }
        
        return removedBooks;
    }
    
    // Buy a book
    public double buyBook(String isbn, int quantity, String email, String address) throws BookstoreException {
        Book book = inventory.get(isbn);
        
        if (book == null) {
            throw new BookstoreException("Quantum book store: Book with ISBN " + isbn + " not found");
        }
        
        if (!book.isAvailable(quantity)) {
            throw new BookstoreException("Quantum book store: Insufficient stock or book not available for purchase");
        }
        
        
        double totalAmount = book.getPrice() * quantity;
        
        
        book.reduceInventory(quantity);
        
       
        book.processPurchase(quantity, email, address);
        
        System.out.println("Quantum book store: Purchase successful - Total: $" + totalAmount);
        return totalAmount;
    }
    
    // Get book by ISBN
    public Book getBook(String isbn) {
        return inventory.get(isbn);
    }
    
    // Display all books
    public void displayInventory() {
        System.out.println("Quantum book store: Current Inventory:");
        for (Book book : inventory.values()) {
            System.out.println("Quantum book store: " + book.toString());
        }
    }
    
    // Get inventory size
    public int getInventorySize() {
        return inventory.size();
    }
}

// Test class (testcases)
public class QuantumBookstoreFullTest {
    public static void main(String[] args) {
        System.out.println("Quantum book store: Starting comprehensive test...\n");
        
        QuantumBookstore bookstore = new QuantumBookstore();
        
        // Test 1: Adding different types of books
        System.out.println("==Test 1: Adding Books ==");
        PaperBook paperBook = new PaperBook("978-0134685991", "Programming for Engineers", "John Smith", 2020, 45.99, 10);
        EBook eBook = new EBook("978-0135166307", "The Seven Habits of Highly Effective People", "Stephen Covey", 1989, 29.99, "PDF");
        ShowcaseBook showcaseBook = new ShowcaseBook("978-0596009205", "A Tale of Two Cities", "Charles Dickens", 1859, 39.95);
        
        bookstore.addBook(paperBook);
        bookstore.addBook(eBook);
        bookstore.addBook(showcaseBook);
        
        bookstore.displayInventory();
        System.out.println();
        
        // Test 2: Buying a paper book
        System.out.println("== Test 2: Buying Paper Book ==");
        try {
            double amount = bookstore.buyBook("978-0134685991", 2, "customer@email.com", "123 Main St, City, State");
            System.out.println("Quantum book store: Paid amount: $" + amount);
        } catch (BookstoreException e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }
        System.out.println();
        
        // Test 3: Buying an EBook
        System.out.println("== Test 3: Buying EBook ==");
        try {
            double amount = bookstore.buyBook("978-0135166307", 1, "customer@email.com", "123 Main St, City, State");
            System.out.println("Quantum book store: Paid amount: $" + amount);
        } catch (BookstoreException e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }
        System.out.println();
        
        // Test 4: Trying to buy showcase book (should fail)
        System.out.println("== Test 4: Trying to Buy Showcase Book ==");
        try {
            double amount = bookstore.buyBook("978-0596009205", 1, "customer@email.com", "123 Main St, City, State");
            System.out.println("Quantum book store: Paid amount: $" + amount);
        } catch (BookstoreException e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }
        System.out.println();
        
        // Test 5: Trying to buy more than available stock
        System.out.println("== Test 5: Insufficient Stock Test ==");
        try {
            double amount = bookstore.buyBook("978-0134685991", 15, "customer@email.com", "123 Main St, City, State");
            System.out.println("Quantum book store: Paid amount: $" + amount);
        } catch (BookstoreException e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }
        System.out.println();
        
        // Test 6: Trying to buy non-existent book
        System.out.println("== Test 6: Non-existent Book Test ==");
        try {
            double amount = bookstore.buyBook("978-INVALID", 1, "customer@email.com", "123 Main St, City, State");
            System.out.println("Quantum book store: Paid amount: $" + amount);
        } catch (BookstoreException e) {
            System.out.println("Quantum book store: Error - " + e.getMessage());
        }
        System.out.println();
        
        // Test 7: Removing outdated books
        System.out.println("== Test 7: Removing Outdated Books ==");
        List<Book> removedBooks = bookstore.removeOutdatedBooks(2025, 15);
        System.out.println("Quantum book store: Removed " + removedBooks.size() + " outdated books");
        bookstore.displayInventory();
        System.out.println();
        
        // Test 8: Adding new book types (demonstrating extensibility)
        System.out.println("== Test 8: Extensibility Test ==");
        // Example of how easy it would be to add a new book type
        System.out.println("Quantum book store: System is designed to easily accommodate new book types");
        System.out.println("Quantum book store: Simply extend the Book class and implement the required methods");
        
        System.out.println("\nQuantum book store: All tests completed!");
    }
}

// Example of how to extend the system with a new book type
// 3ashan a test the extensibility bta3t el system
class AudioBook extends Book {
    private String narrator;
    private int durationMinutes;
    
    public AudioBook(String isbn, String title, String author, int yearPublished, double price, String narrator, int durationMinutes) {
        super(isbn, title, author, yearPublished, price);
        this.narrator = narrator;
        this.durationMinutes = durationMinutes;
    }
    
    @Override
    public boolean isAvailable(int quantity) {
        return true; // Audio books are always available
    }
    
    @Override
    public void reduceInventory(int quantity) {
        // No inventory reduction needed for digital audio books
    }
    
    @Override
    public void processPurchase(int quantity, String email, String address) {
        // Could send download link via email
        System.out.println("Quantum book store: Audio book download link sent to " + email);
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", Narrator: %s, Duration: %d min", narrator, durationMinutes);
    }
}