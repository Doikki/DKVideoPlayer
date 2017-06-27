package com.devlin_n.videoplayer.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devlin_n.videoplayer.R;
import com.devlin_n.videoplayer.player.BackgroundPlayService;
import com.devlin_n.videoplayer.util.Constants;

/**
 * 错误提示，网络提示
 * Created by Devlin_n on 2017/4/13.
 */

public class StatusView extends LinearLayout {

    private TextView tvMessage;
    private TextView btnAction;
    private ImageView ivClose;
    private float downX;
    private float downY;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.layout_status_view, this);
        tvMessage = (TextView) root.findViewById(R.id.message);
        btnAction = (TextView) root.findViewById(R.id.status_btn);
        ivClose = (ImageView) root.findViewById(R.id.btn_close);
        this.setBackgroundResource(android.R.color.black);
        setClickable(true);
        if (Constants.IS_START_FLOAT_WINDOW) {
            ivClose.setVisibility(VISIBLE);
            ivClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().getApplicationContext().stopService(new Intent(getContext(), BackgroundPlayService.class));
                }
            });
        }
    }

    public void setMessage(String msg) {
        if (tvMessage != null) tvMessage.setText(msg);
    }

    public void setButtonTextAndAction(String text, OnClickListener listener) {
        if (btnAction != null) {
            btnAction.setText(text);
            btnAction.setOnClickListener(listener);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                // True if the child does not want the parent to intercept touch events.
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - downX);
                float absDeltaY = Math.abs(ev.getY() - downY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
