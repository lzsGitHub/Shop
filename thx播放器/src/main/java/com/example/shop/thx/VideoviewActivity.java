package com.example.shop.thx;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.VideoView;

public class VideoviewActivity extends AppCompatActivity {
    private String path= "file:///"+ Environment.getExternalStorageDirectory().getAbsolutePath().substring(0,8)+"/extSdCard/test3.mp4";

    private VideoView mPlayVideoview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        mPlayVideoview = (VideoView) findViewById(R.id.play_videoview);
        mPlayVideoview.setVideoPath(path);
    }

    public void go1(View v) {
            mPlayVideoview.start();

    }

    public void go2(View v) {


    }

    public void go3(View v) {


    }

    public void go4(View v) {


    }
}
