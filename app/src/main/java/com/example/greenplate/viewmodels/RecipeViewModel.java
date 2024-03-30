package com.example.greenplate.viewmodels;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import com.example.greenplate.views.RecipeDetailActivity;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;

import java.util.ArrayList;
import java.util.List;

// Behaves like an adapter
public class RecipeViewModel extends RecyclerView.Adapter<RecipeViewModel.RecipeViewHolder> {
    private List<Recipe> recipeList;

    public RecipeViewModel(List<Recipe> recipeList) {
        this.recipeList = recipeList;
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
                intent.putStringArrayListExtra("RECIPE_INGREDIENTS", new ArrayList<>(recipe.getIngredients()));

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
