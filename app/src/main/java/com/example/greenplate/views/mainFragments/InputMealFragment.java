package com.example.greenplate.views.mainFragments;


import android.content.Context;
import static com.example.greenplate.viewmodels.SignUpViewModel.defaultAge;
import static com.example.greenplate.viewmodels.SignUpViewModel.defaultGender;
import static com.example.greenplate.viewmodels.SignUpViewModel.defaultHeight;
import static com.example.greenplate.viewmodels.SignUpViewModel.defaultWeight;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.greenplate.views.ColumnActivity;
import com.example.greenplate.views.PieActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

public class InputMealFragment extends Fragment {

    private Button storeMealBtn;
    private Button compareCaloriesBtn;
    private Button MealEatenBtn;
    private TextInputEditText mealNameInput, mealCaloriesInput;
    private TextView calorieGoalText, dailyCalorieText;
    private InputMealViewModel inputMealViewModel;
    private DatabaseReference databaseReference;
    private double calorieGoal = 0.0;
    private double dailyCalorieIntake = 0.0;
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_meal, container, false);

        storeMealBtn = view.findViewById(R.id.storeMealBtn);
        compareCaloriesBtn = view.findViewById(R.id.compareCaloriesBtn);
        MealEatenBtn = view.findViewById(R.id.MealEatenBtn);
        mealNameInput = view.findViewById(R.id.mealNameInput);
        mealCaloriesInput = view.findViewById(R.id.mealCaloriesInput);
        calorieGoalText = view.findViewById(R.id.calorieGoalText);
        dailyCalorieText = view.findViewById(R.id.dailyCalorieIntakeText);

        inputMealViewModel = new ViewModelProvider(this).get(InputMealViewModel.class);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        compareCaloriesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ColumnActivity.class);
            intent.putExtra("calorieGoal", calorieGoal);
            intent.putExtra("dailyCalorieIntake", dailyCalorieIntake);
            startActivity(intent);
        });
        MealEatenBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PieActivity.class);
            startActivity(intent);
        });
        // storing meals
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // Correct placement of storeMeal call
            inputMealViewModel.storeMeal(userId, currentDate, mealName, calories);
            mealNameInput.setText("");
            mealCaloriesInput.setText("");

            fetchAndUpdateCalorieData();
        });

        fetchAndUpdateCalorieData();
        return view;
    }

    private void fetchAndUpdateCalorieData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        databaseReference.child("users").child(userId).child("meals").child(currentDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double dailyCalories = 0;
                        for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                            Long calories = mealSnapshot.getValue(Long.class);
                            if (calories != null) {
                                dailyCalories += calories;
                            }
                        }
                        dailyCalorieIntake = dailyCalories;
                        dailyCalorieText.setText("Daily Calorie Intake: " + dailyCalorieIntake);

                        // Fetch user profile info for BMR calculation
                        databaseReference.child("users").child(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        if (userSnapshot.exists()) {
                                            String ageStr = userSnapshot.child("age").getValue(String.class);
                                            String weightStr = userSnapshot.child("weight").getValue(String.class);
                                            String heightStr = userSnapshot.child("height").getValue(String.class);
                                            String gender = userSnapshot.child("gender").getValue(String.class);

                                            if (ageStr != null && weightStr != null && heightStr != null && gender != null) {
                                                try {
                                                    int age = Integer.parseInt(ageStr);
                                                    int weight = Integer.parseInt(weightStr);
                                                    int height = Integer.parseInt(heightStr);

                                                    double bmr = calculateBMR(gender, age, weight, height);
                                                    calorieGoalText.setText("Calculated Calorie Goal: " + bmr);
                                                    calorieGoal = bmr; // Update global BMR value
                                                } catch (NumberFormatException e) {
                                                    Toast.makeText(getContext(), "Enter values in Profile Page", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // errors - again, do we handle these
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // errors - do we wanna handle these?
                    }
                });
    }



    private double calculateBMR(String gender, int age, int weight, int height) {
        return gender.equals("Male") ? 10 * weight + 6.25 * height - 5 * age + 5 :
                10 * weight + 6.25 * height - 5 * age - 161;

    }
}