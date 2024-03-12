package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.greenplate.R;
import com.example.greenplate.views.mainFragments.InputMealFragment;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class PieActivity extends AppCompatActivity{
    private Button ExitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_activity);

        ExitBtn = findViewById(R.id.ExitBtn1);
        ExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity
                finish();
            }
        });
        AnyChartView anyChartView = findViewById(R.id.any_chart_view1);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar1));
        fetchMealDataAndSetupChart(anyChartView);
    }

    private void fetchMealDataAndSetupChart(AnyChartView anyChartView) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child("users").child(userId).child("meals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Snapshot) {
                List<DataEntry> dataEntries = new ArrayList<>();
                for (DataSnapshot snapshot : Snapshot.getChildren()) {
                    String mealName = snapshot.getKey();
                    Long calories = (Long) snapshot.getValue();
                    if (mealName != null && calories != null) {
                        dataEntries.add(new ValueDataEntry(mealName, calories));
                    }
                }

                Pie pie = AnyChart.pie();
                pie.data(dataEntries);

                pie.title("Calories in Each Meal");

                anyChartView.setChart(pie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PieActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

}

