package com.atexcode.antitheft.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.RequiresApi;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraHandler {
    private Camera camera;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private PictureTakenCallback pictureTakenCallback;
    private VideoTakenCallback videoTakenCallback;
    private MediaRecorder mediaRecorder;

    CameraHandler(PictureTakenCallback callback) {
        this.pictureTakenCallback = callback;
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
    CameraHandler(VideoTakenCallback callback){
        this.videoTakenCallback = callback;
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
    void takePic() {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                camera = Camera.open(0);
                try {
                    camera.setPreviewTexture(new SurfaceTexture(10));
                    camera.startPreview();
//                    camera.takePicture(null, null, new Camera.PictureCallback() {
//                        @RequiresApi(api = Build.VERSION_CODES.O)
//                        @Override
//                        public void onPictureTaken(byte[] data, Camera camera) {
//                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                            byte[] byteArray = byteArrayOutputStream.toByteArray();
//                            String encodedImage = Base64.getEncoder().encodeToString(byteArray);
//
//                            // Release the camera after capturing the image successfully
//                            camera.stopPreview();
//                            camera.release();
//
//                            // Invoke the callback with the encoded image
//                            if (pictureTakenCallback != null) {
//                                pictureTakenCallback.onPictureTaken(encodedImage);
//                            }
//                        }
//                    });
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();

                            // Save the image to the gallery
                            String fileName = "image_" + System.currentTimeMillis() + ".png";
                            String directoryName = "AtexAntiTheft";
                            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName);

                            if (!directory.exists()) {
                                directory.mkdirs(); // Create the directory if it doesn't exist
                            }

                            File imageFile = new File(directory, fileName);
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                                fileOutputStream.write(byteArray);
                                fileOutputStream.close();

                                // Refresh the gallery to make the image visible
                                //MediaScannerConnection.scanFile( new String[]{imageFile.getPath()}, null, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Release the camera after capturing the image successfully
                            if (camera != null) {
                                camera.stopPreview();
                                camera.release();

                            };


                            // Invoke the callback with the image file path
                            if (pictureTakenCallback != null) {

                                pictureTakenCallback.onPictureTaken(imageFile.getPath());
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    if (camera != null) {

                        camera.release();
                    }
                }
            }
        });

    }

    //Video Recording
    // Video Recording
    void takeVideo(int seconds) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                camera = null;
                mediaRecorder = null;
                SurfaceTexture surfaceTexture = new SurfaceTexture(10);

                try {
                    camera = Camera.open(0);
                    mediaRecorder = new MediaRecorder();

                    camera.setPreviewTexture(surfaceTexture);
                    camera.startPreview();

                    // Set the audio source (e.g., MIC) before setting the audio encoder
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                    // Set the video source and other media recorder settings
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
//                    mediaRecorder.setMaxDuration(10 * 1000);

                    // Set the audio encoder
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                    // Set the output file path for the video
                    String fileName = "video_" + System.currentTimeMillis() + ".mp4";
                    String directoryName = "AtexAntiTheft";
                    File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), directoryName);

                    if (!directory.exists()) {
                        directory.mkdirs(); // Create the directory if it doesn't exist
                    }

                    File videoFile = new File(directory, fileName);

                    // Set the output file path for the video
                    mediaRecorder.setOutputFile(videoFile.getPath());

                    // Prepare and start recording with some delay (e.g., 1000ms)
                    try {
                        mediaRecorder.prepare();
                        Thread.sleep(1000);  // Add a delay to allow initialization
                        mediaRecorder.start();

                        // Record video for the specified duration
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        Log.e("Debug 1", "Error: "+e.getMessage());
                        e.printStackTrace();
                    } finally {
                        // Stop and release media recorder
                        if (mediaRecorder != null) {
                            try {
                                mediaRecorder.stop();
                            } catch (Exception e) {
                                Log.e("Debug 2","Error: "+ e.getMessage());
                                e.printStackTrace();
                            }
                            mediaRecorder.reset();
                            mediaRecorder.release();
                        }

                        // Release the camera after recording the video successfully
                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                        }

                        // Invoke the callback with the video file path
                        if (videoTakenCallback != null) {
                            videoTakenCallback.onVideoTaken(videoFile.getPath());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Debug 3", "Error: "+e.getMessage());
                    // Handle any exceptions and release resources
                    if (camera != null) {
                        camera.release();
                    }
                    if (mediaRecorder != null) {
                        mediaRecorder.reset();
                        mediaRecorder.release();
                    }
                }
            }
        });
    }


    void shutdown() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
