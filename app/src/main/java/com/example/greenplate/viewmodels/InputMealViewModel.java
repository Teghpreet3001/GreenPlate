package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.SingletonFirebase;
import com.google.firebase.database.DatabaseReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputMealViewModel extends ViewModel {
    private DatabaseReference mDatabase;

    public InputMealViewModel() {
        mDatabase = SingletonFirebase.getInstance().getDatabaseReference();
    }

    public void storeMeal(String userId, String date, String mealName, int calories) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // If the date is not provided, use the current date
        if (date == null || date.isEmpty()) {
            date = sdf.format(new Date());
        }
        // Get a reference to the user
        DatabaseReference mealRef = mDatabase.child("users").child(userId).child("meals").child(date).child(mealName);

        mealRef.setValue(calories)
                .addOnSuccessListener(aVoid -> {
                    // Handle successful update
                })
                .addOnFailureListener(e -> {
                    // Handle failed update
                });
    }

}
