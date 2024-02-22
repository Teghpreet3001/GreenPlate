package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.greenplate.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an Intent to start SignUpActivity
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

        // Optional: If you don't want users to return to MainActivity by pressing back,
        // call finish() after starting the activity
        finish();
    }
}
