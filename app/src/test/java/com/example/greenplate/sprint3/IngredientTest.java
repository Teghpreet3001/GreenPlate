package com.example.greenplate.sprint3;
import static org.junit.Assert.assertEquals;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.RecipeViewModel;

import org.junit.Test;
import java.util.Arrays;

public class IngredientTest {
    private static final double DELTA = 0.0001; // A small number

    // Shreyashi Tests
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
    @Test
    public void testEmptyNameField() {
        String isInputValid = IngredientViewModel
                .handleIngredientInputData("", 5,10,"10/11/2024")[0];
        assertEquals("Input shouldn't be valid","false",isInputValid);
    }
    // Teghpreet Tests
    @Test
    public void testCaloriesQuantityNonZero() {
        Ingredient testIng = new Ingredient("Cheese", 5, 500, "10/20/2025");
        testIng.setCaloriesPerServing(0);
        assertEquals("Calories will not change", 500, testIng.getCaloriesPerServing(), DELTA);
    }
    @Test
    public void testIngredientCaloriesNonNegative() {
        Ingredient testIng = new Ingredient("Bell Peppers", 10, 100, "10/20/2025");
        testIng.setCaloriesPerServing(-200);
        assertEquals("Calories will not change", 100, testIng.getCaloriesPerServing(), DELTA);
    }
}
