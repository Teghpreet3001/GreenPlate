package com.example.greenplate.sprint4;

import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;

import com.example.greenplate.models.CalorieCountDecorator;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RecipeComponent;

public class CalorieUnitTests {

    // Teghpreet Unit Tests
    @Test
    public void testGetTotalCalories() {
        List<String> ingredients = new ArrayList<>();
        Ingredient banana = new Ingredient("Banana", 3, 20, null); // 3 bananas with 20 calories each
        Ingredient apple = new Ingredient("Apple", 2, 30, null); // 2 apples with 30 calories each
        ingredients.add(banana.getName());
        ingredients.add(apple.getName());

        RecipeComponent mockRecipe = new Recipe("Mock Recipe", ingredients, "5",
                createIngredientQuantities(banana, apple));
        CalorieCountDecorator decorator = new CalorieCountDecorator(mockRecipe);
        int expectedTotalCalories = 3 * banana.getCaloriesPerServing() + 2 * apple.getCaloriesPerServing();
        int actualTotalCalories = decorator.getTotalCalories();
        assertEquals("Total Calories should be equal",expectedTotalCalories, actualTotalCalories);
    }

    @Test
    public void testGetTotalCaloriesWithBothIngredientsNull() {
        List<String> ingredients = new ArrayList<>();
        Ingredient banana = null;
        Ingredient apple = null;

        RecipeComponent mockRecipe = new Recipe("Mock Recipe", ingredients, "5",
                createIngredientQuantities(banana, apple));
        CalorieCountDecorator decorator = new CalorieCountDecorator(mockRecipe);
        int expectedTotalCalories = 0; // Both ingredients are null, so total calories should be 0
        int actualTotalCalories = decorator.getTotalCalories();
        assertEquals("Total Calories should be 0 when both ingredients are null", expectedTotalCalories, actualTotalCalories);
    }

    @Test
    public void testGetTotalCaloriesWithOneIngredientNull() {
        List<String> ingredients = new ArrayList<>();
        Ingredient banana = new Ingredient("Banana", 3, 20, null); // 3 bananas with 20 calories each
        Ingredient apple = null;
        ingredients.add(banana.getName());

        RecipeComponent mockRecipe = new Recipe("Mock Recipe", ingredients, "5",
                createIngredientQuantities(banana, apple));
        CalorieCountDecorator decorator = new CalorieCountDecorator(mockRecipe);
        int expectedTotalCalories = 3 * banana.getCaloriesPerServing(); // Apple is null, so total calories should be for bananas only
        int actualTotalCalories = decorator.getTotalCalories();
        assertEquals("Total Calories should be for bananas only when one ingredient is null", expectedTotalCalories, actualTotalCalories);
    }
    private Map<String, Integer> createIngredientQuantities(Ingredient... ingredients) {
        Map<String, Integer> ingredientQuantities = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient != null) {
                ingredientQuantities.put(ingredient.getName(), ingredient.getQuantity());
            }
        }
        return ingredientQuantities;
    }

    @Test
    public void testGetTotalCaloriesWithZeroQuantity() {
        List<String> ingredients = new ArrayList<>();
        Ingredient banana = new Ingredient("Banana", 3, 20, null); // 3 bananas with 20 calories each
        Ingredient apple = new Ingredient("Apple", 0, 30, null); // 0 apples, should not add to total calories

        ingredients.add(banana.getName());
        ingredients.add(apple.getName());

        RecipeComponent mockRecipe = new Recipe("Mock Recipe", ingredients, "5",
                createIngredientQuantities(banana, apple));
        CalorieCountDecorator decorator = new CalorieCountDecorator(mockRecipe);
        int expectedTotalCalories = 3 * banana.getCaloriesPerServing(); // Only banana calories
        int actualTotalCalories = decorator.getTotalCalories();
        assertEquals("Total Calories should only include bananas", expectedTotalCalories, actualTotalCalories);
    }

    @Test
    public void testGetTotalCaloriesWithDuplicateIngredients() {
        List<String> ingredients = new ArrayList<>();
        Ingredient banana = new Ingredient("Banana", 1, 20, null); // A single banana with 20 calories each

        ingredients.add(banana.getName());
        ingredients.add(banana.getName());

//        System.out.println(ingredients);

        banana.setQuantity(banana.getQuantity() * 2);

        RecipeComponent mockRecipe = new Recipe("Mock Recipe", ingredients, "5",
                createIngredientQuantities(banana));
        CalorieCountDecorator decorator = new CalorieCountDecorator(mockRecipe);
        int expectedTotalCalories = 2 * 20;
        int actualTotalCalories = decorator.getTotalCalories();
        assertEquals("Total Calories should sum quantities of duplicate ingredients", expectedTotalCalories, actualTotalCalories);
    }
}
