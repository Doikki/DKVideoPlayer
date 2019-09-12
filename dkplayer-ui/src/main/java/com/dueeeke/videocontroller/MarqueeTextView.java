package com.dueeeke.videocontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 实现跑马灯效果的TextView
 */
public class MarqueeTextView extends TextView {
    private boolean mNeedFocus;
    public MarqueeTextView(Context context) {
        super(context);
    }
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        if (mNeedFocus) {
            return false;
        }
        return super.isFocused();
    }

    public void setNeedFocus(boolean needFocus) {
        mNeedFocus = needFocus;
    }
}