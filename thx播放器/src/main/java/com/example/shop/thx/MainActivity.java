package com.example.shop.thx;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    //private String path= "file:///"+Environment.getExternalStorageDirectory().getAbsolutePath().substring(0,8)+"/extSdCard/onepunchman.mp4";
    private String path = "file:///" + Environment.getExternalStorageDirectory().getAbsolutePath().substring(0, 8) + "/extSdCard/test3.mp4";
    //    private String path="file:///"+Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/test.mp4";
    //做呈现
    private SurfaceView mVideoPlayer;
    //做后台控制
    private MediaPlayer mMediaPlayer;
    //给SV做设置
    private SurfaceHolder mSurfaceHolder;
    private int mPlaypos = 0;
    private CheckBox mCbPr;
    private TextView mTvNowTime;
    private SeekBar mSeekbarProgress;
    private TextView mTvTotalTime;
    private CheckBox mCbScreen;
    private LinearLayout bottomLlActivity;
    public static long count;
    public static long count1;
    public static long firClick;
    public static long secClick;


    private LayoutActivity mLayoutActivity;
    private AudioBroad mAudioBroad;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mTvNowTime.setText(UtilTimeLimit.format(mMediaPlayer.getDuration()));
                    //每隔一秒设置一下当前进度----（当前媒体的播放位置）
                    mSeekbarProgress.setProgress(mMediaPlayer.getCurrentPosition());
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    break;
//                case 2:
//                    bottomLlActivity.setVisibility(View.GONE);
//                    mHandler.sendEmptyMessageDelayed(2,3000);
//                    break;
                default:
                    break;
            }
        }
    };
    private RelativeLayout relActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mVideoPlayer = (SurfaceView) findViewById(R.id.video_player);
        mSurfaceHolder = mVideoPlayer.getHolder();
        relActivity = (RelativeLayout) findViewById(R.id.rel_activity);
        mCbPr = (CheckBox) findViewById(R.id.cb_pr);
        mTvNowTime = (TextView) findViewById(R.id.tv_now_time);
        mTvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        mSeekbarProgress = (SeekBar) findViewById(R.id.seekbar_progress);
        bottomLlActivity = (LinearLayout) findViewById(R.id.bottom_ll_activity);

        //加手势，进行实例化：
        mLayoutActivity = (LayoutActivity) findViewById(R.id.layout_activity);
        mAudioBroad = new AudioBroad();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        this.registerReceiver(mAudioBroad,intentFilter);

        mCbScreen = (CheckBox) findViewById(R.id.cb_screen);

