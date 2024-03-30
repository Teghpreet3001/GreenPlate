package com.example.greenplate.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String title = getIntent().getStringExtra("RECIPE_TITLE");
        List<String> ingredients = getIntent().getStringArrayListExtra("RECIPE_INGREDIENTS");
        String quantity = getIntent().getStringExtra("RECIPE_QUANTITY");

        TextView titleTextView = findViewById(R.id.detailTitleTextView);
        TextView ingredientsTextView = findViewById(R.id.detailIngredientsTextView);
        TextView quantityTextView = findViewById(R.id.detailQuantityTextView);

        titleTextView.setText(title);
        ingredientsTextView.setText(TextUtils.join(", ", ingredients));
        quantityTextView.setText(quantity);
    }
}
