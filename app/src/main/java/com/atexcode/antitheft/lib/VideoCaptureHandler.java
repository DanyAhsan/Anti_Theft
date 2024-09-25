package com.atexcode.antitheft.lib;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.io.File;

public class VideoCaptureHandler {

    private Context context;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private MediaRecorder mediaRecorder;
    private VideoTakenCallback videoTakenCallback;
    private TextureView previewTextureView;
    private File fileReturned;

    public VideoCaptureHandler(Context context, VideoTakenCallback callback) {
        this.context = context;
        this.videoTakenCallback = callback;
        startBackgroundThread();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("VideoCaptureThread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    public void takeVideo(int seconds, TextureView previewTextureView) {
        this.previewTextureView = previewTextureView;
        // Start video recording
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                configureMediaRecorder(seconds);
                prepareMediaRecorder();

                previewTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

                    @Override
                    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                        mediaRecorder.setPreviewDisplay(new Surface(surface));
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            Log.d("Video","Started....");
                            Thread.sleep(10 * 1000);
                            Log.d("Video","After 10 seconds, stopping...");
                            mediaRecorder.stop();
                            mediaRecorder.reset();
                            mediaRecorder.release();
                            closeCamera();
                            if (videoTakenCallback != null) {
                                videoTakenCallback.onVideoTaken(fileReturned.getPath());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            closeCamera();
                            if (mediaRecorder != null) {
                                mediaRecorder.reset();
                                mediaRecorder.release();
                            }
                        }
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                    }
                });
            }
        });
    }

    private void configureMediaRecorder(int seconds) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setMaxDuration(seconds * 1000);

        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        String directoryName = "AtexAntiTheft";
        File directory = new File(context.getExternalFilesDir(null), directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File videoFile = new File(directory, fileName);
        fileReturned = videoFile;
        mediaRecorder.setOutputFile(videoFile.getPath());
    }

    private void prepareMediaRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                mediaRecorder.reset();
                mediaRecorder.release();
            }
        }
    }

    public void closeCamera() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
