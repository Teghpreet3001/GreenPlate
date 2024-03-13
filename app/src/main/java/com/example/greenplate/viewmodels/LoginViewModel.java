package com.example.greenplate.viewmodels;

import android.text.Editable;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.SingletonFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {
    private FirebaseAuth firebaseAuth;

    public boolean handleInputData(Editable username, Editable password) {
        if (username == null) {
            return false;
        } else if (password == null) {
            return false;
        }

        String email = String.valueOf(username);
        String passwordStr = String.valueOf(password);

        if (email.isEmpty()) {
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            return false;
        } else if (passwordStr.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    public void login(Editable username, Editable password, LoginListener loginListener) {
        String email = String.valueOf(username);
        String passwordStr = String.valueOf(password);

        firebaseAuth = SingletonFirebase.getInstance().getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(email, passwordStr).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginListener.onLoginSuccess();
            } else {
                loginListener.onLoginFailure();
            }
        });
    }

    public interface LoginListener {
        void onLoginSuccess();
        void onLoginFailure();
    }
}
