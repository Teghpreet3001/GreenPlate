package com.example.greenplate.models;

import java.util.List;
import java.util.Map;

public class CalorieCountDecorator implements RecipeDecorator {
    private RecipeComponent decoratedRecipe;

    public CalorieCountDecorator(RecipeComponent decoratedRecipe) {
        this.decoratedRecipe = decoratedRecipe;
    }

    @Override
    public String getTitle() {
        return decoratedRecipe.getTitle();
    }

    @Override
    public List<String> getIngredients() {
        return decoratedRecipe.getIngredients();
    }

    @Override
    public String getQuantity() {
        return decoratedRecipe.getQuantity();
    }

    @Override
    public Map<String, Integer> getIngredientQuantities() {
        return decoratedRecipe.getIngredientQuantities();
    }

    @Override
    public int getTotalCalories() {
        int totalCalories = 0;
        for (Map.Entry<String, Integer> entry
                : decoratedRecipe.getIngredientQuantities().entrySet()) {
            String ingredientName = entry.getKey();
            int quantity = entry.getValue();
            Ingredient ingredient = Ingredient.getIngredientByName(ingredientName);
            if (ingredient != null) {
                totalCalories += ingredient.getCaloriesPerServing() * quantity;
            }
        }
        return totalCalories;
    }
}
