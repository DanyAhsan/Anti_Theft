package com.atexcode.antitheft;

import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.atexcode.antitheft.lib.AtexDeviceManager;
import com.atexcode.antitheft.lib.AtexLocalDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TrackActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName dpmCom;
    private boolean adminEnabled;
    DeviceInfo deviceInfoRemote;

    private AtexLocalDB ldb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        ldb = new AtexLocalDB(this);




        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching info...");
        progressDialog.setCancelable(false);

        Button fetchInfoButton = findViewById(R.id.buttonfetchInfo);
        fetchInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchInfo();
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
    private void fetchInfo() {

            handleProgressDialog(true);
            EditText email = findViewById(R.id.editTextEmail);
            findDeviceByEmail(email.getText().toString());
            handleProgressDialog(false);
        }

    private void findDeviceByEmail(String userEmail) {

        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference().child("devices");

        devicesRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Device found
                    for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                        DeviceInfo deviceInfo = deviceSnapshot.getValue(DeviceInfo.class);
                        if (deviceInfo != null) {
                            deviceInfoRemote = deviceInfo;
                            // Handle the found device information
                            String status = String.valueOf(deviceInfo.getStatus());
                            String location = deviceInfo.getLocation();

                            EditText deviceStatusField = findViewById(R.id.editDeviceStatus);
                            EditText deviceLocationField = findViewById(R.id.editDeviceLocation);


                            if (deviceInfo.getStatus() == 1) {
                                deviceStatusField.setText("Stolen");
                                deviceStatusField.setTextColor(getResources().getColor(R.color.stolenColor)); // Set your stolen color
                            } else {
                                deviceStatusField.setText("Normal");
                                deviceStatusField.setTextColor(getResources().getColor(R.color.normalStatusColor)); // Set your normal color

                            }
                            deviceLocationField.setText(deviceInfo.getLocation());
                            deviceStatusField.requestFocus();

                            // Do something with status and location
                            Toast.makeText(TrackActivity.this, "Device found: Email: "+ deviceInfo.getEmail() +",Status - " + status + ", Location - " + location, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Device not found
                    Toast.makeText(TrackActivity.this, "Device not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TrackActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
