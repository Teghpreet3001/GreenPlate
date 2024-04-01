package com.example.greenplate.viewmodels;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.Toast;

import com.example.greenplate.models.RecipeSortStrategy;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.views.RecipeDetailActivity;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Behaves like an adapter
public class RecipeViewModel extends RecyclerView.Adapter<RecipeViewModel.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private DatabaseReference mDatabase;
    private RecipeSortStrategy sortStrategy;
    private Map<String, Integer> userPantry;

    public RecipeViewModel(List<Recipe> recipeList, Map<String, Integer> userPantry) {
        this.recipeList = recipeList;
        this.userPantry = userPantry;
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
    }
    private static boolean hasAllIngredients(Recipe recipe, Map<String, Integer> userPantry) {
        Map<String, Integer> requiredIngredients = recipe.getIngredientQuantities();
        if (requiredIngredients == null || userPantry == null) {
            // Log.d("RecipeViewModel", "One or more maps are null.");
            return false;
        }
        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();
            Integer pantryQuantity = userPantry.get(ingredient);
            // Log.d("RecipeViewModel", "Checking ingredient: " + ingredient + ", required: " + requiredQuantity + ", in pantry: " + pantryQuantity);

            if (pantryQuantity == null || pantryQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }


    public RecipeViewModel() {
        // for testing
    }
    public RecipeViewModel(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
    }

    public RecipeViewModel(List<Recipe> recipeList, RecipeSortStrategy sortStrategy) {
        this.recipeList = recipeList;
        this.sortStrategy = sortStrategy;
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
    }

    public void setSortingStrategy(RecipeSortStrategy sortingStrategy) {
        this.sortStrategy = sortingStrategy;
    }

    public void applySortStrategy() {
        if (sortStrategy != null) {
            recipeList = sortStrategy.sortRecipes(recipeList);
            notifyDataSetChanged();
        } else {
            Log.e("RecipeViewModel", "Sorting strategy not set");
        }
    }

    public String[] handleRecipeInputData(String ingredients, String quantity,
                                         String title, String ingredientQuantities) {
        if (ingredients == null) {
            return new String[]{"false", "Ingredients are null"};
        } else if (quantity == null) {
            return new String[]{"false", "Quantity is null"};
        } else if (title == null) {
            return new String[]{"false", "Recipe title is null"};
        }

        if (quantity.trim().isEmpty()) {
            return new String[]{"false", "Quantity field is empty"};
        } else if (ingredients.trim().isEmpty()) {
            return new String[]{"false", "Ingredients field is empty"};
        } else if (title.trim().isEmpty()) {
            return new String[]{"false", "Recipe title field is empty"};
        }

        int quantityNum = -1;

        try {
            quantityNum = Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            return new String[]{"false", "Quantity must be an integer"};
        }

        if (quantityNum <= 0) {
            return new String[]{"false", "Quantity cannot be negative"};
        }

        if (ingredientQuantities.split(",").length != ingredients.split(",").length) {
            return new String[]{"false", "Number of quantities and ingredients in"
                    + " comma-separated list must be equal"};
        }

        return new String[]{"true", ""};
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe, userPantry);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);

                intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                intent.putExtra("RECIPE_QUANTITY", recipe.getQuantity());
                intent.putStringArrayListExtra("RECIPE_INGREDIENTS",
                        new ArrayList<>(recipe.getIngredients()));

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeTitleTextView;
        private TextView recipeIngredientsTextView;
        private TextView recipeQuantityTextView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitleTextView = itemView.findViewById(R.id.recipeTitleTV);
            recipeIngredientsTextView = itemView.findViewById(R.id.recipeIngredientsTV);
            recipeQuantityTextView = itemView.findViewById(R.id.recipeQuantityTV);
        }

        void bind(Recipe recipe, Map<String, Integer> userPantry) {
            recipeTitleTextView.setText(recipe.getTitle());
            StringBuilder ingredientsBuilder = new StringBuilder();
            Map<String, Integer> ingredientQuantities = recipe.getIngredientQuantities();
            if (ingredientQuantities != null) {
                for (Map.Entry<String, Integer> entry : ingredientQuantities.entrySet()) {
                    if (ingredientsBuilder.length() > 0) {
                        ingredientsBuilder.append(", "); // csv se separate kardo
                    }
                    String ingredient = entry.getKey();
                    Integer quantity = entry.getValue();
                    ingredientsBuilder.append(ingredient).append(" (").append(quantity).append(")");
                }
            }

            if (ingredientsBuilder.length() == 0) {
                ingredientsBuilder.append("No ingredients"); // if nothing found, then show accordingly
            }

            recipeIngredientsTextView.setText("Ingredients: " + ingredientsBuilder.toString());
            recipeQuantityTextView.setText("Quantity: " + recipe.getQuantity());
            recipeQuantityTextView.setText("Quantity: " + recipe.getQuantity());


            // if enough qty for ingredients in pantry -> green, else red
            if (hasAllIngredients(recipe, userPantry)) {
                itemView.setBackgroundColor(Color.parseColor("#aff5a9")); // pantry has ingredients
                itemView.setEnabled(true);
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f5b8a9")); // pantry does not have enough ingredients
                itemView.setEnabled(false);
            }

            itemView.setOnClickListener(v -> {
                if (itemView.isEnabled()) {
                    Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);

                    intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                    intent.putExtra("RECIPE_QUANTITY", recipe.getQuantity());
                    intent.putStringArrayListExtra("RECIPE_INGREDIENTS",
                            new ArrayList<>(recipe.getIngredients()));

                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "Not enough ingredients to make this recipe", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
