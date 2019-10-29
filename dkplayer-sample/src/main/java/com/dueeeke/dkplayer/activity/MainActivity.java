package com.dueeeke.dkplayer.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.fragment.main.ApiFragment;
import com.dueeeke.dkplayer.fragment.main.ExtensionFragment;
import com.dueeeke.dkplayer.fragment.main.ListFragment;
import com.dueeeke.dkplayer.fragment.main.PipFragment;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.dkplayer.util.cache.ProxyVideoCacheManager;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private List<Fragment> mFragments;
    private int mCurrentIndex;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean enableBack() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();

        //检测当前是用的哪个播放器
        Object factory = Utils.getCurrentPlayerFactory();
        if (factory instanceof ExoMediaPlayerFactory) {
            setTitle(getResources().getString(R.string.app_name) + " (ExoPlayer)");
        } else if (factory instanceof IjkPlayerFactory) {
            setTitle(getResources().getString(R.string.app_name) + " (IjkPlayer)");
        } else {
            setTitle(getResources().getString(R.string.app_name) + " (MediaPlayer)");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        mFragments = new ArrayList<>();
        mFragments.add(new ApiFragment());
        mFragments.add(new ListFragment());
        mFragments.add(new ExtensionFragment());
        mFragments.add(new PipFragment());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_content, mFragments.get(0))
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.close_float_window:
                PIPManager.getInstance().stopFloatWindow();
                PIPManager.getInstance().reset();
                break;
            case R.id.clear_cache:
                if (ProxyVideoCacheManager.clearAllCache(this)) {
                    Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (itemId == R.id.ijk || itemId == R.id.exo || itemId == R.id.media) {
            //切换播放核心，不推荐这么做，我这么写只是为了方便测试
            VideoViewConfig config = VideoViewManager.getConfig();
            try {
                Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
                mPlayerFactoryField.setAccessible(true);
                PlayerFactory playerFactory = null;
                String msg = getString(R.string.str_current_player);
                switch (itemId) {
                    case R.id.ijk:
                        playerFactory = IjkPlayerFactory.create();
                        setTitle(getResources().getString(R.string.app_name) + " (IjkPlayer)");
                        break;
                    case R.id.exo:
                        playerFactory = ExoMediaPlayerFactory.create();
                        setTitle(getResources().getString(R.string.app_name) + " (ExoPlayer)");
                        break;
                    case R.id.media:
                        playerFactory = AndroidMediaPlayerFactory.create();
                        setTitle(getResources().getString(R.string.app_name) + " (MediaPlayer)");
                        break;
                }
                mPlayerFactoryField.set(config, playerFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index;
        int itemId = menuItem.getItemId();
        switch (itemId) {
            default:
            case R.id.tab_api:
                index = 0;
                break;
            case R.id.tab_list:
                index = 1;
                break;
            case R.id.tab_extension:
                index = 2;
                break;
            case R.id.tab_pip:
                index = 3;
                break;
        }

        if (mCurrentIndex != index) {
            //切换tab，释放正在播放的播放器
            if (mCurrentIndex == 1) {
                VideoViewManager.instance().release();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = mFragments.get(index);
            Fragment currFragment = mFragments.get(mCurrentIndex);
            if (fragment.isAdded()) {
                transaction.hide(currFragment).show(fragment);
            } else {
                transaction.add(R.id.layout_content, fragment).hide(currFragment);
            }
            transaction.commitAllowingStateLoss();
            mCurrentIndex = index;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (VideoViewManager.instance().onBackPressed())
            return;
        super.onBackPressed();
    }
}
