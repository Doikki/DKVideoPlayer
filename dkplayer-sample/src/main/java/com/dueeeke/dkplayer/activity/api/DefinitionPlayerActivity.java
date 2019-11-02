package com.dueeeke.dkplayer.activity.api;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.widget.videoview.DefinitionVideoView;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.ijk.IjkPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;

import java.util.LinkedHashMap;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器演示
 * Created by Devlin_n on 2017/4/7.
 */

public class DefinitionPlayerActivity extends BaseActivity<DefinitionVideoView<IjkPlayer>> {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_definition_player;
    }

    @Override
    protected int getTitleResId() {
        return R.string.str_definition;
    }

    @Override
    protected void initView() {
        super.initView();
        mVideoView = findViewById(R.id.player);

        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent("韩雪：积极的悲观主义者", false);
//        controller.setTitle("韩雪：积极的悲观主义者");
//        mVideoView.setCustomMediaPlayer(new IjkPlayer(this) {
//            @Override
//            public void setInitOptions() {
//                //精准seek
//                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//            }
//        });

        mVideoView.setPlayerFactory(new PlayerFactory<IjkPlayer>() {
            @Override
            public IjkPlayer createPlayer() {
                return new IjkPlayer() {
                    @Override
                    public void setOptions() {
                        //精准seek
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    }
                };
            }
        });

        LinkedHashMap<String, String> videos = new LinkedHashMap<>();
        videos.put("标清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_sd.flv");
        videos.put("高清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_hd.flv");
        videos.put("超清", "http://mov.bn.netease.com/open-movie/nos/flv/2017/07/24/SCP786QON_shd.flv");
        mVideoView.setDefinitionVideos(videos);
        mVideoView.setVideoController(controller);
        mVideoView.start();
    }
}
