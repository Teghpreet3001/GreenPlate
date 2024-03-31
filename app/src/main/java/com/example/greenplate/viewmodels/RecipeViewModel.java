package com.example.greenplate.viewmodels;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.views.RecipeDetailActivity;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Behaves like an adapter
public class RecipeViewModel extends RecyclerView.Adapter<RecipeViewModel.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private DatabaseReference mDatabase;
    public RecipeViewModel() {
        // for testing
    }
    public RecipeViewModel(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
    }

    public String[] handleRecipeInputData(String ingredients, String quantity,
                                         String title) {
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

        return new String[]{"true", ""};
    }

    public void storeRecipe(String ingredients, String quantity,
                            String title) {
        Recipe recipe = new Recipe(title, Arrays.asList(ingredients.split(",")), quantity);

        mDatabase.child("numRecipes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("GreenPlate", "Recipe Key: " + task.getResult().getValue());

                long recipeKey = (long) task.getResult().getValue();
                recipeKey += 1;

                DatabaseReference cookbookRef =
                        mDatabase.child("cookbook").child(String.valueOf(recipeKey));

                long finalRecipeKey = recipeKey;

                cookbookRef.setValue(recipe)
                        .addOnSuccessListener(aVoid -> {
                            mDatabase.child("numRecipes").setValue(finalRecipeKey)
                                    .addOnFailureListener(e -> {
                                        Log.e("GreenPlate", "Recipe key was not updated");
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("GreenPlate", "Recipe was not added");
                        });
            } else {
                Log.e("GreenPlate", "Invalid recipe key");
            }
        });
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
        holder.bind(recipe);
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

        void bind(Recipe recipe) {
            recipeTitleTextView.setText(recipe.getTitle());
            recipeIngredientsTextView.setText(
                    "Ingredients: " + TextUtils.join(", ", recipe.getIngredients()));
            recipeQuantityTextView.setText("Quantity: " + recipe.getQuantity());
        }
    }
}
