package com.devlin_n.magic_player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.devlin_n.magic_player.player.IjkVideoView;

/**
 * 用于显示video的，做了横屏与竖屏的匹配，还有需要rotation需求的
 */

public class MagicTextureView extends TextureView {

    private int mVideoWidth;

    private int mVideoHeight;

    private int screenType;

    public MagicTextureView(Context context) {
        super(context);
    }

    public MagicTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;

    }

    public void setScreenType(int type) {
        screenType = type;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        int widthS = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int heightS = getDefaultSize(mVideoHeight, heightMeasureSpec);


        if (mVideoWidth > 0 && mVideoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }

        if (getRotation() != 0 && getRotation() % 90 == 0) {
            if (widthS < heightS) {
                if (width > height) {
                    width = (int) (width * (float) widthS / height);
                    height = widthS;
                } else {
                    height = (int) (height * (float) width / widthS);
                    width = widthS;
                }
            } else {
                if (width > height) {
                    height = (int) (height * (float) width / widthS);
                    width = widthS;
                } else {
                    width = (int) (width * (float) widthS / height);
                    height = widthS;
                }
            }
        }

        //如果设置了比例
        if (screenType == IjkVideoView.SCREEN_TYPE_16_9) {
            if (height > width) {
                width = height * 9 / 16;
            } else {
                height = width * 9 / 16;
            }
        } else if (screenType == IjkVideoView.SCREEN_TYPE_4_3) {
            if (height > width) {
                width = height * 3 / 4;
            } else {
                height = width * 3 / 4;
            }
        }

        mVideoHeight = height;

        mVideoWidth = width;

        setMeasuredDimension(width, height);
    }


}