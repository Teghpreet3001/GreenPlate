package com.example.greenplate.sprint4;

import static org.junit.Assert.assertEquals;

import com.example.greenplate.models.Ingredient;

import org.junit.Test;

public class ShoppingListTest {
    // Rachit's Tests
    private static final double DELTA = 0.0001; // A small number

    @Test
    public void testShoppingListItemQuantityNonZero() {
        Ingredient testIng = new Ingredient("Dhaniya", 2, 100, "");
        testIng.setQuantity(0);
        assertEquals("Quantity will not change", 2, testIng.getQuantity(), DELTA);
    }
    @Test
    public void testShoppingListItemQuantityNonNegative() {
        Ingredient testIng = new Ingredient("Dhaniya", 2, 100, "");
        testIng.setQuantity(-10);
        assertEquals("Quantity will not change", 2, testIng.getQuantity(), DELTA);
    }
}
