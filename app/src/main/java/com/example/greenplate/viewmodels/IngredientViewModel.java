package com.example.greenplate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.SingletonFirebase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IngredientViewModel extends ViewModel {

    private DatabaseReference mDatabase;
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Ingredient>> ingredientListLiveData;

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public LiveData<List<Ingredient>> getIngredientListLiveData() {
        return ingredientListLiveData;
    }

    public IngredientViewModel() {
        // Assuming "pantry" is a direct child of the database root
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
        ingredientListLiveData = new MutableLiveData<>();
        // Initialize with an empty list
        ingredientListLiveData.setValue(new ArrayList<>());
    }

    public void addIngredientToPantry(String userId, Ingredient ingredient) {
        DatabaseReference pantryRef = mDatabase.child("users").child(userId).child("pantry").child(ingredient.getName());

        pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Ingredient existingIngredient = dataSnapshot.getValue(Ingredient.class);
                    if (existingIngredient != null && existingIngredient.getQuantity() > 0) {
                    } else {
                        messageLiveData.postValue("The ingredient already exists.");
                        addPantry(pantryRef, ingredient);
                    }
                } else {
                    // Ingredient does not exist, proceed to add
                    addPantry(pantryRef, ingredient);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void addPantry(DatabaseReference pantryRef, Ingredient ingredient) {
        if (ingredient.getQuantity() <= 0) {
            messageLiveData.postValue("Quantity must be positive.");
            return;
        }
        pantryRef.setValue(ingredient)
                .addOnSuccessListener(aVoid -> messageLiveData.postValue("Ingredient added to pantry."))
                .addOnFailureListener(e -> messageLiveData.postValue("Failed to add ingredient: " + e.getMessage()));
    }

    private void removeIngredientFromPantry(DatabaseReference pantryRef) {
        pantryRef.removeValue()
                .addOnSuccessListener(aVoid -> messageLiveData.postValue("Ingredient removed from pantry."))
                .addOnFailureListener(e -> messageLiveData.postValue("Failed to remove ingredient: " + e.getMessage()));
    }

    // Method to increase the quantity of an ingredient
    public void increaseIngredientQuantity(String name) {
        List<Ingredient> currentList = ingredientListLiveData.getValue();
        if (currentList != null) {
            for (Ingredient ingredient : currentList) {
                if (ingredient.getName().equals(name)) {
                    ingredient.setQuantity(ingredient.getQuantity() + 1);
                    ingredientListLiveData.setValue(currentList);
                    return;
                }
            }
        }
    }

    // Method to decrease the quantity of an ingredient
    public void decreaseIngredientQuantity(String name) {
        List<Ingredient> currentList = ingredientListLiveData.getValue();
        if (currentList != null) {
            for (Ingredient ingredient : currentList) {
                if (ingredient.getName().equals(name) && ingredient.getQuantity() > 0) {
                    ingredient.setQuantity(ingredient.getQuantity() - 1);
                    ingredientListLiveData.setValue(currentList);
                    return;
                }
            }
        }
    }
}
