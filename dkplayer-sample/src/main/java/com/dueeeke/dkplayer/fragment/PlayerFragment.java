package com.dueeeke.dkplayer.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

public class PlayerFragment extends Fragment {

    private VideoView mVideoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_player, container, false);
        mVideoView = view.findViewById(R.id.player);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //播放raw
        String url = "android.resource://" + getContext().getPackageName() + "/" + R.raw.movie;
        mVideoView.setUrl(url);

        mVideoView.setVideoController(new StandardVideoController(getContext()));
        mVideoView.start();
    }
}
