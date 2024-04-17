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
import android.widget.CheckBox;
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
    private Button buyItemsButton;
    private ShoppingListAdapter shoppingListAdapter;

    private DatabaseReference shoppingItems;

    private String userId = SingletonFirebase.getInstance()
            .getFirebaseAuth().getCurrentUser().getUid();

    private ShoppingListViewModel mViewModel;
    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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

        buyItemsButton = view.findViewById(R.id.buyItemsButton);
        RecyclerView ingredientRecyclerView = view.findViewById(R.id.shoppingListRecyclerView);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        buyItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ingredient> boughtIngredients = new ArrayList<>();
                for (int i = 0; i < shoppingListAdapter.getItemCount(); i++) {
                    ShoppingListAdapter.ShoppingListViewHolder holder =
                            (ShoppingListAdapter.ShoppingListViewHolder)
                            ingredientRecyclerView.findViewHolderForAdapterPosition(i);
                    if (holder.checkBox.isChecked()) {
                        Ingredient ingredient = shoppingListAdapter.getShoppingList().get(i);
                        boughtIngredients.add(ingredient);
                        // Remove from Firebase shopping list
                        shoppingItems.child(ingredient.getName()).removeValue();
                        // Add to Firebase pantry (handle duplicates as necessary)
                        addToPantry(ingredient);
                    }
                }
                shoppingListAdapter.notifyDataSetChanged();
            }
        });


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
    private void addToPantry(Ingredient ingredient) {
        DatabaseReference pantryRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("pantry");
        pantryRef.child(ingredient.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Merge quantities if exists
                            Ingredient existingIngredient = snapshot.getValue(Ingredient.class);
                            existingIngredient.setQuantity((int) (existingIngredient.getQuantity()
                                    + ingredient.getQuantity()));
                            pantryRef.child(ingredient.getName()).setValue(existingIngredient);
                        } else {
                            // Add new ingredient to pantry
                            pantryRef.child(ingredient.getName()).setValue(ingredient);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ShoppingListFragment", "Failed to update pantry",
                                error.toException());
                    }
                });
    }

    // Adapter class for RecyclerView
    private class ShoppingListAdapter
            extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

        private List<Ingredient> shoppingList;

        private ShoppingListAdapter(List<Ingredient> shoppingList) {
            this.shoppingList = shoppingList;
        }

        @NonNull
        @Override
        public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ingredient_shopping_list, parent, false);
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
        public List<Ingredient> getShoppingList() {
            return shoppingList;
        }

        public class ShoppingListViewHolder extends RecyclerView.ViewHolder {
            // Declare views
            private TextView nameTextView;
            private TextView quantityTextView;
            private Button increaseButton;
            private Button decreaseButton;
            private CheckBox checkBox;
            private IngredientViewModel ingredientViewModel = new IngredientViewModel();

            public ShoppingListViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views
                nameTextView = itemView.findViewById(R.id.nameTextView);
                quantityTextView = itemView.findViewById(R.id.quantityTextView);
                increaseButton = itemView.findViewById(R.id.increaseButton);
                decreaseButton = itemView.findViewById(R.id.decreaseButton);
                checkBox = itemView.findViewById(R.id.checkBoxItem);
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
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Ingredient ingredient = shoppingList.get(position);
                    }
                });
            }

            public void bind(Ingredient ingredient) {
                // Bind data to views
                nameTextView.setText(ingredient.getName());
                quantityTextView.setText(String.valueOf(ingredient.getQuantity()));
                checkBox.setChecked(false);

            }
        }
    }

}