package com.example.greenplate.views.mainFragments;

import android.content.Intent;
import android.os.Bundle;
import com.example.greenplate.views.InputIngredientActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.greenplate.R;

public class IngredientFragment extends Fragment {

    private Button addIngredientButton;
    private Button exitButton;

    public static IngredientFragment newInstance() {
        return new IngredientFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        // Initialize the add ingredient button
        addIngredientButton = view.findViewById(R.id.addIngredientButton);
         // If you want to do something with the exit button

        // Set up the button click listener
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddIngredientActivity
                Intent intent = new Intent(getActivity(), InputIngredientActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }
}
