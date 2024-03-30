package com.example.greenplate.views.mainFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.views.InputIngredientActivity;

import java.util.List;

public class IngredientFragment extends Fragment {

    private Button addIngredientButton;
    private IngredientViewModel ingredientViewModel;
    private IngredientAdapter ingredientAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        RecyclerView ingredientRecyclerView = view.findViewById(R.id.ingredientRecyclerView);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ingredientAdapter = new IngredientAdapter();
        ingredientRecyclerView.setAdapter(ingredientAdapter);

        // Initialize ViewModel
        ingredientViewModel = new ViewModelProvider(this).get(IngredientViewModel.class);

        // Observe LiveData for ingredient list changes
        ingredientViewModel.getIngredientListLiveData().observe(getViewLifecycleOwner(), ingredients -> {
            // Update UI when ingredient list changes
            ingredientAdapter.setIngredients(ingredients);
        });

        // Initialize the add ingredient button
        addIngredientButton = view.findViewById(R.id.addIngredientButton);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the InputIngredientActivity to add a new ingredient
                Intent intent = new Intent(getActivity(), InputIngredientActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Adapter class for RecyclerView
    private class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

        private Ingredient[] ingredients = new Ingredient[0];

        public void setIngredients(Ingredient[] ingredients) {
            this.ingredients = ingredients;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
            return new IngredientViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
            Ingredient ingredient = ingredients[position];
            holder.bind(ingredient);
        }

        @Override
        public int getItemCount() {
            return ingredients.length;
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients.toArray(new Ingredient[0]);
        }

        public class IngredientViewHolder extends RecyclerView.ViewHolder {
            // Declare views
            private TextView nameTextView;
            private TextView quantityTextView;
            private Button increaseButton;
            private Button decreaseButton;

            public IngredientViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views
                nameTextView = itemView.findViewById(R.id.nameTextView);
                quantityTextView = itemView.findViewById(R.id.quantityTextView);
                increaseButton = itemView.findViewById(R.id.increaseButton);
                decreaseButton = itemView.findViewById(R.id.decreaseButton);

                // Set click listeners for increase and decrease buttons
                increaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Ingredient ingredient = ingredients[position];
                            // Increase quantity
                            ingredientViewModel.increaseIngredientQuantity(ingredient.getName());
                        }
                    }
                });

                decreaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Ingredient ingredient = ingredients[position];
                            // Decrease quantity
                            ingredientViewModel.decreaseIngredientQuantity(ingredient.getName());
                        }
                    }
                });
            }

            public void bind(Ingredient ingredient) {
                // Bind data to views
                nameTextView.setText(ingredient.getName());
                quantityTextView.setText(String.valueOf(ingredient.getQuantity()));
            }
        }
    }
}
