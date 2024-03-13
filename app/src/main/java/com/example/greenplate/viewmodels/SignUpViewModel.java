package com.example.greenplate.viewmodels;

import android.text.Editable;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpViewModel extends ViewModel {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static final String defaultGender = "Enter your Gender";
    public static final String defaultHeight = "Enter your Height (in inches)";
    public static final String defaultWeight = "Enter your Weight (in lbs)";
    public static final String defaultAge = "Enter your Age";

    public boolean handleInputData(Editable username,
                                   Editable password, Editable firstName, Editable lastName) {
        if (username == null) {
            return false;
        } else if (password == null) {
            return false;
        } else if (firstName == null) {
            return false;
        } else if (lastName == null) {
            return false;
        }

        String email = String.valueOf(username);
        String passwordStr = String.valueOf(password);
        String firstNameStr = String.valueOf(firstName).trim();
        String lastNameStr = String.valueOf(lastName).trim();

        if (email.isEmpty()) {
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            return false;
        } else if (passwordStr.trim().isEmpty()) {
            return false;
        } else if (firstNameStr.isEmpty()) {
            return false;
        } else if (lastNameStr.isEmpty()) {
            return false;
        }

        return true;
    }

    public void signUp(Editable username, Editable password, CharSequence firstName, CharSequence lastName, SignUpListener signUpListener) {
        String email = String.valueOf(username);
        String passwordStr = String.valueOf(password);
        String firstNameStr = String.valueOf(firstName).trim();
        String lastNameStr = String.valueOf(lastName).trim();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth.createUserWithEmailAndPassword(email, passwordStr).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = new User(firstNameStr, lastNameStr, email, defaultHeight, defaultWeight, defaultGender, defaultAge);
                databaseReference.child("users")
                        .child(firebaseAuth.getCurrentUser()
                                .getUid()).setValue(user);
                signUpListener.onSignUpSuccess();
            } else {
                signUpListener.onSignUpFailure();
            }
        });
    }

    public interface SignUpListener {
        void onSignUpSuccess();
        void onSignUpFailure();
    }
}