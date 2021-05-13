package xyz.doikki.dkplayer.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.fragment.main.ApiFragment;
import xyz.doikki.dkplayer.fragment.main.ExtensionFragment;
import xyz.doikki.dkplayer.fragment.main.ListFragment;
import xyz.doikki.dkplayer.fragment.main.PipFragment;
import xyz.doikki.dkplayer.util.PIPManager;
import xyz.doikki.dkplayer.util.Tag;
import xyz.doikki.dkplayer.util.Utils;
import xyz.doikki.dkplayer.util.cache.ProxyVideoCacheManager;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;
import xyz.doikki.videoplayer.player.PlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private List<Fragment> mFragments = new ArrayList<>();
    public static int mCurrentIndex;

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

        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .start();

        //检测当前是用的哪个播放器
        Object factory = Utils.getCurrentPlayerFactory();
        if (factory instanceof ExoMediaPlayerFactory) {
            setTitle(getResources().getString(R.string.app_name) + " (ExoPlayer)");
        } else if (factory instanceof IjkPlayerFactory) {
            setTitle(getResources().getString(R.string.app_name) + " (IjkPlayer)");
        } else if (factory instanceof AndroidMediaPlayerFactory) {
            setTitle(getResources().getString(R.string.app_name) + " (MediaPlayer)");
        } else {
            setTitle(getResources().getString(R.string.app_name) + " (unknown)");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        mFragments.add(new ApiFragment());
        mFragments.add(new ListFragment());
        mFragments.add(new ExtensionFragment());
        mFragments.add(new PipFragment());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_content, mFragments.get(0))
                .commitAllowingStateLoss();

        mCurrentIndex = 0;
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
            case R.id.cpu_info:
                CpuInfoActivity.start(this);
                break;
        }

        if (itemId == R.id.ijk || itemId == R.id.exo || itemId == R.id.media) {
            //切换播放核心，不推荐这么做，我这么写只是为了方便测试
            VideoViewConfig config = VideoViewManager.getConfig();
            try {
                Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
                mPlayerFactoryField.setAccessible(true);
                PlayerFactory playerFactory = null;
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
                getVideoViewManager().releaseByTag(Tag.LIST);
                getVideoViewManager().releaseByTag(Tag.SEAMLESS, false);//注意不能移除
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = mFragments.get(index);
            Fragment curFragment = mFragments.get(mCurrentIndex);
            if (fragment.isAdded()) {
                transaction.hide(curFragment).show(fragment);
            } else {
                transaction.add(R.id.layout_content, fragment).hide(curFragment);
            }
            transaction.commitAllowingStateLoss();
            mCurrentIndex = index;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getVideoViewManager().onBackPress(Tag.LIST)) return;
        if (getVideoViewManager().onBackPress(Tag.SEAMLESS)) return;
        super.onBackPressed();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
