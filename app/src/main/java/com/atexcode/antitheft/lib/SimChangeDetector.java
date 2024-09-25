package com.atexcode.antitheft.lib;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimChangeDetector {

    private static final String TAG = "SimChangeDetector";

    public void startListening(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            telephonyManager.listen(new PhoneStateListener() {
                @Override
                public void onServiceStateChanged(android.telephony.ServiceState serviceState) {
                    super.onServiceStateChanged(serviceState);

                    Log.d(TAG, "Service State Changed");
                    // Handle SIM card state changes here
                    int simState = telephonyManager.getSimState();
                    switch (simState) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                            Log.d(TAG, "SIM card absent");
                            // Perform actions for absent SIM card
                            break;
                        case TelephonyManager.SIM_STATE_UNKNOWN:
                            Log.d(TAG, "SIM card state unknown");
                            // Perform actions for unknown SIM card state
                            break;
                        case TelephonyManager.SIM_STATE_READY:
                            Log.d(TAG, "Sim card ready");
                        // Add other cases as needed
                    }
                }
            }, PhoneStateListener.LISTEN_SERVICE_STATE);
        }
    }

    public void stopListening(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE);
        }
    }
}
