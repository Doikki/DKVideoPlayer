package com.dueeeke.dkplayer.activity.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.transition.Transition;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.util.IntentKeys;
import com.dueeeke.dkplayer.util.Tag;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

public class DetailActivity extends BaseActivity<VideoView> {

    private FrameLayout mPlayerContainer;

    public static final String VIEW_NAME_PLAYER_CONTAINER = "player_container";

    @Override
    protected int getTitleResId() {
        return R.string.str_seamless_play;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void initView() {
        super.initView();
        mPlayerContainer = findViewById(R.id.player_container);
        ViewCompat.setTransitionName(mPlayerContainer, VIEW_NAME_PLAYER_CONTAINER);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || !addTransitionListener()) {
            initVideoView();
        }
    }

    private void initVideoView() {
        //拿到VideoView实例
        mVideoView = getVideoViewManager().get(Tag.SEAMLESS);
        //如果已经添加到某个父容器，就将其移除
        Utils.removeViewFormParent(mVideoView);
        //把播放器添加到页面的容器中
        mPlayerContainer.addView(mVideoView);
        //设置新的控制器
        StandardVideoController controller = new StandardVideoController(DetailActivity.this);
        mVideoView.setVideoController(controller);

        Intent intent = getIntent();
        boolean seamlessPlay = intent.getBooleanExtra(IntentKeys.SEAMLESS_PLAY, false);
        String title = intent.getStringExtra(IntentKeys.TITLE);
        controller.addDefaultControlComponent(title, false);
        if (seamlessPlay) {
            //无缝播放需还原Controller状态
            controller.setPlayState(mVideoView.getCurrentPlayState());
            controller.setPlayerState(mVideoView.getCurrentPlayerState());
        } else {
            //不是无缝播放的情况
            String url = intent.getStringExtra(IntentKeys.URL);
            mVideoView.setUrl(url);
            mVideoView.start();
        }
    }

    @RequiresApi(21)
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    initVideoView();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            //移除Controller
            mVideoView.setVideoController(null);
            //将VideoView置空，其目的是不执行 super.onPause(); 和 super.onDestroy(); 中的代码
            mVideoView = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateResume(this);
    }

    /**
     * Android10 Activity的onStop方法会导致共享元素动画失效，通过反射注入恢复共享元素动画
     * 需要同时配置项目的targetSdkVersion <= 27 ， 因为Android10把反射也禁止了。
     */
    public static void updateResume(Activity activity) {
        try {
            Field activityTransitionStateField = Activity.class.getDeclaredField("mActivityTransitionState");
            activityTransitionStateField.setAccessible(true);
            Object mActivityTransitionState = activityTransitionStateField.get(activity);
            Class clazz = Class.forName("android.app.ActivityTransitionState");
            Method enterReady = clazz.getDeclaredMethod("enterReady", Activity.class);
            enterReady.setAccessible(true);
            enterReady.invoke(mActivityTransitionState, activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
