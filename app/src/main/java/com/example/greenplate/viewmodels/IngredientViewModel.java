package com.example.greenplate.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.SingletonFirebase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
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
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
        ingredientListLiveData = new MutableLiveData<>();
        ingredientListLiveData.setValue(new ArrayList<>());
    }
    public static String[] handleIngredientInputData(String name, double quantity, int calories, String expiry) {
        if (name == null) {
            return new String[]{"false", "Name is null"};

        }
        if (name.trim().isEmpty()) {
            return new String[]{"false", "Ingredients field is empty"};
        }
        return new String[]{"true", ""};

    }
        public void addIngredientToPantry(String userId, Ingredient ingredient) {
        if (ingredient.getName() == null || ingredient.getName().trim().isEmpty()) {
            messageLiveData.postValue("Ingredient must have a name.");
            return;
        }
        if (ingredient.getQuantity() <= 0) {
            messageLiveData.postValue("Quantity must be positive.");
            return;
        }
        if (ingredient.getCaloriesPerServing() <= 0) {
            messageLiveData.postValue("Calories per serving must be positive.");
            return;
        }

        DatabaseReference pantryRef = mDatabase.child("users").child(userId).child("pantry")
                .child(ingredient.getName());

        pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Ingredient existingIngredient = dataSnapshot.getValue(Ingredient.class);
                    if (existingIngredient != null && existingIngredient.getQuantity() > 0) {
                        messageLiveData.postValue("The ingredient already exists.");
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
        pantryRef.setValue(ingredient)
                .addOnSuccessListener(aVoid -> messageLiveData
                        .postValue("Ingredient added to pantry."))
                .addOnFailureListener(e -> messageLiveData.
                        postValue("Failed to add ingredient: " + e.getMessage()));
    }

    private void removeIngredientFromPantry(DatabaseReference ingredientToRemove) {
        ingredientToRemove.removeValue()
                .addOnSuccessListener(aVoid -> messageLiveData
                        .postValue("Ingredient removed from pantry."))
                .addOnFailureListener(e -> messageLiveData
                        .postValue("Failed to remove ingredient: " + e.getMessage()));
    }

    // Method to increase the quantity of an ingredient
    public void increaseIngredientQuantity(Ingredient ingredient) {
        final String userId = SingletonFirebase.getInstance().getFirebaseAuth()
                .getCurrentUser().getUid();
        DatabaseReference ingredientToIncrease = SingletonFirebase.getInstance()
                .getDatabaseReference()
                .child("users").child(userId).child("pantry").child(ingredient.getName());
        DatabaseReference quantity = ingredientToIncrease.child("quantity");

        // Increment the value of "quantity" by 1
        quantity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long currentQuantity = (Long) dataSnapshot.getValue();
                    if (currentQuantity != null) {
                        long newQuantity = currentQuantity + 1;
                        quantity.setValue(newQuantity);
                        System.out.println("Quantity incremented to: " + newQuantity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

    // Method to decrease the quantity of an ingredient
    public void decreaseIngredientQuantity(Ingredient ingredient) {
        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        DatabaseReference ingredientToDecrease = SingletonFirebase.getInstance()
                .getDatabaseReference()
                .child("users").child(userId).child("pantry").child(ingredient.getName());
        DatabaseReference quantity = ingredientToDecrease.child("quantity");

        // Decrement the value of "quantity" by 1
        quantity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long currentQuantity = (Long) dataSnapshot.getValue();
                    if (currentQuantity != null) {
                        long newQuantity = currentQuantity - 1;
                        quantity.setValue(newQuantity);
                        if (newQuantity == 0) {
                            removeIngredientFromPantry(ingredientToDecrease);
                        }
                        System.out.println("Quantity decremented to: " + newQuantity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }
}


