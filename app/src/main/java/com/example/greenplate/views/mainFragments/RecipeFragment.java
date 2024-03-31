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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeViewModel adapter;
    private String recipeTitle;
    private String recipeQuantity;
    private List<Recipe> recipeList;
    private List<String> recipeIngredients;
    private Button storeRecipeBtn;
    private Spinner sortSpinner; // Spinner for sorting options

    private TextInputEditText recipeNameInput;
    private TextInputEditText recipeIngredientsInput;
    private TextInputEditText recipeQuantityInput;
    private TextInputEditText recipeIngredientQuantityInput;


    private DatabaseReference recipes;

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        recipes = SingletonFirebase.getInstance().getDatabaseReference()
                .child("cookbook");
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        recyclerView = rootView.findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeList = new ArrayList<>();

        // Initialize the spinner
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

            String[] inputRes = adapter.handleRecipeInputData(ingredients, quantity, recipeName,
                    quantityIngredients);
            if (inputRes[0].equals("false")) {
                Toast.makeText(getContext(), inputRes[1], Toast.LENGTH_LONG).show();
            } else {
                adapter.storeRecipe(ingredients, quantity, recipeName, quantityIngredients);

                recipeNameInput.setText("");
                recipeIngredientsInput.setText("");
                recipeQuantityInput.setText("");
                recipeIngredientQuantityInput.setText("");
            }
        });

        recipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator =
                            new GenericTypeIndicator<HashMap<String, Object>>() { };
                    HashMap<String, Object> recipe = recipeSnapshot.getValue(genericTypeIndicator);
                    recipeTitle = (String) recipe.get("title");
                    recipeIngredients = (List<String>) recipe.get("ingredients");
                    recipeQuantity = (String) recipe.get("quantity");
                    recipeList.add(new Recipe(recipeTitle, recipeIngredients, recipeQuantity,
                            null));
                    adapter = new RecipeViewModel(recipeList);
                    recyclerView.setAdapter(adapter);
                    adapter = new RecipeViewModel(recipeList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read global recipes",
                        databaseError.toException());
            }
        });

        // Set listener for sorting options
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Apply sorting strategy based on selected option
                switch (position) {
                    case 0: // Sort by quantity
                        adapter.setSortingStrategy(new SortByQuantityStrategy());
                        adapter.applySortStrategy();
                        break;
                    case 1: // Sort by title
                        adapter.setSortingStrategy(new SortByTitleStrategy());
                        adapter.applySortStrategy();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle Errors
            }
        });

        return rootView;
    }
}
