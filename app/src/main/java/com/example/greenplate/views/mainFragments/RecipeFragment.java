package com.example.greenplate.views.mainFragments;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.models.SortByQuantityStrategy;
import com.example.greenplate.models.SortByTitleStrategy;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeViewModel adapter;
    private DatabaseReference mDatabase;
    private List<Recipe> recipeList;

    private Button storeRecipeBtn;
    private Spinner sortSpinner; // Spinner for sorting options
    private TextInputEditText recipeNameInput;
    private TextInputEditText recipeIngredientsInput;
    private TextInputEditText recipeQuantityInput;
    private TextInputEditText recipeIngredientQuantityInput;
    private Map<String, Integer> userPantry = new HashMap<>();
    private Map<String, Integer> shoppingList = new HashMap<>();
    private DatabaseReference recipes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        recipes = SingletonFirebase.getInstance().getDatabaseReference()
                .child("cookbook");
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        recyclerView = rootView.findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeList = new ArrayList<>();

        sortSpinner = rootView.findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        storeRecipeBtn = rootView.findViewById(R.id.storeRecipeBtn);
        recipeNameInput = rootView.findViewById(R.id.recipeNameInput);
        recipeIngredientsInput = rootView.findViewById(R.id.ingredientListInput);
        recipeQuantityInput = rootView.findViewById(R.id.quantityInput);
        recipeIngredientQuantityInput = rootView.findViewById(R.id.ingredientQuantityInput);

        storeRecipeBtn.setOnClickListener(v -> {
            String recipeName = recipeNameInput.getText().toString().trim();
            String ingredients = recipeIngredientsInput.getText().toString().trim();
            String quantity = recipeQuantityInput.getText().toString().trim();
            String quantityIngredients = recipeIngredientQuantityInput.getText().toString().trim();

            String[] inputRes = adapter.handleRecipeInputData(ingredients,
                    quantity, recipeName, quantityIngredients);
            if (inputRes[0].equals("true")) {
                List<String> ingredientsList = new ArrayList<>(Arrays
                        .asList(ingredients.split(",")));
                Map<String, Integer> ingredientQuantitiesMap = new HashMap<>();

                String[] quantitiesArr = quantityIngredients.split(",");
                for (int i = 0; i < ingredientsList.size(); i++) {
                    ingredientQuantitiesMap.put(ingredientsList.get(i).trim(),
                            Integer.parseInt(quantitiesArr[i].trim()));
                }

                Recipe newRecipe = new Recipe(recipeName,
                        ingredientsList, quantity, ingredientQuantitiesMap);

                saveRecipeToFirebase(newRecipe);
            } else {
                Toast.makeText(getContext(), inputRes[1], Toast.LENGTH_LONG).show();
            }
            // to reset after hitting submit
            recipeNameInput.setText("");
            recipeIngredientsInput.setText("");
            recipeQuantityInput.setText("");
            recipeIngredientQuantityInput.setText("");
        });

        recipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeList.add(recipe);
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new RecipeViewModel(recipeList, userPantry, shoppingList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RecipeFragment", "Failed to read global recipes",
                        databaseError.toException());
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // sort by qty
                        adapter.setSortingStrategy(new SortByQuantityStrategy());
                        adapter.applySortStrategy();
                        break;
                    case 1: // sort by title
                        adapter.setSortingStrategy(new SortByTitleStrategy());
                        adapter.applySortStrategy();
                        break;
                    default:
                        // Handle unexpected position here
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle Errors
            }
        });

        DatabaseReference pantryRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("pantry");


        pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String ingredientName = item.child("name").getValue(String.class);
                    Integer quantity = item.child("quantity").getValue(Integer.class);
                    if (ingredientName != null && quantity != null) {
                        userPantry.put(ingredientName, quantity);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RecipeFragment", "Failed to read user pantry", databaseError.toException());
            }
        });

        DatabaseReference shoppingListRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("shoppingList");

        shoppingListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String ingredientName = item.child("name").getValue(String.class);
                    Integer quantity = item.child("quantity").getValue(Integer.class);
                    if (ingredientName != null && quantity != null) {
                        shoppingList.put(ingredientName, quantity);
                    }
                }
                setupRecyclerView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RecipeFragment", "Failed to read shoppinglist",
                        databaseError.toException());
            }
        });

        return rootView;
    }

    private void setupRecyclerView() {
        adapter = new RecipeViewModel(recipeList, userPantry, shoppingList);
        recyclerView.setAdapter(adapter);
    }

    private void saveRecipeToFirebase(Recipe recipe) {
        DatabaseReference newRecipeRef = mDatabase.child("cookbook").push();
        newRecipeRef.setValue(recipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("RecipeFragment", "Recipe saved successfully.");
            } else {
                Log.e("RecipeFragment", "Failed to save recipe. Something went wrong.",
                        task.getException());
            }
        });
    }
}
