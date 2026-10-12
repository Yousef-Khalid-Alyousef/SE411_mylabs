package edu.psu.se411.lab09_inventory.model;

import edu.psu.se411.lab09_inventory.App;
import edu.psu.se411.lab09_inventory.strategy.SearchByAuthor;
import edu.psu.se411.lab09_inventory.strategy.SearchByCategory;
import edu.psu.se411.lab09_inventory.strategy.SearchById;
import edu.psu.se411.lab09_inventory.strategy.SearchByName;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {
    private final Book book = new Book("B01", "Concurrency in Java", "Ali Ahmed");
    private final ElectronicDevice phone = new ElectronicDevice("E01", "iPhone", "Phone");

    private Inventory<Item> mixedInventory() {
        Inventory<Item> inventory = new Inventory<>();
        inventory.addItem(book);
        inventory.addItem(phone);
        return inventory;
    }

    @Test
    void addsAndRemovesDifferentItemTypes() {
        Inventory<Item> inventory = mixedInventory();
        assertEquals(List.of(book, phone), inventory.getAllItems());
        assertTrue(inventory.removeItem(book));
        assertFalse(inventory.removeItem(book));
        assertEquals(List.of(phone), inventory.getAllItems());
    }

    @Test
    void returnedListsDoNotExposeTheInventory() {
        Inventory<Item> inventory = mixedInventory();
        inventory.getAllItems().clear();
        inventory.findItems(item -> true).clear();
        assertEquals(List.of(book, phone), inventory.getAllItems());
    }

    @Test
    void searchesMixedItemsByIdAndName() {
        Inventory<Item> inventory = mixedInventory();
        assertEquals(List.of(phone), inventory.findItems(new SearchById("E01")));
        assertEquals(List.of(book), inventory.findItems(new SearchByName("Concurrency in Java")));
        assertEquals(List.of(phone), inventory.findItems(new SearchByName("IPHONE")));
        assertTrue(inventory.findItems(new SearchById("missing")).isEmpty());
    }

    @Test
    void returnsEveryMatchingItemInInsertionOrder() {
        Inventory<Item> inventory = mixedInventory();
        Book second = new Book("B02", "Concurrency in Java", "Sara Ahmed");
        inventory.addItem(second);
        assertEquals(List.of(book, second), inventory.findItems(new SearchByName("Concurrency in Java")));
    }

    @Test
    void bookInventorySupportsAuthorAndSharedItemStrategies() {
        Inventory<Book> books = new Inventory<>();
        books.addItem(book);
        books.addItem(new Book("B02", "Effective Java", "Joshua Bloch"));
        assertEquals(List.of(book), books.findItems(new SearchByAuthor("ali ahmed")));
        assertEquals(List.of(book), books.findItems(new SearchById("B01")));
        assertTrue(books.findItems(new SearchByAuthor("Unknown")).isEmpty());
    }

    @Test
    void deviceInventorySearchesByCategory() {
        Inventory<ElectronicDevice> devices = new Inventory<>();
        devices.addItem(phone);
        ElectronicDevice laptop = new ElectronicDevice("E02", "MacBook", "Laptop");
        devices.addItem(laptop);
        assertEquals(List.of(laptop), devices.findItems(new SearchByCategory("laptop")));
        assertTrue(devices.findItems(new SearchByCategory("Tablet")).isEmpty());
    }

    @Test
    void lambdaSearchWorksForProductsAndOtherTypes() {
        assertEquals(List.of(book), mixedInventory().findItems(item -> item instanceof Book));
        Inventory<Integer> numbers = new Inventory<>();
        numbers.addItem(1);
        numbers.addItem(2);
        numbers.addItem(4);
        assertEquals(List.of(2, 4), numbers.findItems(number -> number % 2 == 0));
    }

    @Test
    void displaysAnyInventoryUsingTheWildcard() {
        Inventory<Book> books = new Inventory<>();
        books.addItem(book);
        Inventory<String> names = new Inventory<>();
        names.addItem("Yousef");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.out;
        try (PrintStream capture = new PrintStream(output)) {
            System.setOut(capture);
            App.displayInventory(books);
            App.displayInventory(names);
        } finally {
            System.setOut(previous);
        }
        assertTrue(output.toString().contains(book.toString()));
        assertTrue(output.toString().contains("Yousef"));
    }
}
