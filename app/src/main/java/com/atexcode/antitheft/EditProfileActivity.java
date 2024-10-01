package com.atexcode.antitheft;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.atexcode.antitheft.lib.AtexStyling;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private Button buttonUpdateProfile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);

        // Disable editing of email field
        editTextEmail.setFocusable(false);
        editTextEmail.setClickable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Fetch user data from Firebase Realtime Database and autofill the fields
            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User userData = dataSnapshot.getValue(User.class);
                        if (userData != null) {
                            editTextName.setText(userData.getName());
                            editTextEmail.setText(userData.getEmail());
                            editTextPhone.setText(userData.getPhone());
                            editTextAddress.setText(userData.getAddress());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }

        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    private void updateUserProfile() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            //Validation
            if (name.isEmpty()) {
                editTextName.setError("Name is required");
                editTextName.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                editTextPhone.setError("Phone number is required");
                editTextPhone.requestFocus();
                return;
            }

            if (address.isEmpty()) {
                editTextAddress.setError("Address is required");
                editTextAddress.requestFocus();
                return;
            }
            if(!AtexStyling.EditTextSetLength(editTextPhone, 11, 13))
                return;
            //Validation End

            handleProgressDialog(true);
            // Update user's profile display name
            user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update user data in Firebase Realtime Database
                                String userId = user.getUid();
                                User updatedUser = new User(userId, name, user.getEmail(), phone, address);
                                databaseReference.child(userId).setValue(updatedUser);

                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                            handleProgressDialog(false);
                        }
                    });
        }
    }
}
