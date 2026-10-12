package edu.psu.se411.lab09_inventory.strategy;

import edu.psu.se411.lab09_inventory.model.Item;
import java.util.Objects;

public class SearchById implements SearchStrategy<Item> {
    private final String id;

    public SearchById(String id) {
        this.id = Objects.requireNonNull(id, "Search id is required");
    }

    @Override
    public boolean matches(Item item) {
        return item.getId().equals(id);
    }
}
