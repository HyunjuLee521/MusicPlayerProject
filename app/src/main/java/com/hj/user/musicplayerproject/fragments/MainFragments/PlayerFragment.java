package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.models.FavoriteMusicFile;
import com.hj.user.musicplayerproject.models.MusicFile;
import com.hj.user.musicplayerproject.services.MusicService;
import com.hj.user.musicplayerproject.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 2017-03-22.
 */

public class PlayerFragment extends Fragment implements View.OnClickListener {

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

    private Realm mRealm;


    private String mPlayMode;


    private static final int SEC = 1000;
    private CountDownTimer progressTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();

        mPlayMode = "repeat";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

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

        mHeartImageview.setOnClickListener(this);
        mRepaeatImageview.setOnClickListener(this);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mService.getMediaPlayer() != null) {
                    mService.getMediaPlayer().seekTo(progress * SEC);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 드래그 멈출때
                // mService.getMediaPlayer().pause();

                //  allowProgressUpdates = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 최초 탭해서 드래그 시작할때
                if (mService.getMediaPlayer() != null) {
                    mService.getMediaPlayer().seekTo(seekBar.getProgress());
                }
                //  allowProgressUpdates = true;
            }
        });


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
//            updateUI2(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            cancelCountdown();
            mBound = false;
        }
    };


    @Override
    public void onStop() {
        super.onStop();


        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void restartMainUpdateUI(MyUtils.restartUpdateUiEvent event) {
        boolean isPlaying = event.isPlaying;
        Toast.makeText(getContext(), "updatdUi 이벤트버스 도착, isPlaying 값 " + isPlaying, Toast.LENGTH_SHORT).show();

        updateUI2(isPlaying);
    }


    Uri uri;

    @Subscribe
    public void updateUI2(Boolean isPlaying) {

        if (uri == null || uri != mService.getCurrentUri()) {

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
            if (isPlaying) {
                if (retriever != null) {
                    // ms값
                    int longDuration = mService.getMediaPlayer().getDuration();

                    int min = longDuration / 1000 / 60;
                    int sec = longDuration / 1000 % 60;

                    mEndTimeTextview.setText(String.format(Locale.KOREA, "%d:%02d", min, sec));

//                    mSeekbar.setMax(longDuration);
                    startCountdown();
                }
            }

//            RealmQuery<FavoriteMusicFile> a = mRealm.where(FavoriteMusicFile.class);
//            RealmQuery<FavoriteMusicFile> b = a.equalTo("uri", uri.toString());
//            RealmResults<FavoriteMusicFile> c = b.findAll();
//            int d = c.size();


        }

        uri = mService.getCurrentUri();

        boolean itemIsFavorite = false;

        if (mRealm.where(FavoriteMusicFile.class)
                .equalTo("uri", uri.toString())
                .findAll()
                .size() > 0 &&
                mRealm.where(FavoriteMusicFile.class) != null) {
            itemIsFavorite = true;
        }


//        Toast.makeText(getActivity(), "좋아요 플레이리스트 렘에 들어간 갯수 "
//                        + mRealm.where(FavoriteMusicFile.class)
//                        .findAll()
//                        .size(),
//                Toast.LENGTH_SHORT).show();

        if (itemIsFavorite) {
//                mHeartImageview.setImageResource(R.drawable.ic_favorite_border_black_24dp);

//            Toast.makeText(getActivity(), "좋아요 되어있음", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(R.drawable.like_black).into(mHeartImageview);


        } else {
//                mHeartImageview.setImageResource(R.drawable.like_black);
//            Toast.makeText(getActivity(), "좋아요 되어있지않음", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(R.drawable.like_white).into(mHeartImageview);

        }


    }


    @Override
    public void onClick(View v) {
        final Uri uri = mService.getCurrentUri();

        switch (v.getId()) {
            case R.id.heart_imageview:
                if (uri != null) {

                    boolean itemIsFavorite = false;

                    if (mRealm.where(FavoriteMusicFile.class)
                            .equalTo("uri", uri.toString())
                            .findAll()
                            .size() > 0 &&
                            mRealm.where(FavoriteMusicFile.class) != null) {
                        itemIsFavorite = true;
                    }

                    if (itemIsFavorite) {
                        // 이미 선택되어 있음

                        Glide.with(this).load(R.drawable.like_white).into(mHeartImageview);

                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                realm.where(FavoriteMusicFile.class)
                                        .equalTo("uri", uri.toString())
                                        .findAll()
                                        .deleteAllFromRealm();

                            }
                        });

                    } else {
                        // 선택되어 있지 않음

                        Glide.with(this).load(R.drawable.like_black).into(mHeartImageview);
                        getSongToFavoritePlaylist(uri);
                    }

                }
                break;


            case R.id.repeat_imageview:
                if (uri != null) {
                    if (mPlayMode.equals("play_one")) {
                        // 1. 전체 반복 default (리핏블랙)
                        Glide.with(this).load(R.drawable.repeat).into(mRepaeatImageview);
                        mPlayMode = "repeat";

                    } else if (mPlayMode.equals("repeat")) {
                        // 2. 전체 랜덤 반복 (셔플)

                        Glide.with(this).load(R.drawable.shuffle).into(mRepaeatImageview);
                        mPlayMode = "shuffle";

                    } else if (mPlayMode.equals("shuffle")) {
                        // 3. 한곡 반복 (플레이 원)

                        Glide.with(this).load(R.drawable.play_one).into(mRepaeatImageview);
                        mPlayMode = "play_one";
                    }

                    /**
                     * {@link com.hj.user.musicplayerproject.services.MusicService#getChangedPlayMode(MyUtils.changePlayModeEvent)}
                     */

                    MyUtils.changePlayModeEvent event = new MyUtils.changePlayModeEvent(mPlayMode);
                    EventBus.getDefault().post(event);

                }


                break;

            default:
                break;


        }


    }


    public ArrayList<MusicFile> getModelList() {
        ArrayList<MusicFile> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<MusicFile> results = realm
                    .where(MusicFile.class)
                    .findAll();
            list.addAll(realm.copyFromRealm(results));
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public ArrayList<MusicFile> getModelList2(RealmResults<MusicFile> musicFIles) {
        ArrayList<MusicFile> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            list.addAll(realm.copyFromRealm(musicFIles));
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }


    // uri값 받아와서
    // Realm 테이블 MusicFile에 저장
    public void getSongToFavoritePlaylist(Uri uri) {

        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(MyUtils.getRealPath(this, uri));
        retriever.setDataSource(getContext(), uri);

        // 미디어 정보
        final String mUri = uri.toString();
        final String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        final String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
        final String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

        // 오디오 앨범 자켓 이미지
        // bitmap -> String으로 변환하여 저장
        final byte[] image2;


//             오디오 앨범 자켓 이미지
        byte albumImage[] = retriever.getEmbeddedPicture();
        if (null != albumImage) {
            // 바이트 -> 비트맵
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
            // 비트맵 -> String
            image2 = albumImage;

        } else {
            image2 = null;

        }


        // 렘에 저장
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                FavoriteMusicFile favoriteMusicFile = mRealm.createObject(FavoriteMusicFile.class);

                favoriteMusicFile.setUri(mUri);
                favoriteMusicFile.setArtist(artist);
                favoriteMusicFile.setTitle(title);
                favoriteMusicFile.setDuration(duration);
                favoriteMusicFile.setImage2(image2);


                Number currentIdNum = mRealm.where(FavoriteMusicFile.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 0;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                favoriteMusicFile.setId(nextId);
                mRealm.insertOrUpdate(favoriteMusicFile); // using insert API


            }
        });


    }


    private void startCountdown() {
        if (progressTimer != null) {
            progressTimer.cancel();
        }

        final int position = mService.getMediaPlayer().getCurrentPosition();
        final int duration = mService.getMediaPlayer().getDuration();

        mSeekbar.setIndeterminate(duration == -1);
        mSeekbar.setMax(duration);

        progressTimer = new CountDownTimer(duration - position, SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mService.getMediaPlayer() != null) {

                    int currentPosition = mService.getMediaPlayer().getCurrentPosition();

                    // TODO 안변함
                    mSeekbar.setProgress(currentPosition);
                    Log.d(TAG, "onTick: " + currentPosition + " 맥스값 : " + mSeekbar.getMax());

                    // if (allowProgressUpdates) {
                    //   mSeekBar.setProgress((duration - (int) millisUntilFinished) / SEC);
                    // }

                    int min = currentPosition / SEC / 60;
                    int sec = currentPosition / SEC % 60;
                    mStartTimeTextview.setText(String.format(Locale.KOREA, "%d:%02d", min, sec));
                }
            }

            @Override
            public void onFinish() {
                progressTimer = null;
                mSeekbar.setProgress(0);
                mSeekbar.setMax(0);
            }
        }.start();
    }

    private void cancelCountdown() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        int position = mService.getMediaPlayer().getCurrentPosition();
        final int duration = mService.getMediaPlayer().getDuration();

        mSeekbar.setIndeterminate(false);
        mSeekbar.setProgress(position / SEC);
        mSeekbar.setMax(duration / SEC);
    }


}
