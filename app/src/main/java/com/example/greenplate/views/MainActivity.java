package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import com.example.greenplate.R;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.views.mainFragments.IngredientFragment;
import com.example.greenplate.views.mainFragments.InputMealFragment;
import com.example.greenplate.views.mainFragments.RecipeFragment;
import com.example.greenplate.views.mainFragments.ShoppingListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        // Create an Intent to start SignUpActivity
        if (SingletonFirebase.getInstance().getFirebaseAuth().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            replaceFragment(new InputMealFragment());
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.inputMeal) {
                replaceFragment(new InputMealFragment());
            } else if (item.getItemId() == R.id.recipe) {
                replaceFragment(new RecipeFragment());
            } else if (item.getItemId() == R.id.ingredients) {
                replaceFragment(new IngredientFragment());
            } else if (item.getItemId() == R.id.shoppingList) {
                replaceFragment(new ShoppingListFragment());
            } else {
                replaceFragment(new ProfileFragment());
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
