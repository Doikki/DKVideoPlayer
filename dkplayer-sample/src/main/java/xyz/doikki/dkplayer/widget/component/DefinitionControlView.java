package xyz.doikki.dkplayer.widget.component;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import xyz.doikki.dkplayer.R;
import xyz.doikki.videocontroller.component.VodControlView;

import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.util.L;
import xyz.doikki.videoplayer.util.PlayerUtils;

public class DefinitionControlView extends VodControlView {

    private final TextView mDefinition;

    private final PopupWindow mPopupWindow;
    private List<String> mRateStr;
    private final LinearLayout mPopLayout;

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
    public void onScreenModeChanged(int screenMode) {
        super.onScreenModeChanged(screenMode);
        if (screenMode == DKVideoView.SCREEN_MODE_FULL) {
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

    private final OnClickListener rateOnClickListener = new OnClickListener() {

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
        mControlWrapper.stopUpdateProgress();
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
