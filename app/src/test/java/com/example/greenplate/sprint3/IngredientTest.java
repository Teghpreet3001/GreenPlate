package com.example.greenplate.sprint3;
import static org.junit.Assert.assertEquals;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;

import org.junit.Test;
import java.util.Arrays;

public class IngredientTest {
    private static final double DELTA = 0.0001; // A small number

    @Test
    public void testIngredientQuantityNonZero() {
        Ingredient testIng = new Ingredient("Orange", 2, 50, "10/20/2025");
        testIng.setQuantity(0);
        assertEquals("Quantity will not change", 2, testIng.getQuantity(), DELTA);
    }

    @Test
    public void testIngredientQuantityNonNegative() {
        Ingredient testIng = new Ingredient("Flour", 2, 500, "10/20/2025");
        testIng.setQuantity(-10);
        assertEquals("Quantity will not change", 2, testIng.getQuantity(), DELTA);
    }

}
