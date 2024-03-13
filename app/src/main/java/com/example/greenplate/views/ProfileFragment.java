package com.example.greenplate.views;


import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenplate.models.SingletonFirebase;
import com.example.greenplate.viewmodels.ProfileViewModel;
import com.example.greenplate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView uFirstName;
    private TextView uLastName;
    private DatabaseReference dbFirstName;
    private DatabaseReference dbLastName;
    private DatabaseReference dbUserEmail;

    private DatabaseReference dbUserHeight;

    private DatabaseReference dbUserWeight;
    private DatabaseReference dbUserGender;

    private DatabaseReference dbUserAge;
    private ProfileViewModel mViewModel;

    private TextInputEditText uEmail;

    private TextInputEditText uHeight;

    private TextInputEditText uWeight;

    private TextInputEditText uGender;

    private TextInputEditText uAge;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        /*Logic for Inflating EditText values*/
        final String userId = SingletonFirebase.getInstance()
                .getFirebaseAuth().getCurrentUser().getUid();
        uFirstName = (TextView) v.findViewById(R.id.userFirstName);
        uLastName = (TextView) v.findViewById(R.id.userLastName);
        uEmail = (TextInputEditText) v.findViewById(R.id.userEmail);
        uHeight = (TextInputEditText) v.findViewById(R.id.height);
        uWeight = (TextInputEditText) v.findViewById(R.id.weight);
        uGender = (TextInputEditText) v.findViewById(R.id.gender);
        uAge = (TextInputEditText) v.findViewById(R.id.age);

        dbFirstName = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("firstName");
        dbLastName = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("lastName");
        dbUserEmail = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("email");
        dbUserHeight = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("height");
        dbUserWeight = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("weight");
        dbUserGender = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("gender");
        dbUserAge = SingletonFirebase.getInstance().getDatabaseReference()
                .child("users").child(userId).child("age");


        dbFirstName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userFirstName = dataSnapshot.getValue(String.class);
                    uFirstName.setText(userFirstName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        dbFirstName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userFirstName = dataSnapshot.getValue(String.class);
                    uFirstName.setText(userFirstName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        dbLastName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userLastName = dataSnapshot.getValue(String.class);
                    uLastName.setText(userLastName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        // Add a ValueEventListener to retrieve the value of dbUserEmail
        dbUserEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userEmail = dataSnapshot.getValue(String.class);
                    uEmail.setText(userEmail);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        dbUserHeight.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userHeight = dataSnapshot.getValue(String.class);
                    uHeight.setText(userHeight);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        dbUserWeight.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userWeight = dataSnapshot.getValue(String.class);
                    uWeight.setText(userWeight);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        dbUserGender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userGender = dataSnapshot.getValue(String.class);
                    uGender.setText(userGender);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        dbUserAge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userAge = dataSnapshot.getValue(String.class);
                    uAge.setText(userAge);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to read user email value.",
                        databaseError.toException());
            }
        });

        Button saveInfoBtn = v.findViewById(R.id.saveInfoBtn);

        saveInfoBtn.setOnClickListener(v_2 -> {
            // Save the updated values to the database
            dbUserEmail.setValue(uEmail.getText().toString())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Profile Fragment", e.toString());
                        }
                    });

            dbUserHeight.setValue(uHeight.getText().toString());
            dbUserWeight.setValue(uWeight.getText().toString());
            dbUserGender.setValue(uGender.getText().toString());
            dbUserAge.setValue(uAge.getText().toString());
            Toast.makeText(getActivity(), "Data updated!", Toast.LENGTH_LONG).show();
        });

        Button signOutBtn = v.findViewById(R.id.signOutBtn);

        signOutBtn.setOnClickListener(v_1 -> {
            SingletonFirebase.getInstance().getFirebaseAuth().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }






}