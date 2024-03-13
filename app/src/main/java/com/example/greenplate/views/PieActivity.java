package com.example.greenplate.views;

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
import com.example.greenplate.R;
import com.example.greenplate.models.SingletonFirebase;
import com.anychart.charts.Pie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PieActivity extends AppCompatActivity {
    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_activity);

        exitBtn = findViewById(R.id.ExitBtn1);
        exitBtn.setOnClickListener(new View.OnClickListener() {
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
        DatabaseReference databaseReference = SingletonFirebase.getInstance()
                .getDatabaseReference();
        String userId = SingletonFirebase.getInstance().getFirebaseAuth().getCurrentUser().getUid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Fetch meals for the current date
        databaseReference.child("users").child(userId).child("meals").child(currentDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<DataEntry> dataEntries = new ArrayList<>();
                        for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                            String mealName = mealSnapshot.getKey();
                            // Now we fetch the calories as a Long directly
                            Long calories = mealSnapshot.getValue(Long.class);
                            if (mealName != null && calories != null) {
                                dataEntries.add(new ValueDataEntry(mealName, calories));
                            }
                        }

                        Pie pie = AnyChart.pie();
                        pie.data(dataEntries);
                        pie.background("#CDEBC5");
                        pie.title().fontColor("#000000");
                        pie.labels().fontColor("#000000");
                        pie.title("Calories in Each Meal for " + currentDate);
                        anyChartView.setChart(pie);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PieActivity", "Database error: " + databaseError.getMessage());
                    }
                });
    }
}

