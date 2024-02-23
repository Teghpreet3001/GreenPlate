package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.example.greenplate.R;
import com.example.greenplate.views.mainFragments.IngredientFragment;
import com.example.greenplate.views.mainFragments.InputMealFragment;
import com.example.greenplate.views.mainFragments.RecipeFragment;
import com.example.greenplate.views.mainFragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        // this code written by kabu to redirect to sign up page will be removed later (i think)
      
        // Create an Intent to start SignUpActivity
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);

        // Optional: If you don't want users to return to MainActivity by pressing back,
        // call finish() after starting the activity
        finish();
      
        // the following code is written by rachit

        replaceFragment(new InputMealFragment());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.inputMeal) {
                replaceFragment(new InputMealFragment());
            } else if (item.getItemId() == R.id.recipe) {
                replaceFragment(new RecipeFragment());
            }
            else if (item.getItemId() == R.id.ingredients) {
                replaceFragment(new IngredientFragment());
            } else {
                replaceFragment(new ShoppingListFragment());
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.commit();
    }
}
