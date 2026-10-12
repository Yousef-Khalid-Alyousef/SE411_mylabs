package edu.psu.se411.lab09_inventory.strategy;

import edu.psu.se411.lab09_inventory.model.Item;
import java.util.Objects;

public class SearchByName implements SearchStrategy<Item> {
    private final String name;

    public SearchByName(String name) {
        this.name = Objects.requireNonNull(name, "Search name is required");
    }

    @Override
    public boolean matches(Item item) {
        return item.getName().equalsIgnoreCase(name);
    }
}
