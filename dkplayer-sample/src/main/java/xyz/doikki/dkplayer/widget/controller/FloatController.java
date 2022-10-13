package xyz.doikki.dkplayer.widget.controller;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.doikki.dkplayer.widget.component.PipControlView;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videoplayer.controller.GestureVideoController;

/**
 * 悬浮播放控制器
 * Created by Doikki on 2017/6/1.
 */
public class FloatController extends GestureVideoController {

    public FloatController(@NonNull Context context) {
        super(context);
        initView();
    }

    public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    protected void initView() {
        addControlComponent(new CompleteView(getContext()));
        addControlComponent(new ErrorView(getContext()));
        addControlComponent(new PipControlView(getContext()));
    }
}
