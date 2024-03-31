package com.example.greenplate.sprint3;
import static org.junit.Assert.assertEquals;

import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.RecipeViewModel;

import org.junit.Test;
import java.util.Arrays;

public class RecipeTest {
    // Rachit's Tests
    @Test
    public void testRecipeQuantityNonZero() {
        Recipe testRecipe = new Recipe("Pasta", Arrays.asList("Ginger, Garlic, Tomatoes"),
                "1", null);
        testRecipe.setQuantity("0");
        assertEquals("Quantity will not change","1",testRecipe.getQuantity());
    }

    @Test
    public void testRecipeQuantityNonNegative() {
        Recipe testRecipe = new Recipe("Pizza", Arrays.asList("Steak, Chicken, Pork"),
                "10", null);
        testRecipe.setQuantity("-4");
        assertEquals("Quantity will not change","10",testRecipe.getQuantity());
    }

    // Unnathi's Tests
    @Test
    public void testEmptyIngredientList() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        String isInputValid =
                recipeViewModel.handleRecipeInputData("", "1", "oats",
                        "1")[0];
        assertEquals("Input should not be valid","false",isInputValid);
    }
    @Test
    public void testEmptyQuantityField() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        String isInputValid =
                recipeViewModel
                        .handleRecipeInputData("oats, milk", "", "oats",
                                "1")[0];
        assertEquals("Input should not be valid","false",isInputValid);
    }
    @Test
    public void testEmptyTitleList() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        String isInputValid =
                recipeViewModel.handleRecipeInputData("oat, milk", "1", "")[0];
        assertEquals("Input should not be valid","false",isInputValid);
    }
}
