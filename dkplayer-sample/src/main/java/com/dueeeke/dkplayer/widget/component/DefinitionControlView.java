package com.dueeeke.dkplayer.widget.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class DefinitionControlView extends VodControlView {

    private TextView mDefinition;

    private PopupWindow mPopupWindow;
    private List<String> mRateStr;
    private LinearLayout mPopLayout;

    private int mCurIndex;

    private LinkedHashMap<String, String> mMultiRateData;

    private OnRateSwitchListener mOnRateSwitchListener;

    public DefinitionControlView(@NonNull Context context) {
        super(context);
    }

    public DefinitionControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefinitionControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_rate_pop, this, false);
        mPopupWindow.setContentView(mPopLayout);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setClippingEnabled(false);
        mDefinition = findViewById(R.id.tv_definition);
        mDefinition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRateMenu();
            }
        });
    }

    private void showRateMenu() {
        mPopLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupWindow.showAsDropDown(mDefinition, -((mPopLayout.getMeasuredWidth() - mDefinition.getMeasuredWidth()) / 2),
                -(mPopLayout.getMeasuredHeight() + mDefinition.getMeasuredHeight() + PlayerUtils.dp2px(getContext(), 10)));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_definition_control_view;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        super.onVisibilityChanged(isVisible, anim);
        if (!isVisible) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            mDefinition.setVisibility(VISIBLE);
        } else {
            mDefinition.setVisibility(GONE);
            mPopupWindow.dismiss();
        }
    }

    public void setData(LinkedHashMap<String, String> multiRateData) {
        mMultiRateData = multiRateData;
        if (mDefinition != null && TextUtils.isEmpty(mDefinition.getText())) {
            L.d("multiRate");
            if (multiRateData == null) return;
            mRateStr = new ArrayList<>();
            int index = 0;
            ListIterator<Map.Entry<String, String>> iterator = new ArrayList<>(multiRateData.entrySet()).listIterator(multiRateData.size());
            while (iterator.hasPrevious()) {//反向遍历
                Map.Entry<String, String> entry = iterator.previous();
                mRateStr.add(entry.getKey());
                TextView rateItem = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_rate_item, null);
                rateItem.setText(entry.getKey());
                rateItem.setTag(index);
                rateItem.setOnClickListener(rateOnClickListener);
                mPopLayout.addView(rateItem);
                index++;
            }
            ((TextView) mPopLayout.getChildAt(index - 1)).setTextColor(ContextCompat.getColor(getContext(), R.color.theme_color));
            mDefinition.setText(mRateStr.get(index - 1));
            mCurIndex = index - 1;
        }
    }

    private OnClickListener rateOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            if (mCurIndex == index) return;
            ((TextView) mPopLayout.getChildAt(mCurIndex)).setTextColor(Color.BLACK);
            ((TextView) mPopLayout.getChildAt(index)).setTextColor(ContextCompat.getColor(getContext(), R.color.theme_color));
            mDefinition.setText(mRateStr.get(index));
            switchDefinition(mRateStr.get(index));
            mPopupWindow.dismiss();
            mCurIndex = index;
        }
    };

    private void switchDefinition(String s) {
        mControlWrapper.hide();
        mControlWrapper.stopProgress();
        String url = mMultiRateData.get(s);
        if (mOnRateSwitchListener != null)
            mOnRateSwitchListener.onRateChange(url);
    }

    public interface OnRateSwitchListener {
        void onRateChange(String url);
    }

    public void setOnRateSwitchListener(OnRateSwitchListener onRateSwitchListener) {
        mOnRateSwitchListener = onRateSwitchListener;
    }
}
