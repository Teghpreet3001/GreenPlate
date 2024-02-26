package com.example.greenplate.views.mainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;

public class InputMealFragment extends Fragment {

    private InputMealViewModel mViewModel;

    public static InputMealFragment newInstance() {
        return new InputMealFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_meal, container, false);
    }

}