package com.example.greenplate.models;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByTitleStrategy implements RecipeSortStrategy {
    @Override
    public List<Recipe> sortRecipes(List<Recipe> recipes) {
        Collections.sort(recipes, Comparator.comparing(Recipe::getTitle));
        return recipes;
    }
}