package com.atexcode.antitheft.lib;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.atexcode.antitheft.DeviceInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CallListenerReceiver extends BroadcastReceiver {

    private AtexLocalDB ldb;
    AtexGPS atxgps;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        ldb = new AtexLocalDB(context);
        atxgps = new AtexGPS(context);

        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference("users");

        //Declarations above

        ldb.open();
        HashMap<String, Object> dataMap = ldb.getDeviceInfo();
        ldb.close();
        boolean callListenerEnabled = (boolean) dataMap.get("call_listener");
        String callerPhone = String.valueOf(dataMap.get("block_phone_no"));

        // Register a PhoneStateListener to monitor phone state changes
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if(callListenerEnabled){
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // Phone is ringing, check if the incoming number matches the specified number
                        if (callerPhone.equals(incomingNumber)) {
                            ldb.open();
                            ldb.insertLog("Calling", callerPhone);
                            ldb.setDeviceStatus(true);
                            ldb.insertLog("Device","Status updated to stolen");
                            ldb.close();

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userId = user.getUid();
                            String userEmail = user.getEmail();

                            setDeviceInfo(userId, userEmail);

                            AtexDeviceManager.lockDeviceNow(context);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone call ended
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Phone is in a call
                        break;
                }
                }
            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void setDeviceInfo(String userId, String userEmail) {
        // Create a DeviceInfo object with initial values
        DeviceInfo deviceInfo = new DeviceInfo(userEmail, atxgps.getGPSCoordinates(), 1);
        // Set device info for the user under "devices" child
        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference().child("devices").child(userId);
        devicesRef.setValue(deviceInfo);
    }
}
