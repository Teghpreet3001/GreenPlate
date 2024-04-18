package com.example.greenplate.models;

import java.util.List;
import java.util.Map;

public interface RecipeComponent {
    String getTitle();
    List<String> getIngredients();
    String getQuantity();
    Map<String, Integer> getIngredientQuantities();
}
