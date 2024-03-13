package com.example.greenplate.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingletonFirebase {
    private static SingletonFirebase singletonFirebase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private SingletonFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://greenplate-d518c-default-rtdb.firebaseio.com/").getReference();
    }

    public static SingletonFirebase getInstance() {
        if (singletonFirebase == null) {
            singletonFirebase = new SingletonFirebase();
        }

        return singletonFirebase;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
