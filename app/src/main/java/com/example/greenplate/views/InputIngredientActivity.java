package com.example.greenplate.views;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.viewmodels.IngredientViewModel;

public class InputIngredientActivity extends AppCompatActivity {
    final String userId = SingletonFirebase.getInstance().getFirebaseAuth().getCurrentUser()
            .getUid();
    private Button exitButton;
    private Button addIngredient;
    private EditText nameInput;
    private EditText quantityInput;
    private EditText caloriesInput;
    private EditText expirationInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_ingredient);
        exitButton = findViewById(R.id.ExitBtn);
        IngredientViewModel viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);
        viewModel.getMessageLiveData().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity, which will take the user back to the previous screen
                finish();
            }
        });
        nameInput = findViewById(R.id.ingredientNameInput);
        quantityInput = findViewById(R.id.ingredientQuantityInput);
        caloriesInput = findViewById(R.id.caloriesPerServingInput);
        expirationInput = findViewById(R.id.expirationDateInput);

        addIngredient = findViewById(R.id.addIngredientBtn); // Assuming you have a submit button
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString();
                double quantity = Double.parseDouble(quantityInput.getText().toString());
                int calories = Integer.parseInt(caloriesInput.getText().toString());
                String expirationDate = expirationInput.getText().toString();

                Ingredient ingredient = new Ingredient(name, quantity, calories, expirationDate);
                viewModel.addIngredientToPantry(userId,ingredient);
                nameInput.setText("");  // Clears the text
                quantityInput.setText("");
                caloriesInput.setText("");
                expirationInput.setText("");
            }
        });

    }

}
