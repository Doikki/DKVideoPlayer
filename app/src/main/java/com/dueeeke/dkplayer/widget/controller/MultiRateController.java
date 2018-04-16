package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.interf.MultiRateMediaPlayerControl;
import com.dueeeke.dkplayer.widget.videoview.MultiRateIjkVideoView;

import java.util.List;

/**
 * 多码率控制器
 * Created by Devlin_n on 2018/4/16.
 */

public class MultiRateController extends StandardVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected TextView multiRate;
    private PopupMenu mPopupMenu;


    public MultiRateController(@NonNull Context context) {
        this(context, null);
    }

    public MultiRateController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiRateController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        multiRate = controllerView.findViewById(R.id.tv_multi_rate);
        multiRate.setOnClickListener(this);
        mPopupMenu = new PopupMenu(getContext(), multiRate);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.tv_multi_rate) {
            showRateMenu();
        }
    }

    private void showRateMenu() {
        mPopupMenu.show();
    }


    @Override
    protected int setProgress() {
        if (multiRate != null && TextUtils.isEmpty(multiRate.getText())) {
            List<MultiRateIjkVideoView.MultiRateVideoModel> multiRateData = ((MultiRateMediaPlayerControl) mediaPlayer).getMultiRateData();
            MultiRateIjkVideoView.MultiRateVideoModel multiRateVideoModel = multiRateData.get(0);
            if (multiRateVideoModel != null) multiRate.setText(multiRateVideoModel.type);
            for (MultiRateIjkVideoView.MultiRateVideoModel item : multiRateData) {
                mPopupMenu.getMenu().add(item.type);
            }
            mPopupMenu.setOnMenuItemClickListener(item -> {
                ((MultiRateMediaPlayerControl) mediaPlayer).switchRate(item.getTitle().toString());
                multiRate.setText(item.getTitle().toString());
                return false;
            });
        }
        return super.setProgress();
    }
}
