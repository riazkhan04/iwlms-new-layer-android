package com.orsac.android.pccfwildlife.CameraClass;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.VideoMessageGajaBandhuActivity;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 12/4/21 in com.cnc3camera.
 */
public class PhotoVideoRedirectActivity extends AppCompatActivity {

    TextView ok_txt,cancel_txt;

    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovideo_redirect);


        init();

    }
    VideoView videoView;
    private void init() {

        ImageView imgShow = findViewById(R.id.imgShow);
        videoView = findViewById(R.id.vidShow);
        ok_txt = findViewById(R.id.ok_txt);
        cancel_txt = findViewById(R.id.cancel_txt);

        if (!getIntent().getStringExtra("PATH").equalsIgnoreCase("")) {
            VideoMessageGajaBandhuActivity.videoPath = getIntent().getStringExtra("PATH");
        }
        if(getIntent().getStringExtra("WHO").equalsIgnoreCase("Image")){

            imgShow.setVisibility(View.VISIBLE);

            Glide.with(PhotoVideoRedirectActivity.this)
                    .load(getIntent().getStringExtra("PATH"))
                    .placeholder(R.drawable.ic_menu_camera)
                    .into(imgShow);
        }else {

            videoView.setVisibility(View.VISIBLE);
            try {
                videoView.setMediaController(null);
                videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("PATH")));

            } catch (Exception e){
                e.printStackTrace();
            }
            videoView.requestFocus();
            //videoView.setZOrderOnTop(true);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {

                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.pause();
                }
            });
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    videoView.start();
                }
            });

            ok_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoMessageGajaBandhuActivity.videoPath = getIntent().getStringExtra("PATH");
                    finish();
                }
            });
            cancel_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }



    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        super.onBackPressed();
    }


}
