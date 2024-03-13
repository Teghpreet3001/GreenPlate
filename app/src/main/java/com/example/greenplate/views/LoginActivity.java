package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        editTextUsername = findViewById(R.id.editTextUsernameLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        Button loginButton = findViewById(R.id.loginButton);
        Button exitAppButton = findViewById(R.id.buttonExitApp);

        TextView signUpText = findViewById(R.id.textViewSignUpLink);
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            if (!loginViewModel.handleInputData(editTextUsername.getText(),
                    editTextPassword.getText())) {
                Toast.makeText(LoginActivity.this,
                        "Inputted details are invalid", Toast.LENGTH_SHORT).show();
            } else {
                loginViewModel.login(editTextUsername.getText(),
                        editTextPassword.getText(),
                        new LoginViewModel.LoginListener() {
                        @Override
                        public void onLoginSuccess() {
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onLoginFailure() {
                            Toast.makeText(LoginActivity.this,
                                    "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        exitAppButton.setOnClickListener(v ->  {
            LoginActivity.this.finishAffinity();
            System.exit(0);
        });
    }
}
