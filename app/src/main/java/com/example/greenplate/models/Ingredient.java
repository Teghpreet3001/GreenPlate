package com.example.greenplate.models;

import java.io.Serial;
import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;
    private int quantity;
    private int caloriesPerServing;
    private String expirationDate; // Optional, can be null if not provided
    public Ingredient() {
        //
    }
    public Ingredient(String name, int quantity, int caloriesPerServing, String expirationDate) {
        this.name = name;
        this.quantity = quantity;
        this.caloriesPerServing = caloriesPerServing;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
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
}
