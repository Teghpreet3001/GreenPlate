package com.example.greenplate.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Ingredient implements Serializable {

    private static final Map<String, Ingredient> INGREDIENT_MAP = new HashMap<>();

    private String name;
    private int quantity;
    private int caloriesPerServing;
    private String expirationDate; // Optional, can be null if not provided

    public Ingredient() {

    }

    public Ingredient(String name, int quantity, int caloriesPerServing, String expirationDate) {
        this.name = name;
        this.quantity = quantity;
        this.caloriesPerServing = caloriesPerServing;
        this.expirationDate = expirationDate;
        // Add the ingredient to the map when instantiated
        INGREDIENT_MAP.put(name, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    public int getCaloriesPerServing() {
        return caloriesPerServing;
    }

    public void setCaloriesPerServing(int caloriesPerServing) {
        if (caloriesPerServing > 0) {
            this.caloriesPerServing = caloriesPerServing;
        }
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    // Static method to retrieve an ingredient by name
    public static Ingredient getIngredientByName(String name) {
        return INGREDIENT_MAP.get(name);
    }
}
