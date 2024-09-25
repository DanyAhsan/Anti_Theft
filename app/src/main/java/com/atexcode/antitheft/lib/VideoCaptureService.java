package com.atexcode.antitheft.lib;


    import android.app.Notification;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
    import android.content.Context;
    import android.content.Intent;
    import android.hardware.Camera;
    import android.media.CamcorderProfile;
    import android.media.MediaRecorder;
    import android.os.IBinder;
        import android.os.Build;
    import android.util.Log;

    import com.atexcode.antitheft.HomeActivity;
    import com.atexcode.antitheft.R;

    import java.io.IOException;

public class VideoCaptureService extends Service {
    private static final int FOREGROUND_SERVICE_ID = 101; // Unique notification ID
    private static final String CHANNEL_ID = "VideoCaptureChannel"; // Notification channel ID

    private Camera camera;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;



    @Override
    public void onCreate() {
        super.onCreate();
        // Set the service reference in AtexDeviceManager
        AtexDeviceManager.setVideoCaptureService(this);

        startForegroundService();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundService() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Video Capture Service")
                .setContentText("Capturing video of intruders")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(FOREGROUND_SERVICE_ID, notification);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Video Capture Channel";
            String description = "Channel for video capture service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Static methods below
//    public static void captureIntruder(Context context){
//        cam = new CameraHandler(new PictureTakenCallback() {
//            @Override
//            public void onPictureTaken(String ImagePath) {
//                AtexLocalDB db = new AtexLocalDB(context);
//                db.open();
//                db.insertLog("Captured", "Intruder Captured");
//                db.close();
//                cam.shutdown();
//                Log.e("Camera Service", "Intruder captured");
//            }
//        });
//
//        cam.takePic();
//    }

    public static void captureIntruderShorts(Context context) {

       // VideoRecorder.startRecording();
    }

//    public void startRecording(int seconds) {
//            }


}