//        兼容2.3及以下版本，否则只有声音没有画面
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        mCbPr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mHandler.removeMessages(1);
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.start();
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        });
        mSeekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //手动拖动进度条的时候当前时间需要改变
                mTvNowTime.setText(UtilTimeLimit.format(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
                mMediaPlayer.seekTo(mSeekbarProgress.getProgress());
            }
        });
        mLayoutActivity.setOnLightChangeListener(new LayoutActivity.OnLightChangeListener() {
            @Override
            public void onLightChange(float lightNow) {

            }
        });
        mLayoutActivity.setOnTimeChangeListener(new LayoutActivity.OnTimeChangeListener() {
            @Override
            public void OnTimeChange(int timeTotalChange, boolean isLastConfirm) {
                if (isLastConfirm) {
                    int Now = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.seekTo(Now+timeTotalChange);
                }

            }
        });
        mCbScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //横屏
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    //竖屏
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
//        relActivity.setOnClickListener(this);


        relActivity.setOnTouchListener(new View.OnTouchListener() {

            boolean cbisChecked = true;
            boolean singleChecked = true;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    count++;
                    count1++;
                    if (count1 == 1) {
                        firClick = System.currentTimeMillis();
                        if (singleChecked) {
                            bottomLlActivity.setVisibility(View.GONE);
                            singleChecked = false;
                        } else {
                            bottomLlActivity.setVisibility(View.VISIBLE);
                            singleChecked = true;

                        }
                        count1 = 0;
                    }
                    if (count == 2) {
                        secClick = System.currentTimeMillis();
                        if (secClick - firClick < 500) {
                            if (cbisChecked) {
                                mCbPr.setChecked(true);
                                cbisChecked = false;
                            } else {
                                mCbPr.setChecked(false);
                                cbisChecked = true;
                            }

                        }
                        count = 0;
                        firClick = 0;
                        secClick = 0;
                    }

                }
                return true;
            }
        });

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//       int Action=event.getAction();
//        if (Action==1) {
//            bottomLlActivity.setVisibility(View.GONE);
//        }
//        return true;
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("执行到了横屏设置");
            //横屏设置布局参数占满
            ViewGroup.LayoutParams layoutParams = relActivity.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            relActivity.setLayoutParams(layoutParams);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            System.out.println(layoutParams.height + "____________________");
        } else {
            //竖屏高度设置布局参数为250dp
            ViewGroup.LayoutParams layoutParams = relActivity.getLayoutParams();
            layoutParams.height = DensityUtil.dip2px(this, 250);
            relActivity.setLayoutParams(layoutParams);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        resizeSv();
    }

    private void initMp() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(this, Uri.parse(path));//:设置播放数据源http:// file:/// android_assetes://R.raw.xxx
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setLooping(true);// 循环播放
            mMediaPlayer.setOnPreparedListener(this); //解析视频回调监听
            mMediaPlayer.prepareAsync();//解析视频，准备播放
            mMediaPlayer.setDisplay(mSurfaceHolder);  //置在哪个surfaceview上播放

        }
    }

    private void stopMP(int pos) {
        mHandler.removeMessages(1);
        mPlaypos = pos;
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        System.gc();
    }

    @Override//开始播视频
    public void surfaceCreated(SurfaceHolder holder) {
        initMp();
//        mMediaPlayer .start() //开始播放
//
//        mMediaPlayer.pause() 暂停播放
//
//        mMediaPlayer.stop() 停止播放，有的机型不能达到效果
//
//        mMediaPlayer.seekTo() 跳到指定播放位置，会有偏差
//
//        mMediaPlayer.release() 释放资源，防止内存溢出
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override//停止播放视频
    public void surfaceDestroyed(SurfaceHolder holder) {
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
        System.out.println("执行了--surfaceDestroyed");
        stopMP(mMediaPlayer.getCurrentPosition());
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        System.out.println("MainActivity.onPrepared");
        mMediaPlayer.seekTo(mPlaypos);
        //设置当前seekBar的最大进度为视频的总时长
        mSeekbarProgress.setMax(mMediaPlayer.getDuration());
        mTvTotalTime.setText(UtilTimeLimit.format(mMediaPlayer.getDuration()));
        resizeSv();
        //解决Home键后mCbPr显示不正确
        if (!mCbPr.isChecked()) {

            mMediaPlayer.start();

            mHandler.sendEmptyMessageDelayed(1, 1000);

        }

    }

    private void resizeSv() {
        int outWidth = DensityUtil.getScreenWidth(this);
        //高度为250dp，所以直接获取高度：dp转像素
        int outHeight = 0;
        if (mCbScreen.isChecked()) {
            outHeight = DensityUtil.getScreenHeight(this);
        } else {
            outHeight = DensityUtil.dip2px(this, 250);
        }
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        //注意不能运算完在转化成float,*1.0f
        float outBl = outWidth * 1.0f / outHeight;
        float videoBl = videoWidth * 1.0f / videoHeight;
        System.out.println(outBl + "");
        System.out.println(videoBl + "");
        if (outBl > videoBl) {
            //重置宽
            int sw = (int) (outHeight * videoBl);
            ViewGroup.LayoutParams layoutParams = mVideoPlayer.getLayoutParams();
            layoutParams.width = sw;
            mVideoPlayer.setLayoutParams(layoutParams);
        } else if (videoBl > outBl) {
            //重置高
            int sh = (int) (outWidth / videoBl);
            ViewGroup.LayoutParams layoutParams = mVideoPlayer.getLayoutParams();
            layoutParams.height = sh;
            mVideoPlayer.setLayoutParams(layoutParams);
        }
    }


//    boolean istouch=true;
//    @Override
//    public void onClick(View v) {
//
//        if (istouch) {
//            bottomLlActivity.setVisibility(View.GONE);
//            istouch=false;
//        }else{
//            bottomLlActivity.setVisibility(View.VISIBLE);
//            istouch=true;
//        }
//    }

    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        stopMP(0);
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mAudioBroad);
    }

    class AudioBroad extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
//        mTvVolume.setText(audioManager.getStreamVolume(audioManager.STREAM_MUSIC)+"");
        }
    }
}
