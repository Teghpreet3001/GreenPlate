package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.HashMap;
import java.util.Map;

public class InputMealViewModel extends ViewModel {
    private DatabaseReference mDatabase;

    public InputMealViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void storeMeal(String userId, String mealName, int calories) {
        // Get a reference to the user
        DatabaseReference userRef = mDatabase.child("users").child(userId);

        // Update the user's meals map with the new meal
        Map<String, Object> mealUpdates = new HashMap<>();
        mealUpdates.put("/meals/" + mealName, calories);

        userRef.updateChildren(mealUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Handle successful update
                })
                .addOnFailureListener(e -> {
                    // Handle failed update
                });
    }

}
