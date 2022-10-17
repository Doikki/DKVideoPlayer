package xyz.doikki.dkplayer.widget.videoview;

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
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.widget.CenteredImageSpan;
import xyz.doikki.videoplayer.DKVideoView;
import xyz.doikki.videoplayer.controller.MediaController;
import xyz.doikki.videoplayer.util.PlayerUtils;
import xyz.doikki.videoplayer.BuildConfig;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * 包含弹幕的播放器
 * @deprecated 推荐 {@link xyz.doikki.dkplayer.widget.component.MyDanmakuView}
 */
@Deprecated
public class DanmukuVideoView extends DKVideoView {
    private DanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private BaseDanmakuParser mParser;


    public DanmukuVideoView(@NonNull Context context) {
        super(context);
    }

    public DanmukuVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmukuVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupMediaPlayer() {
        super.setupMediaPlayer();
        if (mDanmakuView == null) {
            initDanMuView();
        }
        playerContainer.removeView(mDanmakuView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = (int) PlayerUtils.getStatusBarHeight(getContext());
        playerContainer.addView(mDanmakuView, layoutParams);
        //将控制器提到最顶层，如果有的话
        MediaController controller = getVideoController();
        if (controller != null) {
            controller.bringToFront();
        }
    }

    @Override
    protected void startPrepare(boolean reset) {
        super.startPrepare(reset);
        if (mDanmakuView != null) {
            if (reset) mDanmakuView.restart();
            mDanmakuView.prepare(mParser, mContext);
        }
    }

    @Override
    protected void startInPlaybackState() {
        super.startInPlaybackState();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (isInPlaybackState()) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.pause();
            }
        }
    }

    @Override
    public void resume() {
        super.resume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    public void release() {
        super.release();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public void seekTo(long pos) {
        super.seekTo(pos);
        if (isInPlaybackState()) {
            if (mDanmakuView != null) mDanmakuView.seekTo(pos);
        }
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        if (mDanmakuView != null) {
            mDanmakuView.clearDanmakusOnScreen();
        }
    }

    private void initDanMuView() {
// 设置最大显示行数
//        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
//        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
//        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
//        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
//        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuView = new DanmakuView(getContext());
        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
//                .setCacheStuffer(new SpannedCacheStuffer(), null) // 图文混排使用SpannedCacheStuffer
//                .setCacheStuffer(new BackgroundCacheStuffer(), null)  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(null)
                .preventOverlapping(null).setDanmakuMargin(40);
        if (mDanmakuView != null) {
            mParser = new BaseDanmakuParser() {
                @Override
                protected IDanmakus parse() {
                    return new Danmakus();
                }
            };
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
            mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {

                @Override
                public boolean onDanmakuClick(IDanmakus danmakus) {
                    Log.d("DFM", "onDanmakuClick: danmakus size:" + danmakus.size());
                    BaseDanmaku latest = danmakus.last();
                    if (null != latest) {
                        Log.d("DFM", "onDanmakuClick: text of latest danmaku:" + latest.text);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onDanmakuLongClick(IDanmakus danmakus) {
                    return false;
                }

                @Override
                public boolean onViewClick(IDanmakuView view) {
                    return false;
                }
            });
            mDanmakuView.showFPS(BuildConfig.DEBUG);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    /**
     * 显示弹幕
     */
    public void showDanMu() {
        if (mDanmakuView != null) mDanmakuView.show();
    }

    /**
     * 隐藏弹幕
     */
    public void hideDanMu() {
        if (mDanmakuView != null) mDanmakuView.hide();
    }

    /**
     * 发送文字弹幕
     *
     * @param text   弹幕文字
     * @param isSelf 是不是自己发的
     */
    public void addDanmaku(String text, boolean isSelf) {
        if (mDanmakuView == null) return;
        mContext.setCacheStuffer(new SpannedCacheStuffer(), null);
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = text;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = PlayerUtils.sp2px(getContext(), 12);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = Color.GRAY;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = isSelf ? Color.GREEN : Color.TRANSPARENT;
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 发送自定义弹幕
     */
    public void addDanmakuWithDrawable() {
        mContext.setCacheStuffer(new BackgroundCacheStuffer(), null);
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
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
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = PlayerUtils.sp2px(getContext(), 12);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
//        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);

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
