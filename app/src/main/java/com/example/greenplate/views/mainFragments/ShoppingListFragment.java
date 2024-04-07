package com.example.greenplate.views.mainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.example.greenplate.views.InputIngredientActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {
    private Button addShoppingItemButton;
    private ShoppingListAdapter shoppingListAdapter;

    private DatabaseReference shoppingItems;

    private ShoppingListViewModel mViewModel;
    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        shoppingItems = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("shoppingList");
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        // Initialize the add ingredient button
        addShoppingItemButton = view.findViewById(R.id.addShoppingItemButton);
        // If you want to do something with the exit button

        // Set up the button click listener
        addShoppingItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddIngredientActivity
                Intent intent = new Intent(getActivity(), InputIngredientActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView ingredientRecyclerView = view.findViewById(R.id.shoppingListRecyclerView);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingItems.addValueEventListener(new ValueEventListener() {
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
                shoppingListAdapter = new ShoppingListAdapter(ingredientList);
                ingredientRecyclerView.setAdapter(shoppingListAdapter);
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
    private class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

        private List<Ingredient> shoppingList;

        private ShoppingListAdapter(List<Ingredient> shoppingList) {
            this.shoppingList = shoppingList;
        }

        @NonNull
        @Override
        public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ingredient_item, parent, false);
            return new ShoppingListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
            Ingredient ingredient = shoppingList.get(position);
            holder.bind(ingredient);
        }

        @Override
        public int getItemCount() {
            return shoppingList.size();
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.shoppingList = shoppingList;
        }

        public class ShoppingListViewHolder extends RecyclerView.ViewHolder {
            // Declare views
            private TextView nameTextView;
            private TextView quantityTextView;
            private Button increaseButton;
            private Button decreaseButton;
            private IngredientViewModel ingredientViewModel = new IngredientViewModel();

            public ShoppingListViewHolder(@NonNull View itemView) {
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
                            Ingredient ingredient = shoppingList.get(position);
                            ingredientViewModel.increaseShoppingItemQuantity(ingredient);
                        }
                    }
                });

                decreaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Ingredient ingredient = shoppingList.get(position);
                            // Decrease quantity
                            ingredientViewModel.decreaseShoppingItemQuantity(ingredient);
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