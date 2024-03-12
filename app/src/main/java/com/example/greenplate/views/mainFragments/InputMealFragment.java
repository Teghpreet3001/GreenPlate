package com.example.greenplate.views.mainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private InputMealViewModel mViewModel;
    private TextView calorieGoalText;
    private TextView dailyCalorieText;
    private DatabaseReference databaseReference;
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static InputMealFragment newInstance() {
        return new InputMealFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_meal, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        calorieGoalText = view.findViewById(R.id.calorieGoalText);
        dailyCalorieText = view.findViewById(R.id.dailyCalorieIntakeText);

        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String gender = String.valueOf(snapshot.child("gender").getValue());
                    int age = (Integer.valueOf(String.valueOf(snapshot.child("age").getValue())) != null) ? Integer.valueOf(String.valueOf(snapshot.child("age").getValue())) : 19;
                    int weight = Integer.valueOf(String.valueOf(snapshot.child("weight").getValue()));
                    int height = Integer.valueOf(String.valueOf(snapshot.child("height").getValue()));

                    double bmr;

                    if (gender.trim().equals("Male")) {
                        bmr = 10 * weight + 6.25 * height - 5 * age + 5;
                    } else {
                        bmr = 10 * weight + 6.25 * height - 5 * age - 161;
                    }

                    calorieGoalText.setText("Calculated Calorie Goal: " + bmr);
                    Map<String, Long> meals = (Map<String, Long>) snapshot.child("meals").getValue();

                    if (meals != null) {
                        long calorieIntake = 0;

                        for (String key : meals.keySet()) {
                            calorieIntake += meals.get(key);
                        }

                        dailyCalorieText.setText("Daily Calorie Intake: " + calorieIntake);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}