package com.atexcode.antitheft;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.atexcode.antitheft.lib.AtexLocalDB;
import com.atexcode.antitheft.lib.AtexStyling;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;

public class IntrudersActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private AtexLocalDB ldb;
    private AtexStyling atexStyling;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intruders);

        ldb = new AtexLocalDB(this);
        atexStyling = new AtexStyling();

        //Intruder Photos
        TextView noImageText = findViewById(R.id.noImageText);
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "AtexAntiTheft";
        LinearLayout galleryLayout = findViewById(R.id.firstGalleryLayout);
        File directory = new File(directoryPath);
        File[] imageFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png") || name.endsWith(".jpg");
            }
        });
        if (imageFiles != null && imageFiles.length > 0) {
            noImageText.setVisibility(View.GONE);
            for (File imageFile : imageFiles) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.intruder_image_width),
                        getResources().getDimensionPixelSize(R.dimen.intruder_image_height)
                ));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(Uri.fromFile(imageFile));
                galleryLayout.addView(imageView);
            }
        }
        //Intruder Photos END


        //Intruder Videos
        TextView noVideoText = findViewById(R.id.noVideoText);
        String directoryPathVideo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + "AtexAntiTheft";
        LinearLayout galleryLayoutVideo = findViewById(R.id.firstGalleryLayout);
        File directoryVideo = new File(directoryPath);
        File[] videoFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp4") || name.endsWith(".mpeg");
            }
        });
        if (videoFiles != null && videoFiles.length > 0) {
            noVideoText.setVisibility(View.GONE);
            for (File VideoFile : videoFiles) {
                VideoView VideoView = new VideoView(this);
                VideoView.setLayoutParams(new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.intruder_image_width),
                        getResources().getDimensionPixelSize(R.dimen.intruder_image_height)
                ));
                VideoView.setVideoURI(Uri.fromFile(VideoFile));
                galleryLayoutVideo.addView(VideoView);
            }
        }

        //Intruder Videos END

    }


    //Other Methods to display logs in table


}
