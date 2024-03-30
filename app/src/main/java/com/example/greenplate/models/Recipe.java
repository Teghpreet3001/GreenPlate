package com.example.greenplate.models;


import java.util.List;

public class Recipe {
    private String title;
    private String quantity;
    private List<String> ingredients;

    // PLEASE DON'T DELETE THIS. Firebase uses this in a weird way.
    public Recipe() {
        //
    }
    public Recipe(String title, List<String> ingredients, String quantity) {
        this.title = title;
        this.ingredients = ingredients;
        this.quantity = quantity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQuantity(String quantity) {
        if (Integer.parseInt(quantity) > 0) {
            this.quantity = quantity;
        }

    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getQuantity() {
        return quantity;
    }
}
