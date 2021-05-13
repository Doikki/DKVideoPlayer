package xyz.doikki.dkplayer.fragment.main;

import android.content.Intent;
import android.view.View;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.activity.extend.DefinitionPlayerActivity;
import xyz.doikki.dkplayer.activity.extend.ADActivity;
import xyz.doikki.dkplayer.activity.extend.CacheActivity;
import xyz.doikki.dkplayer.activity.extend.CustomExoPlayerActivity;
import xyz.doikki.dkplayer.activity.extend.CustomIjkPlayerActivity;
import xyz.doikki.dkplayer.activity.extend.DanmakuActivity;
import xyz.doikki.dkplayer.activity.extend.FullScreenActivity;
import xyz.doikki.dkplayer.activity.extend.PadActivity;
import xyz.doikki.dkplayer.activity.extend.PlayListActivity;
import xyz.doikki.dkplayer.fragment.BaseFragment;

public class ExtensionFragment extends BaseFragment implements View.OnClickListener {
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_extension;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.btn_fullscreen).setOnClickListener(this);
        findViewById(R.id.btn_danmu).setOnClickListener(this);
        findViewById(R.id.btn_ad).setOnClickListener(this);
        findViewById(R.id.btn_proxy_cache).setOnClickListener(this);
        findViewById(R.id.btn_play_list).setOnClickListener(this);
        findViewById(R.id.btn_pad).setOnClickListener(this);
        findViewById(R.id.btn_custom_exo_player).setOnClickListener(this);
        findViewById(R.id.btn_custom_ijk_player).setOnClickListener(this);
        findViewById(R.id.btn_definition).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fullscreen:
                startActivity(new Intent(getActivity(), FullScreenActivity.class));
                break;
            case R.id.btn_danmu:
                startActivity(new Intent(getActivity(), DanmakuActivity.class));
                break;
            case R.id.btn_ad:
                startActivity(new Intent(getActivity(), ADActivity.class));
                break;
            case R.id.btn_proxy_cache:
                startActivity(new Intent(getActivity(), CacheActivity.class));
                break;
            case R.id.btn_play_list:
                startActivity(new Intent(getActivity(), PlayListActivity.class));
                break;
            case R.id.btn_pad:
                startActivity(new Intent(getActivity(), PadActivity.class));
                break;
            case R.id.btn_custom_exo_player:
                startActivity(new Intent(getActivity(), CustomExoPlayerActivity.class));
                break;
            case R.id.btn_custom_ijk_player:
                startActivity(new Intent(getActivity(), CustomIjkPlayerActivity.class));
                break;
            case R.id.btn_definition:
                startActivity(new Intent(getActivity(), DefinitionPlayerActivity.class));
                break;
        }
    }
}
