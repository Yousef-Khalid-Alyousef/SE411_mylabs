package edu.psu.se411.lab09_inventory;

import edu.psu.se411.lab09_inventory.model.Book;
import edu.psu.se411.lab09_inventory.model.ElectronicDevice;
import edu.psu.se411.lab09_inventory.model.Inventory;
import edu.psu.se411.lab09_inventory.model.Item;
import edu.psu.se411.lab09_inventory.strategy.SearchByAuthor;
import edu.psu.se411.lab09_inventory.strategy.SearchByCategory;
import edu.psu.se411.lab09_inventory.strategy.SearchById;
import edu.psu.se411.lab09_inventory.strategy.SearchByName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Application is starting...");
        try {
            Book book1 = new Book("B01", "Concurrency in Java", "Ali Ahmed");
            Book book2 = new Book("B02", "Effective Java", "Joshua Bloch");
            ElectronicDevice phone = new ElectronicDevice("E01", "iPhone", "Phone");
            ElectronicDevice laptop = new ElectronicDevice("E02", "MacBook", "Laptop");

            Inventory<Item> inventory = new Inventory<>();
            inventory.addItem(book1);
            inventory.addItem(book2);
            inventory.addItem(phone);
            inventory.addItem(laptop);

            System.out.println("All items:");
            displayInventory(inventory);

            System.out.println("\nSearch for a book by name:");
            System.out.println(inventory.findItems(new SearchByName("Concurrency in Java")));
            System.out.println("Search for a device by name:");
            System.out.println(inventory.findItems(new SearchByName("iPhone")));
            System.out.println("Search by id:");
            System.out.println(inventory.findItems(new SearchById("E02")));

            Inventory<Book> books = new Inventory<>();
            books.addItem(book1);
            books.addItem(book2);
            System.out.println("\nSearch books by author:");
            System.out.println(books.findItems(new SearchByAuthor("Ali Ahmed")));

            Inventory<ElectronicDevice> devices = new Inventory<>();
            devices.addItem(phone);
            devices.addItem(laptop);
            System.out.println("Search devices by category:");
            System.out.println(devices.findItems(new SearchByCategory("Laptop")));

            System.out.println("\nLambda search for names containing Java:");
            System.out.println(inventory.findItems(item -> item.getName().contains("Java")));

            inventory.removeItem(phone);
            System.out.println("\nInventory after removing the phone:");
            displayInventory(inventory);
            System.out.println("\nBook inventory:");
            displayInventory(books);
            System.out.println("\nDevice inventory:");
            displayInventory(devices);
        } catch (RuntimeException e) {
            logger.error("Inventory operation failed", e);
            throw e;
        } finally {
            logger.info("Application is closing...");
        }
    }

    public static void displayInventory(Inventory<?> inventory) {
        for (Object item : inventory.getAllItems()) {
            System.out.println(item);
        }
    }
}
