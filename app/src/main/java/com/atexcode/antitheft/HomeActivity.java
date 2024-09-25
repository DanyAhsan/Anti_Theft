package com.atexcode.antitheft;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.atexcode.antitheft.lib.AtexDeviceManager;
import com.atexcode.antitheft.lib.AtexGPS;
import com.atexcode.antitheft.lib.AtexLocalDB;
import com.atexcode.antitheft.lib.VideoCaptureHandler;
import com.atexcode.antitheft.lib.VideoCaptureService;
import com.atexcode.antitheft.lib.VideoRecorder;
import com.atexcode.antitheft.lib.VideoTakenCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 100;
//    private String[] requiredPermissions = {
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.CAMERA,
//            Manifest.permission.INTERNET,
//            Manifest.permission.FOREGROUND_SERVICE,
//            Manifest.permission.VIBRATE,
//            Manifest.permission.WAKE_LOCK,
//            Manifest.permission.RECEIVE_BOOT_COMPLETED,
//            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO
//    };

    private String[] requiredPermissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };




    private DevicePolicyManager devicePolicyManager;
    private ComponentName dpmCom;
    private boolean adminEnabled;

    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewPhone;
    private TextView textViewAddress;
    private Button buttonEditProfile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    //Create DB
    private AtexLocalDB ldb;


    private boolean doubleBackToExitPressedOnce = false;

    private String ldb_logs  = "";


    protected void onResume() {
        super.onResume();
        adminEnabled = devicePolicyManager.isAdminActive(dpmCom);

        if(adminEnabled){
           Toast.makeText(this,"Admin Status Enabled: "+adminEnabled,Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(this,"Admin Status Disabled: "+adminEnabled,Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"Admin Privilege Needed",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        AtexGPS atxgps = new AtexGPS(this);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        dpmCom = new ComponentName(this, AtexDeviceManager.class);

        ldb = new AtexLocalDB(this);

        ldb.open();
        ldb.insertLog("Open","App Opened!");


        adminEnabled = ldb.getAdminStatus();
        ldb.close();

        checkAndRequestPermissions();

//        textViewName = findViewById(R.id.textViewName);
//        textViewPhone = findViewById(R.id.textViewDeviceId);
        //textViewEmail = findViewById(R.id.textViewEmail);
        //textViewPhone = findViewById(R.id.textViewPhone);
        //textViewAddress = findViewById(R.id.textViewAddress);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Already enabled in RealtimeApp.java
        databaseReference = firebaseDatabase.getReference("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Retrieve user data from Firebase Realtime Database
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (userData != null) {
//                        textViewName.setText("Name: " + userData.getName());
//                        //textViewEmail.setText("Email: " + userData.getEmail());
//                        textViewPhone.setText("Mobile: " + userData.getPhone());
                        //textViewAddress.setText("Address: " + userData.getAddress());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Errors here to be handled
                }
            });
        }

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = "Device admin status: "+ adminEnabled;
                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_LONG).show();

                enableDisableDeviceAdmin();
                // Navigate to the EditProfileActivity or any desired activity
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
            }
        });


        //Button Track
        FrameLayout buttonTrackDevice = findViewById(R.id.buttonTrackDevice);

        buttonTrackDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent TrackerIntent = new Intent(HomeActivity.this, TrackActivity.class);
                startActivity(TrackerIntent);
            }

        });

        //Button Intruders
        FrameLayout buttonIntruders = findViewById(R.id.buttonIntruders);
        buttonIntruders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent videoService = new Intent(HomeActivity.this, VideoCaptureService.class);
                //old: startService(videoService);
                //startForegroundService(videoService);
                Intent intruders = new Intent(HomeActivity.this, IntrudersActivity.class);
                startActivity(intruders);
            }
        });
        //Button Logs
        FrameLayout buttonLogs = findViewById(R.id.buttonLogs);

        buttonLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent logsIntent = new Intent(HomeActivity.this, LogsActivity.class);
                    startActivity(logsIntent);
                }

        });


        //Button Settings
        FrameLayout buttonSettings = findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }

        });

    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // Delay for 2 seconds to reset the flag
    }

    private void enableDisableDeviceAdmin(){

        if(!adminEnabled){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, dpmCom);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin Access Needed To Check If Incorrect PIN Entered");
            startActivityForResult(intent,3 );

            adminEnabled = devicePolicyManager.isAdminActive(dpmCom);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 10023:

            case 3:
                if (resultCode == RESULT_OK)
                    Toast.makeText(this, "Admin Privilege Granted", Toast.LENGTH_SHORT);
                else
                    Toast.makeText(this, "Failed to Enable Admin Privilege", Toast.LENGTH_SHORT).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //Taking Permission methods below
    private void checkAndRequestPermissions() {
        // Check if the permissions are already granted
        boolean allPermissionsGranted = true;
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 10023);
        }


        if (!allPermissionsGranted) {
            // Request the permissions
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS);
        } else {
            // All permissions are already granted

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions are granted, perform your operations here
            } else {
                // At least one permission is denied, handle it gracefully
                Toast.makeText(this, "Some permissions are denied. App functionality may be limited.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




}
