package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.SingletonFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IngredientViewModel extends ViewModel {

    private DatabaseReference mDatabase;
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public IngredientViewModel() {
        // Assuming "pantry" is a direct child of the database root
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
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
}
