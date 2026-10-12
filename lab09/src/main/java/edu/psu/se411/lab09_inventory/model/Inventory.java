package edu.psu.se411.lab09_inventory.model;

import edu.psu.se411.lab09_inventory.strategy.SearchStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventory<T> {
    private final List<T> items = new ArrayList<>();

    public void addItem(T item) {
        items.add(Objects.requireNonNull(item, "Item is required"));
    }

    public boolean removeItem(T item) {
        return items.remove(item);
    }

    public List<T> getAllItems() {
        return new ArrayList<>(items);
    }

    public List<T> findItems(SearchStrategy<? super T> strategy) {
        Objects.requireNonNull(strategy, "Search strategy is required");
        List<T> results = new ArrayList<>();
        for (T item : items) {
            if (strategy.matches(item)) {
                results.add(item);
            }
        }
        return results;
    }
}
