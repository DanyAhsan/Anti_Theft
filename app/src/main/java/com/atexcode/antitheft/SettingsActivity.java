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

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName dpmCom;
    private boolean adminEnabled;

    private AtexLocalDB ldb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ldb = new AtexLocalDB(this);


        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpmCom = new ComponentName(this, AtexDeviceManager.class);
        adminEnabled = devicePolicyManager.isAdminActive(dpmCom);
        ldb.open();
        HashMap<String, Object> dataMap = ldb.getDeviceInfo();
        ldb.close();



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Settings...");
        progressDialog.setCancelable(false);

        // Set values to UI elements
        CheckBox checkBoxAdminEnabled = findViewById(R.id.checkBoxAdminEnabled);
        checkBoxAdminEnabled.setChecked(adminEnabled);
        TextView textViewDeviceStatus = findViewById(R.id.textViewDeviceStatus);
        CheckBox checkBoxCallListener = findViewById(R.id.checkBoxCallListener);
        CheckBox checkBoxSimChangeAlert = findViewById(R.id.checkBoxSimChangeAlert);
        EditText editTextStolenOnAttempts = findViewById(R.id.editTextStolenOnAttempts);
        EditText editTextCallBlockPhoneNumber = findViewById(R.id.editTextCallBlockPhoneNumber);

        boolean deviceStatusVal = (boolean) dataMap.get("stolen");
        checkBoxCallListener.setChecked((boolean) dataMap.get("call_listener"));
        checkBoxSimChangeAlert.setChecked((boolean) dataMap.get("sim_change_alert"));

        editTextStolenOnAttempts.setText(String.valueOf(dataMap.get("invalid_attempts")));
        editTextCallBlockPhoneNumber.setText(String.valueOf(dataMap.get("block_phone_no")));

        if (deviceStatusVal) {
            textViewDeviceStatus.setText("Stolen");
            textViewDeviceStatus.setTextColor(getResources().getColor(R.color.stolenColor)); // Set your stolen color
        } else {
            textViewDeviceStatus.setText("Normal");
            textViewDeviceStatus.setTextColor(getResources().getColor(R.color.normalStatusColor)); // Set your normal color
        }


        Button updateButton = findViewById(R.id.buttonSetSettings);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
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
    private void updateSettings() {

            handleProgressDialog(true);
            // Update user's profile display name

            CheckBox checkBoxAdminEnabled = findViewById(R.id.checkBoxAdminEnabled);
            CheckBox checkBoxCallListener = findViewById(R.id.checkBoxCallListener);
            CheckBox checkBoxSimChangeAlert = findViewById(R.id.checkBoxSimChangeAlert);
            EditText editTextStolenOnAttempts = findViewById(R.id.editTextStolenOnAttempts);
            EditText editTextCallBlockPhoneNumber = findViewById(R.id.editTextCallBlockPhoneNumber);

            // Convert UI values to appropriate types
             boolean callListener = checkBoxCallListener.isChecked();
            boolean simChangeAlert = checkBoxSimChangeAlert.isChecked();
            int invalidAttempts = Integer.parseInt(editTextStolenOnAttempts.getText().toString());
            String blockPhoneNumber = editTextCallBlockPhoneNumber.getText().toString();

            // Call the updateDeviceInfo method
            ldb.open();
            boolean updateResult = ldb.updateSettings(invalidAttempts, blockPhoneNumber, callListener, simChangeAlert);
            ldb.close();

            // Handle the result if needed
            if (updateResult) {
                Toast.makeText(SettingsActivity.this, "Settings updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to update settings", Toast.LENGTH_SHORT).show();
            }
            handleProgressDialog(false);
        }
}
