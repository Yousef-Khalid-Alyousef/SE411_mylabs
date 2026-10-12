package edu.psu.se411.lab09_inventory.strategy;

import edu.psu.se411.lab09_inventory.model.Book;
import java.util.Objects;

public class SearchByAuthor implements SearchStrategy<Book> {
    private final String author;

    public SearchByAuthor(String author) {
        this.author = Objects.requireNonNull(author, "Search author is required");
    }

    @Override
    public boolean matches(Book book) {
        return book.getAuthor().equalsIgnoreCase(author);
    }
}
