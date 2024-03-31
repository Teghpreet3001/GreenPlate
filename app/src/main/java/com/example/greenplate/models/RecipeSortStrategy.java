package com.example.greenplate.models;

import java.util.List;

public interface RecipeSortStrategy {
    List<Recipe> sortRecipes(List<Recipe> recipes);
}
