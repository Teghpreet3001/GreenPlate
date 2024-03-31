package com.example.greenplate.views.mainFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.views.InputIngredientActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IngredientFragment extends Fragment {

    private Button addIngredientButton;
    private IngredientAdapter ingredientAdapter;

    private DatabaseReference ingredients;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        ingredients = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("pantry");
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        // Initialize the add ingredient button
        addIngredientButton = view.findViewById(R.id.addIngredientButton);
        // If you want to do something with the exit button

        // Set up the button click listener
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddIngredientActivity
                Intent intent = new Intent(getActivity(), InputIngredientActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView ingredientRecyclerView = view.findViewById(R.id.ingredientRecyclerView);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        System.out.println(ingredients);
        ingredients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ingredient> ingredientList = new ArrayList<>();
                for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                    String ingredientTitle = ingredientSnapshot.getKey();
                    Integer ingredientQuantity = ingredientSnapshot.
                            child("quantity").getValue(Integer.class);
                    ingredientList.add(
                            new Ingredient(ingredientTitle, ingredientQuantity, 120, "09/09/2024"));
                }
                ingredientAdapter = new IngredientAdapter(ingredientList);
                System.out.println(ingredientAdapter);
                ingredientRecyclerView.setAdapter(ingredientAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read global recipes",
                        databaseError.toException());
            }
        });

        return view;
    }

    // Adapter class for RecyclerView
    private class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter
            .IngredientViewHolder> {

        private List<Ingredient> ingredientList;

        private IngredientAdapter(List<Ingredient> ingredientList) {
            this.ingredientList = ingredientList;
            System.out.println(ingredientList);
        }

        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ingredient_item, parent, false);
            return new IngredientViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
            Ingredient ingredient = ingredientList.get(position);
            holder.bind(ingredient);
        }

        @Override
        public int getItemCount() {
            return ingredientList.size();
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredientList = ingredientList;
        }

        public class IngredientViewHolder extends RecyclerView.ViewHolder {
            // Declare views
            private TextView nameTextView;
            private TextView quantityTextView;
            private Button increaseButton;
            private Button decreaseButton;
            private IngredientViewModel ingredientViewModel = new IngredientViewModel();

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
                            System.out.println(ingredientList);
                            Ingredient ingredient = ingredientList.get(position);
                            ingredientViewModel.increaseIngredientQuantity(ingredient);
                        }
                    }
                });

                decreaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Ingredient ingredient = ingredientList.get(position);
                            // Decrease quantity
                            ingredientViewModel.decreaseIngredientQuantity(ingredient);
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
