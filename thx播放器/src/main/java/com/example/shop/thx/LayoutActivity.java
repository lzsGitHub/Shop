package com.example.shop.thx;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class LayoutActivity extends RelativeLayout {
    private final int mStreamMaxVolume;
    private int mNowVolume;
    private GestureDetector mGestureDetector;
    //最大时间改变量
    private final int Max_TIME_CHANGE=1000*30;
    //起始点的坐标
    private float mStartX,mStartY,mCenterX;
    //获取view的总高和宽
    private float mTotalHeight,mTotalWidth;
    //回调接口类属性
    private OnTimeChangeListener mOnTimeChangeListener;
    //给予一个set方法，并在合适的地方调用一下
    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        mOnTimeChangeListener = onTimeChangeListener;
    }

    //亮度和声音
    private int mTypeChange;
    public static final int TYPE_CHANGE_PROGRESS=110;
    public static final int TYPE_CHANGE_VOLUME=120;
    public static final int TYPE_CHANGE_LIGHT=130;
    private OnVolumeChangeListener mOnVolumeChangeListener;
    private float mLightStart;
    private OnLightChangeListener mOnLightChangeListener;

    public void setOnLightChangeListener(OnLightChangeListener onLightChangeListener) {
        mOnLightChangeListener = onLightChangeListener;
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
    }

    private AudioManager mAudioManager;
    public LayoutActivity(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mAudioManager= (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        mStreamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mNowVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector=new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("LayoutActivity.onDown");
                mStartX=e.getX();
                mStartY=e.getY();
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                System.out.println("LayoutActivity.onShowPress");

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //轻敲
                System.out.println("LayoutActivity.onSingleTapUp");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                System.out.println("LayoutActivity.onScroll");
                if (mTypeChange==-1) {
                    if (Math.abs(distanceX)>=Math.abs(distanceY)) {
                        //横着变
                        mTypeChange=TYPE_CHANGE_PROGRESS;
                    }else{
                        //竖着变
                        if(mStartX<mCenterX){
                            //改亮度，左边
                            mTypeChange=TYPE_CHANGE_LIGHT;
                        }else{
                            //改音量，右边
                            mTypeChange=TYPE_CHANGE_VOLUME;

                        }
                    }
                }
                switch (mTypeChange) {
                    case TYPE_CHANGE_PROGRESS:
                        float distance=e2.getX()-mStartX;
                        int timeChange= (int) (distance/mTotalWidth*Max_TIME_CHANGE);
                        //合适的地方调用一下但不最终确认
                        mOnTimeChangeListener.OnTimeChange(timeChange,false);
                        break;
                    case TYPE_CHANGE_VOLUME:
                        float distanceVolume=-1*(e2.getY()-mStartY);
                        int volumeChange= (int) (distanceVolume/mTotalHeight*mStreamMaxVolume);
                        System.out.println("现在的音量："+(mNowVolume+volumeChange+"最大音量： "+mStreamMaxVolume));
                        int result=mNowVolume+volumeChange;
                        if (result<0){
                            result=0;
                        }
                        if (result>mStreamMaxVolume) {
                            result=mStreamMaxVolume;
                        }
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,result,AudioManager.FLAG_SHOW_UI);
//                        mOnVolumeChangeListener.OnVolumeChange(result);
                        break;
                    case TYPE_CHANGE_LIGHT:
                        float distanceLight=-1*(e2.getY()-mStartY);
                        float lightChange= distanceLight/mTotalHeight*1.0f;
                        float resultLight=lightChange+mLightStart;
                        if (resultLight<0){
                            resultLight=0;
                        }
                        if (resultLight>1.0f) {
                            resultLight=1.0f;
                        }
                        BrightnessUtil.setScreenBrightness((Activity) context,resultLight);
                        mOnLightChangeListener.onLightChange(resultLight);
                        break;
                }

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                System.out.println("LayoutActivity.onLongPress");

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                System.out.println("LayoutActivity.onFling");
                return false;
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTotalWidth==0||mTotalHeight==0) {
            mTotalHeight=this.getHeight();
            mTotalWidth=this.getWidth();
            mCenterX=this.getX()+mTotalWidth/2.0f;
            Log.e("mTotalHeight",mTotalHeight+"");
            Log.e("mTotalWidth",mTotalWidth+"");
            Log.e("mCenterX",mCenterX+"");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //把onTouchEvent接收到的事件交给GestureDetector处理。
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (TYPE_CHANGE_PROGRESS==mTypeChange) {
                    float distance=event.getX()-mStartX;
                    Log.e("qq",distance+"");
                    int timeChange= (int) (distance/mTotalWidth*Max_TIME_CHANGE);
                    Log.e("ssssss",timeChange+"");
                    mOnTimeChangeListener.OnTimeChange(timeChange,true);
                }
                if (TYPE_CHANGE_VOLUME==mTypeChange) {
                    mNowVolume=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
                if (TYPE_CHANGE_LIGHT==mTypeChange) {
                    mLightStart=BrightnessUtil.getScreenBrightness((Activity) this.getContext());
                }
                mTypeChange=-1;
                break;
            default:
                break;
        }
        return true;
    }
    public interface OnTimeChangeListener{
        public void OnTimeChange(int timeTotalChange, boolean isLastConfirm);
    }
    public interface OnVolumeChangeListener{
        public void OnVolumeChange(int volumeNow);
    }
    public interface OnLightChangeListener{
        public void onLightChange(float lightNow);
    }
}
