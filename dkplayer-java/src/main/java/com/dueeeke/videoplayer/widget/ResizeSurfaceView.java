package com.dueeeke.videoplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * Created by Devlin_n on 2017/4/10.
 */

public class ResizeSurfaceView extends SurfaceView {

    private int mVideoWidth;
    private int mVideoHeight;
    private int screenType;

    public ResizeSurfaceView(Context context) {
        super(context);
    }

    public ResizeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        getHolder().setFixedSize(width, height);
    }

    public void setScreenScale(int type) {
        screenType = type;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
//                + MeasureSpec.toString(heightMeasureSpec) + ")");

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

//        Log.d("@@@@", "onMeasure: width" + width + "    height:" + height);


        //如果设置了比例
        switch (screenType) {
            case IjkVideoView.SCREEN_SCALE_ORIGINAL:
                width = mVideoWidth;
                height = mVideoHeight;
                break;
            case IjkVideoView.SCREEN_SCALE_16_9:
                if (height > width / 16 * 9) {
                    height = width / 16 * 9;
                } else {
                    width = height / 9 * 16;
                }
                break;
            case IjkVideoView.SCREEN_SCALE_4_3:
                if (height > width / 4 * 3) {
                    height = width / 4 * 3;
                } else {
                    width = height / 3 * 4;
                }
//                Log.d("@@@@", "onMeasure 4:3 : width" + width + "    height:" + height);
                break;
            case IjkVideoView.SCREEN_SCALE_MATCH_PARENT:
                width = widthMeasureSpec;
                height = heightMeasureSpec;
                break;
            default:
                if (mVideoWidth > 0 && mVideoHeight > 0) {

                    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
                    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
                    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
                    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

                    if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                        // the size is fixed
                        width = widthSpecSize;
                        height = heightSpecSize;

                        // for compatibility, we adjust size based on aspect ratio
                        if (mVideoWidth * height < width * mVideoHeight) {
                            //Log.i("@@@", "image too wide, correcting");
                            width = height * mVideoWidth / mVideoHeight;
                        } else if (mVideoWidth * height > width * mVideoHeight) {
                            //Log.i("@@@", "image too tall, correcting");
                            height = width * mVideoHeight / mVideoWidth;
                        }
                    } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                        // only the width is fixed, adjust the height to match aspect ratio if possible
                        width = widthSpecSize;
                        height = width * mVideoHeight / mVideoWidth;
                        if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                            // couldn't match aspect ratio within the constraints
                            height = heightSpecSize;
                        }
                    } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                        // only the height is fixed, adjust the width to match aspect ratio if possible
                        height = heightSpecSize;
                        width = height * mVideoWidth / mVideoHeight;
                        if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                            // couldn't match aspect ratio within the constraints
                            width = widthSpecSize;
                        }
                    } else {
                        // neither the width nor the height are fixed, try to use actual video size
                        width = mVideoWidth;
                        height = mVideoHeight;
                        if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                            // too tall, decrease both width and height
                            height = heightSpecSize;
                            width = height * mVideoWidth / mVideoHeight;
                        }
                        if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                            // too wide, decrease both width and height
                            width = widthSpecSize;
                            height = width * mVideoHeight / mVideoWidth;
                        }
                    }
                } else {
                    // no size yet, just adopt the given spec sizes
                }
                break;
        }
        setMeasuredDimension(width, height);
    }
}
