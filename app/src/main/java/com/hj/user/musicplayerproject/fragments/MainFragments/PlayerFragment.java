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
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.services.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by USER on 2017-03-22.
 */

public class PlayerFragment extends Fragment {

    private MusicService mService;
    private boolean mBound = false;


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
        mArtistTextview = (TextView) view.findViewById(R.id.artist_name_textview);

        mHeartImageview = (ImageView) view.findViewById(R.id.heart_imageview);
        mRepaeatImageview = (ImageView) view.findViewById(R.id.repeat_imageview);


    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
//            updateUI(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    int id = -1;

    @Subscribe
    public void updateUI(Boolean isPlaying) {

        if (id != mService.getCurrentId()) {
            MediaMetadataRetriever retriever = mService.getMetaDataRetriever();
            // 미디어 정보
//        final String mUri = uri.toString();
            final String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
            final String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
            final String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

            // 오디오 앨범 자켓 이미지
            // bitmap -> String으로 변환하여 저장
//        final String image;



            byte albumImage[] = retriever.getEmbeddedPicture();

//        if (null != albumImage) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
////            image = BitMapToString(bitmap);
//
//            mAlbumImageview.setImageBitmap(bitmap);
//        }

            if (null != albumImage) {
                Glide.with(this).load(albumImage).into(mAlbumImageview);
            } else {
                Glide.with(this).load(R.mipmap.ic_launcher).into(mAlbumImageview);
            }


            mTitleTextveiw.setText(title);
            mArtistTextview.setText(artist);

            // TODO
            mEndTimeTextview.setText(duration);

        }

        id = mService.getCurrentId();

    }
}
