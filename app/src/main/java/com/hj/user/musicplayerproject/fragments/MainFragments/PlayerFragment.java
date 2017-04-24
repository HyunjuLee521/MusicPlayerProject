package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hj.user.musicplayerproject.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by USER on 2017-03-22.
 */

public class PlayerFragment extends Fragment {

    private ImageView mAlbumImageview;

    private SeekBar mSeekbar;
    private TextView mStartTimeTextview;
    private TextView mEndTimeTextview;

    private TextView mTitleTextveiw;
    private TextView mArtistTextview;

    private ImageView mHeartImageview;
    private ImageView mRepaeatImageview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.player_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAlbumImageview = (ImageView) view.findViewById(R.id.album_imageview);

        mSeekbar = (SeekBar) view.findViewById(R.id.seekBar);
        mStartTimeTextview = (TextView) view.findViewById(R.id.start_time_textview);
        mEndTimeTextview = (TextView) view.findViewById(R.id.end_time_textview);

        mTitleTextveiw = (TextView) view.findViewById(R.id.title_textview);
        mArtistTextview = (TextView) view.findViewById(R.id.artist_textview);

        mHeartImageview = (ImageView) view.findViewById(R.id.heart_imageview);
        mRepaeatImageview = (ImageView) view.findViewById(R.id.repeat_imageview);


    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void updateUI(Boolean isPlaying) {
//        mResumeImageview.setImageResource(isPlaying ? R.drawable.ic_pause_circle_outline_black_24dp : R.drawable.ic_play_circle_outline_black_24dp);
//        updateMetaData(mService.getMetaDataRetriever());
    }
}
