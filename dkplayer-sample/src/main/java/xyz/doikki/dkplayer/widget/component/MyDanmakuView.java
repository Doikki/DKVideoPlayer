package xyz.doikki.dkplayer.widget.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.widget.CenteredImageSpan;
import xyz.doikki.videoplayer.BuildConfig;
import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class MyDanmakuView extends DanmakuView implements IControlComponent {

    private final DanmakuContext mContext;
    private final BaseDanmakuParser mParser;

    public MyDanmakuView(@NonNull Context context) {
        super(context);
    }

    public MyDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDanmakuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        // 设置最大显示行数
//        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
//        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
//        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
//        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
//        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
//                .setCacheStuffer(new SpannedCacheStuffer(), null) // 图文混排使用SpannedCacheStuffer
//                .setCacheStuffer(new BackgroundCacheStuffer(), null)  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(null)
                .preventOverlapping(null).setDanmakuMargin(40);
        mParser = new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        };
        setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        showFPS(BuildConfig.DEBUG);
        enableDanmakuDrawingCache(true);
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
                release();
                break;
            case VideoView.STATE_PREPARING:
                if (isPrepared()) {
                    restart();
                }
                prepare(mParser, mContext);
                break;
            case VideoView.STATE_PLAYING:
                if (isPrepared() && isPaused()) {
                    resume();
                }
                break;
            case VideoView.STATE_PAUSED:
                if (isPrepared()) {
                    pause();
                }
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                clear();
                clearDanmakusOnScreen();
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    /**
     * 发送文字弹幕
     *
     * @param text   弹幕文字
     * @param isSelf 是不是自己发的
     */
    public void addDanmaku(String text, boolean isSelf) {
        mContext.setCacheStuffer(new SpannedCacheStuffer(), null);
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }

        danmaku.text = text;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.setTime(getCurrentTime() + 1200);
        danmaku.textSize = PlayerUtils.sp2px(getContext(), 12);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = Color.GRAY;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = isSelf ? Color.GREEN : Color.TRANSPARENT;
        addDanmaku(danmaku);
    }

    /**
     * 发送自定义弹幕
     */
    public void addDanmakuWithDrawable() {
        mContext.setCacheStuffer(new BackgroundCacheStuffer(), null);
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }
        // for(int i=0;i<100;i++){
        // }
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round);
        int size = PlayerUtils.dp2px(getContext(), 20);
        drawable.setBounds(0, 0, size, size);

//        danmaku.text = "这是一条弹幕";
        danmaku.text = createSpannable(drawable);
//        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.setTime(getCurrentTime() + 1200);
        danmaku.textSize = PlayerUtils.sp2px(getContext(), 12);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
//        danmaku.borderColor = Color.GREEN;
        addDanmaku(danmaku);

    }

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        CenteredImageSpan span = new CenteredImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" 这是一条自定义弹幕~");
        return spannableStringBuilder;
    }

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {


        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//            danmaku.padding = 5;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor("#65777777"));//黑色 普通
            int radius = PlayerUtils.dp2px(getContext(), 10);
            canvas.drawRoundRect(new RectF(left, top, left + danmaku.paintWidth, top + danmaku.paintHeight), radius, radius, paint);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 禁用描边绘制
        }
    }
}
