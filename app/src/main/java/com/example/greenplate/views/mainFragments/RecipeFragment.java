package com.example.greenplate.views.mainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.viewmodels.RecipeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private String recipeTitle;
    private String recipeQuantity;
    private List<String> recipeList;
    private List<String> recipeIngredients;

    private DatabaseReference recipes; // fb stands for firebase not facebook LOL ROFL #HAHAH
    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        recipes = SingletonFirebase.getInstance().getDatabaseReference()
                .child("cookbook");
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        recyclerView = rootView.findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeList = new ArrayList<>();
        List<Recipe> recipeList = new ArrayList<>();


        recipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> recipe = recipeSnapshot.getValue(genericTypeIndicator);
                    recipeTitle = (String) recipe.get("title");
                    recipeIngredients = (List<String>) recipe.get("ingredients");
                    recipeQuantity = (String) recipe.get("quantity");
                    recipeList.add(new Recipe(recipeTitle, recipeIngredients, recipeQuantity));
                    adapter = new RecipeAdapter(recipeList);
                    recyclerView.setAdapter(adapter);
                    adapter = new RecipeAdapter(recipeList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read global recipes",
                        databaseError.toException());
            }
        });

        return rootView;
    }

}