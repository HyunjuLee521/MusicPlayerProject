package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.services.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by USER on 2017-03-22.
 */

public class MusicControllerFragment extends Fragment implements View.OnClickListener {

    private boolean mBound = false;
    private MusicService mService;

    private ImageView mMoreImageview;
    private ImageView mPrevImageview;
    private ImageView mResumeImageview;
    private ImageView mNextImageview;
    private ImageView mChangeModeImageview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_controller_fragment, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMoreImageview = (ImageView) view.findViewById(R.id.more_imageview);
        mPrevImageview = (ImageView) view.findViewById(R.id.prev_imageview);
        mResumeImageview = (ImageView) view.findViewById(R.id.resume_imageview);
        mNextImageview = (ImageView) view.findViewById(R.id.next_imageview);
        mChangeModeImageview = (ImageView) view.findViewById(R.id.change_mode_imageview);

        mMoreImageview.setOnClickListener(this);
        mPrevImageview.setOnClickListener(this);
        mResumeImageview.setOnClickListener(this);
        mNextImageview.setOnClickListener(this);
        mChangeModeImageview.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }

        EventBus.getDefault().unregister(this);
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

//            // UI 갱신
//            updateResumeImageview(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    // TODO resume 버튼 누를 때 곡이 바뀌지 않아도 changeModeImage가 깜박거리는 현상 수정하기
    @Subscribe
    public void updateResumeImageview(Boolean isPlaying) {
        mResumeImageview.setImageResource(isPlaying ? R.drawable.ic_pause_circle_outline_black_24dp : R.drawable.ic_play_circle_outline_black_24dp);
//        updateAlbumImage(mService.getMetaDataRetriever());
    }

    @Subscribe
    public void updateAlbumImage(MediaMetadataRetriever retriever) {

//        Toast.makeText(mService, "받은 값 " + mService.getMetaDataRetriever().toString(), Toast.LENGTH_SHORT).show();

        if (mService.getMetaDataRetriever() != null) {
            // 오디오 앨범 자켓 이미지
            byte albumImage[] = retriever.getEmbeddedPicture();
            if (null != albumImage) {
                Glide.with(this).load(albumImage).into(mChangeModeImageview);
            } else {
                Glide.with(this).load(R.mipmap.ic_launcher).into(mChangeModeImageview);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MusicService.class);

        switch (v.getId()) {
            case R.id.more_imageview:
                break;


            case R.id.prev_imageview:
                intent.setAction(MusicService.ACTION_PREV);
                break;

            case R.id.resume_imageview:
                intent.setAction(MusicService.ACTION_RESUME);
                break;

            case R.id.next_imageview:
                intent.setAction(MusicService.ACTION_NEXT);
                break;


            case R.id.change_mode_imageview:
                break;


            default:
                break;
        }

        getActivity().startService(intent);


    }
}
