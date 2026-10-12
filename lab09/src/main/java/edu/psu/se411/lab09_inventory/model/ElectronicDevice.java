package edu.psu.se411.lab09_inventory.model;

import java.util.Objects;

public class ElectronicDevice extends Item {
    private final String category;

    public ElectronicDevice(String id, String name, String category) {
        super(id, name);
        this.category = Objects.requireNonNull(category, "Category is required");
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Electronic device: " + getId() + ", " + getName() + ", category: " + category;
    }
}
