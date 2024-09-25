package com.atexcode.antitheft;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.atexcode.antitheft.lib.AtexGPS;
import com.atexcode.antitheft.lib.AtexStyling;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private Button buttonRegister;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonRegister = findViewById(R.id.buttonRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference("users");


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });



    }
    private void handleProgressDialog(boolean flag) {
        if(flag){
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
        else{
            if (progressDialog.isShowing()) {
                progressDialog.hide();
            }
        }
    }
    private void registerUser() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering account..."); // Set message
        progressDialog.setCancelable(false);

        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();

        //Validation
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return; // Stop further processing
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return; // Stop further processing
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            editTextEmail.requestFocus();
            return; // Stop further processing
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return; // Stop further processing
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return; // Stop further processing
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return; // Stop further processing
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Address is required");
            editTextAddress.requestFocus();
            return; // Stop further processing
        }
        if(!AtexStyling.EditTextSetLength(editTextPhone, 11, 13))
            return;
        //Validation End

        handleProgressDialog(true);
        // Create a new user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            // Insert user details into Firebase Realtime Database
                            String userId = user.getUid();
                            User newUser = new User(userId, name, email, phone, address);
                            databaseReference.child(userId).setValue(newUser);

                            setDeviceInfo(userId, email);
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            // Navigate to HomeActivity or any other desired activity
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish(); // Close RegisterActivity
                        } else {
                            // Registration failed
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                        handleProgressDialog(false);
                    }

                });
    }
    private void setDeviceInfo(String userId, String userEmail) {
        AtexGPS atxgps = new AtexGPS(this);
        // Create a DeviceInfo object with initial values
        DeviceInfo deviceInfo = new DeviceInfo(userEmail, atxgps.getGPSCoordinates(), 0);
        // Set device info for the user under "devices" child
        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference().child("devices").child(userId);
        devicesRef.setValue(deviceInfo);
    }

}
