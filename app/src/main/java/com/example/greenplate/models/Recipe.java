package com.example.greenplate.models;
import java.util.List;
import java.util.Map;

public class Recipe {
    private String title;
    private String quantity;
    private List<String> ingredients;
    private Map<String, Integer> ingredientQuantities;

    // PLEASE DON'T DELETE THIS. Firebase uses this in a weird way.
    public Recipe() {
        //
    }
    public Recipe(String title, List<String> ingredients,
                  String quantity, Map<String, Integer> ingredientQuantities) {
        this.title = title;
        this.ingredients = ingredients;
        this.quantity = quantity;
        this.ingredientQuantities = ingredientQuantities;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        if (Integer.parseInt(quantity) > 0) {
            this.quantity = quantity;
        }
    }

    public Map<String, Integer> getIngredientQuantities() {
        return ingredientQuantities;
    }

    public void setIngredientQuantities(Map<String, Integer> ingredientQuantities) {
        this.ingredientQuantities = ingredientQuantities;
    }
}
