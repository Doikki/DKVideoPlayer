package xyz.doikki.dkplayer.activity.pip;

import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.util.DataUtil;
import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.VideoView;

/**
 * Android O PiP demo
 * Created by Doikki on 2018/4/24.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class AndroidOPiPActivity extends AppCompatActivity {
    /**
     * The arguments to be used for Picture-in-Picture mode.
     */
    private final PictureInPictureParams.Builder mPictureInPictureParamsBuilder =
            new PictureInPictureParams.Builder();

    /**
     * Intent action for media controls from Picture-in-Picture mode.
     */
    private static final String ACTION_MEDIA_CONTROL = "media_control";

    /**
     * Intent extra for media controls from Picture-in-Picture mode.
     */
    private static final String EXTRA_CONTROL_TYPE = "control_type";

    /**
     * The request code for play action PendingIntent.
     */
    private static final int REQUEST_PLAY = 1;

    /**
     * The request code for pause action PendingIntent.
     */
    private static final int REQUEST_PAUSE = 2;

    /**
     * The request code for replay action PendingIntent.
     */
    private static final int REQUEST_REPLAY = 3;

    /**
     * The intent extra value for play action.
     */
    private static final int CONTROL_TYPE_PLAY = 1;

    /**
     * The intent extra value for pause action.
     */
    private static final int CONTROL_TYPE_PAUSE = 2;

    /**
     * The intent extra value for replay action.
     */
    private static final int CONTROL_TYPE_REPLAY = 3;

    /**
     * A {@link BroadcastReceiver} to receive action item events from Picture-in-Picture mode.
     */
    private BroadcastReceiver mReceiver;

    private VideoView mVideoView;
    private StandardVideoController mController;
    private int mWidthPixels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pip_android_o);
        mVideoView = findViewById(R.id.player);
        mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        mVideoView.setLayoutParams(new LinearLayout.LayoutParams(mWidthPixels, mWidthPixels * 9 / 16 + 1));

        mVideoView.setDataSource(DataUtil.SAMPLE_URL);
        mController = new StandardVideoController(this);
        mController.addDefaultControlComponent(getString(R.string.str_pip_android_o), false);
        mVideoView.setVideoController(mController);
        mVideoView.start();
        mVideoView.addOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playState, @NonNull HashMap<String, Object> extras) {
                switch (playState) {
                    case VideoView.STATE_PAUSED:
                        updatePictureInPictureActions(
                                R.drawable.dkplayer_ic_action_play_arrow, "播放", CONTROL_TYPE_PLAY, REQUEST_PLAY);
                        break;
                    case VideoView.STATE_PLAYING:
                        updatePictureInPictureActions(
                                R.drawable.dkplayer_ic_action_pause, "暂停", CONTROL_TYPE_PAUSE, REQUEST_PAUSE);
                        break;
                    case VideoView.STATE_PLAYBACK_COMPLETED:
                        updatePictureInPictureActions(
                                R.drawable.dkplayer_ic_action_replay, "重新播放", CONTROL_TYPE_REPLAY, REQUEST_REPLAY);
                        break;
                }
            }
        });
    }

    /**
     * Update the state of pause/resume action item in Picture-in-Picture mode.
     *
     * @param iconId      The icon to be used.
     * @param title       The title text.
     * @param controlType The type of the action. either {@link #CONTROL_TYPE_PLAY} or {@link
     *                    #CONTROL_TYPE_PAUSE}.
     * @param requestCode The request code for the {@link PendingIntent}.
     */
    void updatePictureInPictureActions(
            @DrawableRes int iconId, String title, int controlType, int requestCode) {
        final ArrayList<RemoteAction> actions = new ArrayList<>();

        // This is the PendingIntent that is invoked when a user clicks on the action item.
        // You need to use distinct request codes for play and pause, or the PendingIntent won't
        // be properly updated.
        final PendingIntent intent =
                PendingIntent.getBroadcast(
                        AndroidOPiPActivity.this,
                        requestCode,
                        new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType),
                        0);
        final Icon icon = Icon.createWithResource(AndroidOPiPActivity.this, iconId);
        actions.add(new RemoteAction(icon, title, title, intent));

        mPictureInPictureParamsBuilder.setActions(actions);

        // This is how you can update action items (or aspect ratio) for Picture-in-Picture mode.
        // Note this call can happen even when the app is not in PiP mode. In that case, the
        // arguments will be used for at the next call of #enterPictureInPictureMode.
        setPictureInPictureParams(mPictureInPictureParamsBuilder.build());
    }

    public void startFloatWindow(View view) {
        Rational aspectRatio = new Rational(16, 9);
        mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
        enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVideoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (mVideoView.onBackPressed()) return;
        super.onBackPressed();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        Log.d("pip", "onPictureInPictureModeChanged: " + isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            mVideoView.setVideoController(null);
            mVideoView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // Starts receiving events from action items in PiP mode.
            mReceiver =
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent == null
                                    || !ACTION_MEDIA_CONTROL.equals(intent.getAction())) {
                                return;
                            }

                            // This is where we are called mBack from Picture-in-Picture action
                            // items.
                            final int controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0);
                            switch (controlType) {
                                case CONTROL_TYPE_PLAY:
                                    mVideoView.start();
                                    break;
                                case CONTROL_TYPE_PAUSE:
                                    mVideoView.pause();
                                    break;
                                case CONTROL_TYPE_REPLAY:
                                    mVideoView.replay(true);
                                    break;
                            }
                        }
                    };
            registerReceiver(mReceiver, new IntentFilter(ACTION_MEDIA_CONTROL));
        } else {
            // We are out of PiP mode. We can stop receiving events from it.
            unregisterReceiver(mReceiver);
            mReceiver = null;
            Log.d("pip", "onPictureInPictureModeChanged: " + mVideoView);
            mVideoView.setLayoutParams(new LinearLayout.LayoutParams(
                    mWidthPixels,
                    mWidthPixels * 9 / 16 + 1));
            mVideoView.setVideoController(mController);
            mVideoView.requestLayout();
        }
    }
}
