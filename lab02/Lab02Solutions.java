package lab02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


// Exercise 1: Introduction to Generics

class PrintableList<T> {
    private List<T> items;

    public PrintableList(T[] array) {
        this.items = new ArrayList<>(Arrays.asList(array));
    }

    public void printItems() {
        for (T item : items) {
            System.out.println(item);
        }
    }
}


// Exercise 2: Bounded Type Parameters

class NumberBox<T extends Number> {
    private T item;

    public NumberBox() {}

    public NumberBox(T item) {
        this.item = item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    // Method to calculate the sum of a list of Numbers
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number num : list) {
            sum += num.doubleValue();
        }
        return sum;
    }
}


// Exercise 3: Transformation Pipeline

@FunctionalInterface
interface Transformer<T, R> {
    R transform(T input);
}

class PipeLine<T, R> {
    private final Function<T, R> function;

    private PipeLine(Function<T, R> function) {
        this.function = function;
    }

    // Initialize pipeline starting with input type T
    public static <T> PipeLine<T, T> start() {
        return new PipeLine<>(input -> input);
    }

    // Add a transformer that returns a new pipeline with the updated output type
    public <V> PipeLine<T, V> addTransformer(Transformer<R, V> transformer) {
        return new PipeLine<>(this.function.andThen(transformer::transform));
    }

    // Execute the full sequence of transformations
    public R execute(T input) {
        return function.apply(input);
    }
}


// Exercise 4 & Main Execution

public class Lab02Solutions {

    // Exercise 4: Wildcard to print list of any type
    public static void printList(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    // Exercise 4: Wildcard to sum list of any Number subtype
    public static double sumNumbers(List<? extends Number> list) {
        double sum = 0.0;
        for (Number num : list) {
            sum += num.doubleValue();
        }
        return sum;
    }

    public static void main(String[] args) {
        // --- Exercise 1 Test ---
        System.out.println("--- Exercise 1 ---");
        String[] stringArray = {"Java", "Generics", "SE411", "Lab02"};
        PrintableList<String> printableList = new PrintableList<>(stringArray);
        printableList.printItems();

        // --- Exercise 2 Test ---
        System.out.println("\n--- Exercise 2 ---");
        NumberBox<Integer> intBox = new NumberBox<>(42);
        NumberBox<Double> doubleBox = new NumberBox<>();
        doubleBox.setItem(3.14159);

        System.out.println("Integer Box: " + intBox.getItem());
        System.out.println("Double Box: " + doubleBox.getItem());

        List<Integer> intList = Arrays.asList(10, 20, 30);
        System.out.println("NumberBox Sum: " + NumberBox.sumOfList(intList));

        // --- Exercise 3 Test ---
        System.out.println("\n--- Exercise 3 ---");
        PipeLine<String, Integer> pipeline = PipeLine.<String>start()
                .addTransformer(s -> s.trim())
                .addTransformer(s -> s.toUpperCase())
                .addTransformer(s -> s.length());

        int result = pipeline.execute("   generics pipeline   ");
        System.out.println("Pipeline output (length of trimmed uppercase): " + result);

        // --- Exercise 4 Test ---
        System.out.println("\n--- Exercise 4 ---");
        List<Double> doubleList = Arrays.asList(1.5, 2.5, 3.0);
        List<String> words = Arrays.asList("Alpha", "Beta", "Gamma");

        System.out.print("printList (words): ");
        printList(words);

        System.out.print("printList (doubles): ");
        printList(doubleList);

        System.out.println("sumNumbers (integers): " + sumNumbers(intList));
        System.out.println("sumNumbers (doubles): " + sumNumbers(doubleList));
    }
}