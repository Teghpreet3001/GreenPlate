package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;

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


import java.util.ArrayList;
import java.util.List;

public class ColumnActivity extends AppCompatActivity{
        private Button ExitBtn;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.column_activity);

            ExitBtn = findViewById(R.id.ExitBtn);
            ExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Finish the current activity
                    finish();
                                           }
            });
            AnyChartView anyChartView = findViewById(R.id.any_chart_view);
            anyChartView.setProgressBar(findViewById(R.id.progress_bar));

            Intent intent = getIntent();
            double calorieGoal = intent.getDoubleExtra("calorieGoal", 0);
            double dailyCalorieIntake = intent.getDoubleExtra("dailyCalorieIntake", 0);

            Cartesian cartesian = AnyChart.column();
            Log.d("AnyChartDebug", "Setting up chart...");

            List<DataEntry> data = new ArrayList<>();
            data.add(new ValueDataEntry("Calorie Goal", calorieGoal));
            data.add(new ValueDataEntry("Daily Calorie Intake", dailyCalorieIntake));

            Column column = cartesian.column(data);

            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d)
                    .format("{%Value}{groupsSeparator: }");

            cartesian.animation(true);
            cartesian.title("Calorie Comparison");

            cartesian.yScale().minimum(0d);

            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            cartesian.interactivity().hoverMode(HoverMode.BY_X);

            cartesian.xAxis(0).title("Category");
            cartesian.yAxis(0).title("Calories");

            cartesian.background("#CDEBC5");
            cartesian.title().fontColor("#000000");
            cartesian.labels().fontColor("#000000");
            anyChartView.setChart(cartesian);
        }
    }

