package com.dueeeke.videoplayer.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.CutoutUtil;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class CutoutAdaptHelper {

    private boolean mAdaptCutout;

    private int mSpace;

    private int mCurrentOrientation = -1;

    private Activity mActivity;

    private AdaptView mAdaptView;

    private Callback mCallback;

    CutoutAdaptHelper(Activity activity, Callback callback) {
        mActivity = activity;
        mCallback = callback;
    }

    public void checkCutout() {
        Application application = PlayerUtils.getApplication();
        if (application == null)
            return;
        mAdaptCutout = CutoutUtil.allowDisplayToCutout(mActivity);
        if (mAdaptCutout) {
            mSpace = (int) PlayerUtils.getStatusBarHeight(mActivity);
            if (mAdaptView == null) {
                mAdaptView = new AdaptView(mActivity);
                mAdaptView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            mActivity.addContentView(mAdaptView, mAdaptView.getLayoutParams());
        }
        L.d("adaptCutout: " + mAdaptCutout + " space: " + mSpace);
    }

    public void onOrientationChanged() {
        adjustView();
    }

    public void onPlayerStateChanged(int playerState) {
        if (!mAdaptCutout) return;
        if (playerState == VideoView.PLAYER_NORMAL) {
            CutoutUtil.adaptCutoutAboveAndroidP(mActivity, false);
        } else if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            CutoutUtil.adaptCutoutAboveAndroidP(mActivity, true);
        }
    }

    private void adjustView() {
        if (mAdaptCutout) {
            int o = mActivity.getRequestedOrientation();
            if (o == mCurrentOrientation) {
                return;
            }
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                if (mCallback != null) {
                    mCallback.adjustPortrait(mSpace);
                }
            } else if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                if (mCallback != null) {
                    mCallback.adjustLandscape(mSpace);
                }
            } else if (o == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                if (mCallback != null) {
                    mCallback.adjustReserveLandscape(mSpace);
                }
            }
            mCurrentOrientation = o;
        }
    }

    private class AdaptView extends View {

        public AdaptView(Context context) {
            super(context);
        }


        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            onOrientationChanged();
        }
    }

    public interface Callback {
        void adjustPortrait(int space);
        void adjustLandscape(int space);
        void adjustReserveLandscape(int space);
    }
}
