package edu.psu.se411.lab09_inventory.strategy;

import edu.psu.se411.lab09_inventory.model.ElectronicDevice;
import java.util.Objects;

public class SearchByCategory implements SearchStrategy<ElectronicDevice> {
    private final String category;

    public SearchByCategory(String category) {
        this.category = Objects.requireNonNull(category, "Search category is required");
    }

    @Override
    public boolean matches(ElectronicDevice device) {
        return device.getCategory().equalsIgnoreCase(category);
    }
}
