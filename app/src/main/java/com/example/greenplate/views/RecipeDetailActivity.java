package com.example.greenplate.views;

import static com.example.greenplate.viewmodels.RecipeViewModel.hasAllIngredients;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.SingletonFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RecipeDetailActivity extends AppCompatActivity {

    private Button cookButton;
    private Recipe currentRecipe;
    private Map<String, Integer> userPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Button backButton = findViewById(R.id.backButton);
        cookButton = findViewById(R.id.cookButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize the userPantry map
        userPantry = new HashMap<>();

        // Load pantry data
        loadPantryData();

        // Try to get the complete Recipe object from the intent
        if (getIntent().hasExtra("RECIPE_OBJECT")) {
            currentRecipe = (Recipe) getIntent().getSerializableExtra("RECIPE_OBJECT");
            Log.d(TAG, currentRecipe.getTitle() + " "
                    + currentRecipe.getIngredients() + " "
                    + currentRecipe.getQuantity() + " " + currentRecipe.getIngredientQuantities());
            if (currentRecipe != null) {
                Log.d(TAG, "Recipe received: " + currentRecipe.getTitle());
            } else {
                Log.d(TAG, "Recipe object is null");
            }
        }

        if (currentRecipe == null) {
            Log.d(TAG, "No Recipe Data Available");
            showMessage("Error: No recipe data found.");
            finish();
            return;
        }

        // Display recipe details
        displayRecipeDetails(currentRecipe);

        cookButton.setOnClickListener(v -> {
            Log.d(TAG, "Cook button clicked.");
            if (userPantry != null && !userPantry.isEmpty()) {
                cookRecipe(currentRecipe);
            } else {
                Log.d(TAG, "Cannot cook: Pantry data is not available.");
                showMessage("Cannot cook: Pantry data is not available.");
            }
        });
    }

    private void displayRecipeDetails(Recipe recipe) {
        TextView titleTextView = findViewById(R.id.detailTitleTextView);
        TextView ingredientsTextView = findViewById(R.id.detailIngredientsTextView);
        TextView quantityTextView = findViewById(R.id.detailQuantityTextView);

        titleTextView.setText(recipe.getTitle());
        ingredientsTextView.setText(TextUtils.join(", ", recipe.getIngredients()));
        quantityTextView.setText(recipe.getQuantity());
    }


    private static final String TAG = "CookRecipeActivity";

    private void cookRecipe(Recipe recipe) {
        new AsyncTask<Recipe, Void, Boolean>() {
            private int totalCalories = 0;

            @Override
            protected Boolean doInBackground(Recipe... recipes) {
                Recipe recipe = recipes[0];
                CountDownLatch latch = new CountDownLatch(recipe.getIngredientQuantities().size());

                for (Map.Entry<String, Integer> entry : recipe.getIngredientQuantities()
                        .entrySet()) {
                    String ingredientName = entry.getKey();
                    int requiredQuantity = entry.getValue();

                    // Fetch the actual caloriesPerServing from the pantry
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ingredientRef = SingletonFirebase.getInstance()
                            .getDatabaseReference()
                            .child("users").child(userId)
                            .child("pantry").child(ingredientName);

                    ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Ingredient ingredient = dataSnapshot.getValue(Ingredient.class);
                                if (ingredient != null) {
                                    int calories = ingredient.getCaloriesPerServing()
                                            * requiredQuantity;
                                    totalCalories += calories;
                                }
                            }
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            latch.countDown();
                        }
                    });
                }

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return hasAllIngredients(recipe, userPantry);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    addRecipeToMealHistory(recipe);
                    updateUserMeals(recipe.getTitle(), totalCalories);
                    updateCalorieCount(totalCalories);
                    for (Map.Entry<String, Integer> entry
                            : recipe.getIngredientQuantities().entrySet()) {
                        String ingredientName = entry.getKey();
                        int requiredQuantity = entry.getValue();
                        int currentQuantity = userPantry.getOrDefault(ingredientName, 0);
                        int newQuantity = currentQuantity - requiredQuantity;

                        if (newQuantity >= 0) {
                            updateIngredientQuantityInPantry(ingredientName, newQuantity);
                        } else {
                            // Handle the case where there isn't enough of this ingredient
                            Log.e(TAG, "Not enough " + ingredientName + " to cook the recipe.");
                            // This would be a good place to add code to inform the user via the UI.
                        }
                    }
                } else {
                    showMessage("Not enough ingredients to cook this recipe.");
                }
            }
        }.execute(recipe);
    }


    private void updateUserMeals(String recipeTitle, int additionalCalories) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        DatabaseReference recipeRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("meals").child(currentDate).child(recipeTitle);

        // Run a transaction to increment the calories
        recipeRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Try to retrieve the current calorie count for the recipe
                Integer currentCalories = mutableData.getValue(Integer.class);
                if (currentCalories == null) {
                    mutableData.setValue(additionalCalories);
                } else {
                    // If it is there, increment the calorie count
                    mutableData.setValue(currentCalories + additionalCalories);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   boolean committed, @Nullable DataSnapshot dataSnapshot) {
                // Transaction completed
                if (databaseError != null) {
                    Log.e(TAG, "Firebase counter increment failed: " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "Firebase counter increment succeeded.");
                }
            }
        });
    }




    // Helper methods (will need to be implemented)

    private void updateIngredientQuantityInPantry(String ingredientName, int newQuantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference pantryRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("pantry")
                .child(ingredientName).child("quantity");

        pantryRef.setValue(newQuantity)
                .addOnSuccessListener(aVoid -> Log.d(TAG,
                        "Pantry updated successfully for ingredient: "
                        + ingredientName))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating pantry for ingredient: "
                        + ingredientName + ": " + e.getMessage()));
    }


    private void addRecipeToMealHistory(Recipe recipe) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mealHistoryRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("mealHistory");

        String mealId = mealHistoryRef.push().getKey();
        if (mealId != null) {
            mealHistoryRef.child(mealId).setValue(recipe)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Meal history updated successfully."))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating meal history: "
                            + e.getMessage()));
        }
    }


    private void loadPantryData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference pantryRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("pantry");

        pantryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userPantry.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ingredient ingredient = snapshot.getValue(Ingredient.class);
                    if (ingredient != null) {
                        userPantry.put(ingredient.getName(), (int) ingredient.getQuantity());
                    }
                }
                Log.d(TAG, "Pantry data loaded: " + userPantry);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load pantry data: " + databaseError.getMessage());
            }
        });
    }



    private void updateCalorieCount(int totalCalories) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference calorieRef = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("dailyCalories");

        calorieRef.setValue(totalCalories)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Calories updated successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating calories: "
                        + e.getMessage()));

        showMessage("Recipe cooked successfully.");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}