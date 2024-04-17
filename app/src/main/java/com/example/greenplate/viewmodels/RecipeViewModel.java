package com.example.greenplate.viewmodels;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.Toast;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.RecipeSortStrategy;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.views.RecipeDetailActivity;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Behaves like an adapter
public class RecipeViewModel extends RecyclerView.Adapter<RecipeViewModel.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private static DatabaseReference mDatabase;
    private RecipeSortStrategy sortStrategy;
    private Map<String, Integer> userPantry;
    private Map<String, Integer> shoppingList;
    static final String USER_ID = SingletonFirebase.getInstance()
            .getFirebaseAuth().getCurrentUser().getUid();
    private static IngredientViewModel ingredientViewModel = new IngredientViewModel();

    public RecipeViewModel(List<Recipe> recipeList, Map<String, Integer> userPantry,
                           Map<String, Integer> shoppingList) {
        this.recipeList = recipeList;
        this.userPantry = userPantry;
        this.shoppingList = shoppingList;
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
    }
    public static boolean hasAllIngredients(Recipe recipe, Map<String, Integer> userPantry) {
        Map<String, Integer> requiredIngredients = recipe.getIngredientQuantities();
        Log.d("Yahoo", recipe.getIngredientQuantities().toString());
        Log.d("YahooRecipe", recipe.getTitle());
        if (requiredIngredients == null || userPantry == null) {
            // Log.d("RecipeViewModel", "One or more maps are null.");
            return false;
        }
        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();
            Integer pantryQuantity = userPantry.get(ingredient);

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

    private static Map<String, Integer> calculateMissingIngredients(Recipe recipe, Map<String,
            Integer> userPantry) {
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> requiredIngredients = recipe.getIngredientQuantities();

        if (requiredIngredients == null || userPantry == null) {
            return null;
        }

        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingredient = entry.getKey();
            int requiredQuantity = entry.getValue();
            Integer pantryQuantity = userPantry.get(ingredient);

            if (pantryQuantity == null) {
                pantryQuantity = 0;
            }

            if (pantryQuantity < requiredQuantity) {
                map.put(ingredient, requiredQuantity - pantryQuantity);
            }
        }

        return map;
    }

    private static void addMissingIngredientsToShoppingList(Map<String,
            Integer> missingIngredients, Context context) {
        if (missingIngredients == null) {
            return;
        }

        for (Map.Entry<String, Integer> entry : missingIngredients.entrySet()) {
            // References: https://stackoverflow.com/questions/10903754/input-text-dialog-android
            // https://developer.android.com/develop/ui/views/components/dialogs#PassingEvents

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("How many calories per serving is " + entry.getKey() + "?");
            EditText caloriesInput = new EditText(context);
            caloriesInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(caloriesInput);

            builder.setPositiveButton("Save", (dialog, which) ->
                    ingredientViewModel.addIngredientToShoppingList(USER_ID,
                            new Ingredient(entry.getKey(), entry.getValue(),
                            Integer.parseInt(caloriesInput.getText().toString()),
                            null)));

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }

        Toast.makeText(context, "Missing ingredients added to shopping list",
                Toast.LENGTH_SHORT).show();
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
                intent.putExtra("RECIPE_OBJECT", recipe);

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
        private Button missingIngredientsBtn;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitleTextView = itemView.findViewById(R.id.recipeTitleTV);
            recipeIngredientsTextView = itemView.findViewById(R.id.recipeIngredientsTV);
            recipeQuantityTextView = itemView.findViewById(R.id.recipeQuantityTV);
            missingIngredientsBtn = itemView.findViewById(R.id.missingIngredientsBtn);
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
                ingredientsBuilder.append("No ingredients");
            }

            recipeIngredientsTextView.setText("Ingredients: " + ingredientsBuilder.toString());
            recipeQuantityTextView.setText("Quantity: " + recipe.getQuantity());

            // if enough qty for ingredients in pantry -> green, else red
            if (hasAllIngredients(recipe, userPantry)) {
                itemView.setBackgroundColor(Color.parseColor("#aff5a9")); // pantry has ingredients
                itemView.setEnabled(true);
                missingIngredientsBtn.setVisibility(View.GONE);
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f5b8a9"));
                itemView.setEnabled(false);
            }

            itemView.setOnClickListener(v -> {
                if (itemView.isEnabled()) {
                    Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);

                    intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                    intent.putExtra("RECIPE_QUANTITY", recipe.getQuantity());
                    intent.putExtra("RECIPE_OBJECT", recipe);
                    intent.putStringArrayListExtra("RECIPE_INGREDIENTS",
                            new ArrayList<>(recipe.getIngredients()));

                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(),
                            "Not enough ingredients to make this recipe",
                            Toast.LENGTH_SHORT).show();
                }
            });

            missingIngredientsBtn.setOnClickListener(v -> {
                Map<String, Integer> missingIngredients =
                        calculateMissingIngredients(recipe, userPantry);
                if (missingIngredients == null) {
                    Toast.makeText(v.getContext(),
                            "An error occurred",
                            Toast.LENGTH_SHORT).show();
                } else {
                    addMissingIngredientsToShoppingList(missingIngredients, v.getContext());
                }
            });
        }
    }
}
