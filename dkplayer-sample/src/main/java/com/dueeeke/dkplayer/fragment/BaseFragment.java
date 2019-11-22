package com.dueeeke.dkplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.util.L;

/**
 * 可以懒加载的Fragment，使用androidx fragment的方式实现，区别传统方式
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;

    private boolean mIsInitData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResId(), container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLazyLoad()) {
            fetchData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
        String simpleName = getClass().getSimpleName();
        L.d("BaseFragment " + simpleName + " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        String simpleName = getClass().getSimpleName();
        L.d("BaseFragment " + simpleName + " onPause");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        String simpleName = getClass().getSimpleName();
        L.d("BaseFragment " + simpleName + " onHiddenChanged " + hidden);
    }

    private void fetchData() {
        if (mIsInitData)
            return;
        initData();
        mIsInitData = true;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    protected abstract int getLayoutResId();

    protected void initView() {
    }

    protected void initData() {
    }

    /**
     * 是否懒加载
     */
    protected boolean isLazyLoad() {
        return false;
    }

    /**
     * 子类可通过此方法直接拿到VideoViewManager
     */
    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }
}
