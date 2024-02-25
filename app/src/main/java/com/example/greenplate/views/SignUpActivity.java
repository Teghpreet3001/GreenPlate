package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.greenplate.R;
import com.example.greenplate.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView editTextFirstName;
    private TextView editTextLastName;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
            if (editTextUsername.getText() == null) {
                Toast.makeText(SignUpActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
            } else if (editTextPassword.getText() == null) {
                Toast.makeText(SignUpActivity.this, "Password is invalid", Toast.LENGTH_SHORT).show();
            } else if (editTextFirstName.getText() == null) {
                Toast.makeText(SignUpActivity.this, "First name is invalid", Toast.LENGTH_SHORT).show();
            } else if (editTextLastName.getText() == null) {
                Toast.makeText(SignUpActivity.this, "Last name is invalid", Toast.LENGTH_SHORT).show();
            } else {
                String email = String.valueOf(editTextUsername.getText()).trim();
                String password = String.valueOf(editTextPassword.getText());
                String firstName = String.valueOf(editTextFirstName.getText()).trim();
                String lastName = String.valueOf(editTextLastName.getText()).trim();

                if (email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Email field is empty", Toast.LENGTH_SHORT).show();
                } else if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(SignUpActivity.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                } else if (password.trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                } else if (firstName.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "First name field is empty", Toast.LENGTH_SHORT).show();
                } else if (lastName.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Last name field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    User user = new User(firstName, lastName, email);
                                    databaseReference.child("users")
                                            .child(firebaseAuth.getCurrentUser().getUid()).setValue(user);

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}