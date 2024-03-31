package com.example.greenplate.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByQuantityStrategy implements RecipeSortStrategy {
    @Override
    public List<Recipe> sortRecipes(List<Recipe> recipes) {
        Collections.sort(recipes, Comparator.comparingInt(r -> Integer.parseInt(r.getQuantity())));
        Collections.reverse(recipes);
        return recipes;
    }
}
