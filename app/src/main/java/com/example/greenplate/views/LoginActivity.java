package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenplate.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
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
            if (editTextUsername.getText() == null) {
                Toast.makeText(LoginActivity.this, "Username is invalid", Toast.LENGTH_SHORT).show();
            } else if (editTextPassword.getText() == null) {
                Toast.makeText(LoginActivity.this, "Password is invalid", Toast.LENGTH_SHORT).show();
            } else {
                String username = String.valueOf(editTextUsername.getText()).trim();
                String password = String.valueOf(editTextPassword.getText());

                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username is blank", Toast.LENGTH_SHORT).show();
                } else if (!username.contains("@") || !username.contains(".")) {
                    Toast.makeText(LoginActivity.this, "Username is not a valid email", Toast.LENGTH_SHORT).show();
                } else if (password.trim().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        exitAppButton.setOnClickListener(v ->  {
            LoginActivity.this.finishAffinity();
            System.exit(0);
        });
    }
}
