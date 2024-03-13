package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private SignUpViewModel signUpViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView loginTextView = findViewById(R.id.textViewLoginLink);
        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.registerButton);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);

        registerButton.setOnClickListener(v -> {
            if (!signUpViewModel.handleInputData(editTextUsername.getText(),
                    editTextPassword.getText(), editTextFirstName.getText(),
                    editTextLastName.getText())) {
                Toast.makeText(SignUpActivity.this, "Inputted details are invalid", Toast.LENGTH_SHORT).show();
            } else {
                signUpViewModel.signUp(editTextUsername.getText(),
                        editTextPassword.getText(), editTextFirstName.getText(),
                        editTextLastName.getText(), new SignUpViewModel.SignUpListener() {
                            @Override
                            public void onSignUpSuccess() {
                                Intent intent = new Intent(SignUpActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onSignUpFailure() {
                                Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
    }
}