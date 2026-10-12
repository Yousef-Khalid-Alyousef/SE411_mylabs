package edu.psu.se411.lab09_inventory.model;

import java.util.Objects;

public class Book extends Item {
    private final String author;

    public Book(String id, String name, String author) {
        super(id, name);
        this.author = Objects.requireNonNull(author, "Author is required");
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Book: " + getId() + ", " + getName() + ", author: " + author;
    }
}
