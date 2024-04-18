package com.example.greenplate.sprint4;

import static org.junit.Assert.assertEquals;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.IngredientViewModel;

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

    // Unnathi's Tests
    @Test
    public void testShoppingListItemCaloriesNonZero() {
        Ingredient testIng = new Ingredient("Dhaniya", 2, 100,
                "");
        testIng.setCaloriesPerServing(0);
        assertEquals("Calories per serving will not change", 100,
                testIng.getCaloriesPerServing(), DELTA);
    }
    @Test
    public void testShoppingListItemCaloriesNonNegative() {
        Ingredient testIng = new Ingredient("Dhaniya", 2, 100, "");
        testIng.setCaloriesPerServing(-10);
        assertEquals("Calories per serving will not change", 100,
                testIng.getCaloriesPerServing(), DELTA);
    }

}
