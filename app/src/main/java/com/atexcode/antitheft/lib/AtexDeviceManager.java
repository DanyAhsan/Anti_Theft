package com.atexcode.antitheft.lib;

import static androidx.core.content.ContextCompat.startForegroundService;

import com.atexcode.antitheft.HomeActivity;
import com.atexcode.antitheft.lib.VideoCaptureService;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


public class AtexDeviceManager extends DeviceAdminReceiver {
    private CameraHandler cam;

    private static VideoCaptureService videoCaptureService;

    public static void setVideoCaptureService(VideoCaptureService service) {
        videoCaptureService = service;
    }



    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context,"Device Admin Enabled",Toast.LENGTH_SHORT).show();
        Log.i("loginWatch","Device Admin Enabled");
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context,"Device Admin Disabled",Toast.LENGTH_SHORT).show();
        Log.i("loginWatch","Device Admin Disabled");
    }

    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordFailed(context, intent);


        AtexLocalDB db = new AtexLocalDB(context);
        db.open();
        db.insertLog("Intruder", "Someone tried to unlock device");

        //VideoCaptureService.captureIntruder(context);
        try{
            //videoCaptureService.startRecording(5);
            Log.d("Video Captured","Look like video has captured");
        }
        catch (Exception e){
            Log.d("Video Error", "Error: "+e.getMessage());
        }

        cam = new CameraHandler(new PictureTakenCallback() {
            @Override
            public void onPictureTaken(String ImagePath) {
                db.insertLog("Captured", "Intruder Captured");
                db.close();
                cam.shutdown();
            }
        });
        cam.takePic();
    }

    public static void lockDeviceNow(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (devicePolicyManager != null) {
            AtexLocalDB db = new AtexLocalDB(context);
            db.open();
            db.insertLog("Call Received", "Phone number detected");

            db.insertLog("Lock Now", "Device has been locked");
            db.close();
            devicePolicyManager.lockNow();

        }

    }
}
