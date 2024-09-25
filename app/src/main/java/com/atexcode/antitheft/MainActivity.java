package com.atexcode.antitheft;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.atexcode.antitheft.lib.AtexGPS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);



        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // If the user is not logged in, check Firebase Authentication
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            // If the user is logged in with Firebase, navigate to HomeActivity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
            // If the user is not logged in with Firebase, start LoginActivity
            startActivity(loginIntent);
            finish();

        }

    }

}
