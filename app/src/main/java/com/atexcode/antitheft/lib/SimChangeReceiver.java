package com.atexcode.antitheft.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimChangeReceiver extends BroadcastReceiver {
    TelephonyManager telephonyManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.SERVICE_STATE")) {
            ServiceState serviceState = intent.getParcelableExtra("state");
            if (serviceState != null) {
                int state = serviceState.getState();
                Log.d("ServiceStateReceiver", "Service State Changed: " + state);

                // Handle service state changes here
                int simState = telephonyManager.getSimState();
                switch (simState) {
                    case TelephonyManager.SIM_STATE_ABSENT:
                        Log.d("SIM", "SIM card absent");
                        // Perform actions for absent SIM card
                        break;
                    case TelephonyManager.SIM_STATE_UNKNOWN:
                        Log.d("SIM", "SIM card state unknown");
                        // Perform actions for unknown SIM card state
                        break;
                    case TelephonyManager.SIM_STATE_READY:
                        Log.d("SIM", "Sim card ready");
                        // Add other cases as needed
                }
            }
        }
    }
}
