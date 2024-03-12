package com.example.greenplate.views.mainFragments;

import static com.example.greenplate.views.SignUpActivity.defaultAge;
import static com.example.greenplate.views.SignUpActivity.defaultGender;
import static com.example.greenplate.views.SignUpActivity.defaultHeight;
import static com.example.greenplate.views.SignUpActivity.defaultWeight;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class InputMealFragment extends Fragment {

    private Button storeMealBtn;

    private TextInputEditText mealNameInput, mealCaloriesInput;
    private TextView calorieGoalText, dailyCalorieText;
    private InputMealViewModel inputMealViewModel;
    private DatabaseReference databaseReference;
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_meal, container, false);

        // Initialize components from both branches
        storeMealBtn = view.findViewById(R.id.storeMealBtn);
        mealNameInput = view.findViewById(R.id.mealNameInput);
        mealCaloriesInput = view.findViewById(R.id.mealCaloriesInput);
        calorieGoalText = view.findViewById(R.id.calorieGoalText);
        dailyCalorieText = view.findViewById(R.id.dailyCalorieIntakeText);

        inputMealViewModel = new ViewModelProvider(this).get(InputMealViewModel.class);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Button click listener for storing meals
        storeMealBtn.setOnClickListener(v -> {
            String mealName = mealNameInput.getText().toString().trim();
            String calorieString = mealCaloriesInput.getText().toString().trim();

            if (mealName.isEmpty() || calorieString.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories;
            try {
                calories = Integer.parseInt(calorieString);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Calories must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId == null) {
                Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
                return;
            }

            inputMealViewModel.storeMeal(userId, mealName, calories);
            mealNameInput.setText("");
            mealCaloriesInput.setText("");

            // Update calorie goal and daily intake immediately
            fetchAndUpdateCalorieData();
        });

        // Initial fetch and display of calorie data
        fetchAndUpdateCalorieData();

        return view;
    }
    private void fetchAndUpdateCalorieData() {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("gender").getValue() == null ||
                            String.valueOf(snapshot.child("gender").getValue()).equals(defaultGender) ||
                            snapshot.child("age").getValue() == null ||
                            String.valueOf(snapshot.child("age").getValue()).equals(defaultAge) ||
                            snapshot.child("weight").getValue() == null ||
                            String.valueOf(snapshot.child("weight").getValue()).equals(defaultWeight) ||
                            snapshot.child("height").getValue() == null ||
                            String.valueOf(snapshot.child("height").getValue()).equals(defaultHeight)) {
                        Toast.makeText(getContext(), "Please input all your detals in the profile page.", Toast.LENGTH_LONG).show();
                    } else {
                        int age = Integer.valueOf(String.valueOf(snapshot.child("age").getValue()));
                        int weight = Integer.valueOf(String.valueOf(snapshot.child("weight").getValue()));
                        int height = Integer.valueOf(String.valueOf(snapshot.child("height").getValue()));
                        String gender = String.valueOf(snapshot.child("gender").getValue());

                        double bmr;

                        if (gender.trim().equals("Male")) {
                            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
                        } else {
                            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
                        }

                        calorieGoalText.setText("Calculated Calorie Goal: " + bmr);
                    }

                    Map<String, Long> meals = (Map<String, Long>) snapshot.child("meals").getValue();
                    if (meals != null) {
                        long calorieIntake = 0;

                        for (String key : meals.keySet()) {
                            calorieIntake += meals.get(key);
                        }

                        dailyCalorieText.setText("Daily Calorie Intake: " + calorieIntake);
                    } else {
                        Toast.makeText(getContext(), "Please input some meals to get your daily calorie intake.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            return null;
        }
    }
}

