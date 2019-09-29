package com.dueeeke.videocontroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 显示亮度，音量，进度
 * Created by Devlin_n on 2017/4/17.
 */
public class CenterView extends LinearLayout {

    private ImageView ivIcon;
    private TextView tvPercent;
    private ProgressBar proPercent;

    public CenterView(Context context) {
        super(context);

        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_center_window, this);
        ivIcon = view.findViewById(R.id.iv_icon);
        tvPercent = view.findViewById(R.id.tv_percent);
        proPercent = view.findViewById(R.id.pro_percent);
        setAlpha(0f);
    }

    public void setIcon(int icon) {
        if (ivIcon != null) ivIcon.setImageResource(icon);
    }

    public void setTextView(String text) {
        if (tvPercent != null) tvPercent.setText(text);
    }

    public void setProPercent(int percent) {
        if (proPercent != null) proPercent.setProgress(percent);
    }

    public void setProVisibility(int visibility) {
        if (proPercent != null) proPercent.setVisibility(visibility);
    }

    @Override
    public void setVisibility(final int visibility) {
        if (visibility != VISIBLE) {
            this.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            CenterView.super.setVisibility(visibility);
                        }
                    })
                    .start();
            return;
        } else {
            setAlpha(1f);
        }
        super.setVisibility(visibility);
    }
}
