package edu.psu.se411.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

public class StackTest {

    @Test
    public void testPushAndPop() {
        Stack<String> stringStack = new Stack<>();
        stringStack.push("Z");
        stringStack.push("A");
        assertEquals("A", stringStack.pop());
    }

    @Test
    public void pop_empty_stack() {
        Stack<String> stringStack = new Stack<>();
        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> stringStack.pop(),
                "Expected pop from empty Stack to throw, but it didn't"
        );
        assertEquals("Stack is empty, cannot pop", thrown.getMessage());
    }

    @Test
    public void testPushAndPopReverseOrder() {
        Stack<String> stringStack = new Stack<>();
        stringStack.push("First");
        stringStack.push("Second");
        stringStack.push("Third");

        assertEquals("Third", stringStack.pop());
        assertEquals("Second", stringStack.pop());
        assertEquals("First", stringStack.pop());
    }


    @Test
    public void testPushNullValue() {
        Stack<String> stack = new Stack<>();
        stack.push(null);
        assertNull(stack.pop());
    }

    @Test
    public void testCustomCapacityValid() {
        Stack<Integer> stack = new Stack<>(5);
        stack.push(42);
        assertEquals(42, stack.pop());
    }

    @Test
    public void testCustomCapacityZeroOrNegativeFallback() {
        // Tests the capacity > 0 ? capacity : 10 branch
        Stack<Integer> zeroCapStack = new Stack<>(0);
        zeroCapStack.push(1);
        assertEquals(1, zeroCapStack.pop());

        Stack<Integer> negCapStack = new Stack<>(-5);
        negCapStack.push(2);
        assertEquals(2, negCapStack.pop());
    }

    @Test
    public void testStateAfterMultiplePushesAndPops() {
        Stack<Integer> stack = new Stack<>();
        stack.push(10);
        stack.push(20);
        assertEquals(20, stack.pop());

        stack.push(30);
        assertEquals(30, stack.pop());
        assertEquals(10, stack.pop());

        // Now empty, should throw exception
        assertThrows(NoSuchElementException.class, stack::pop);
    }

}