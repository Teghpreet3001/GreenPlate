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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputMealViewModel extends ViewModel {
    private DatabaseReference mDatabase;

    public InputMealViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
