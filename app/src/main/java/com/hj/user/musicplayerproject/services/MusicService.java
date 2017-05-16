package com.hj.user.musicplayerproject.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.hj.user.musicplayerproject.models.MusicFile;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by USER on 2017-03-31.
 */

public class MusicService extends Service {

    // PlaylistFragment
    // 리스트뷰에서 아이템(곡) 클릭
    public static String ACTION_PLAY = "play";

    // MusicControllerFragment의
    // 가운데 버튼(재생, 일시중지) 클릭
    public static String ACTION_RESUME = "resume";
    // 왼쪽 버튼(<< : 이전곡) 클릭
    public static String ACTION_PREV = "prev";
    // 오른쪽 버튼(>> : 다음곡) 클릭
    public static String ACTION_NEXT = "next";


    private MediaPlayer mMediaPlayer;
    private MediaMetadataRetriever mRetriever;

    private Realm mRealm;

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;

    private boolean mAudioFocusGranted;
    private int mAudioFocusState;

    private int currentId;

    private ArrayList<Uri> mSongList;


    @Override
    public void onCreate() {
        super.onCreate();
        // 렘 초기화
        mRealm = Realm.getDefaultInstance();

        mAudioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);


        // TODO 오디오 포커스를 잃었을 때
        mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {

                Toast.makeText(MusicService.this, "포커스가 변경되었다", Toast.LENGTH_SHORT).show();

                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Toast.makeText(MusicService.this, "포커스체인지 얻었다", Toast.LENGTH_SHORT).show();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
//                        mMediaPlayer.stop();
//                        mMediaPlayer.release();
//                        mMediaPlayer = null;

                        mMediaPlayer.pause();

                        /**
                         * {@link com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment#updateResumeImageview(Boolean)}
                         */
                        EventBus.getDefault().post(isPlaying());
                        Toast.makeText(MusicService.this, "포커스를 잃었다", Toast.LENGTH_SHORT).show();

                        break;


                    // TODO 포커스 잠시 잃었을 때, 미디어플레이어 종료하지 않고 정지
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        mMediaPlayer.pause();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                        break;


                }


            }
        };


        // mSongLIst 초기화
        mSongList = new ArrayList<Uri>();

        // TODO mSongList에 재생목록 담기

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 렘 닫기
        mRealm.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                requestAudioFocus();
                playMusicList(intent.getIntExtra("id", -1));
            } else if (ACTION_RESUME.equals(action) && mMediaPlayer != null) {
                clickResumeButton();
            } else if (ACTION_PREV.equals(action) && mMediaPlayer != null) {
                prevMusic();
            } else if (ACTION_NEXT.equals(action) && mMediaPlayer != null) {
                nextMusic();
            }
        }
        return START_STICKY;
    }

    private void requestAudioFocus() {
        int result = mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocusGranted = true;
            mAudioFocusState = AudioManager.AUDIOFOCUS_GAIN;
//            Toast.makeText(this, "포커스를 요청하여 얻었다", Toast.LENGTH_SHORT).show();
        } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            mAudioFocusGranted = false;
        }
    }


    private void clickResumeButton() {
        requestAudioFocus();

//        if (mMediaPlayer == null) {
//            playMusicList(getIdPref(MusicService.this, "id"));
//        }


        if (mMediaPlayer != null) {
            if (isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }

        /**
         * {@link com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment#updateResumeImageview(Boolean)}
         * {@link com.hj.user.musicplayerproject.fragments.MainFragments.PlayerFragment#updateUI(Boolean)}
         */

        EventBus.getDefault().post(isPlaying());
    }

    public MediaMetadataRetriever getMetaDataRetriever() {
        return mRetriever;
    }

    public Integer getCurrentId() {
        return currentId;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }


    private void setIdPref(Context context, String key, int values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, values);
        editor.apply();
    }

    private int getIdPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int id = prefs.getInt(key, -1);
        return id;
    }


    private void playMusicList(int id) {
        boolean isValidId = true;

        while (mRealm.where(MusicFile.class).equalTo("id", id).count() < 1) {
            isValidId = false;
            id++;

            if (mRealm.where(MusicFile.class).equalTo("id", id).count() != 0) {
                isValidId = true;
                break;
            }
        }

        currentId = id;
        setIdPref(MusicService.this, "id", currentId);

        if (id == -1) {
            Toast.makeText(this, "id값이 -1 : SongFragment에서 id값 전달받지 못했음", Toast.LENGTH_SHORT).show();

        } else if (isValidId) {

            Uri uri = Uri.parse(mRealm.where(MusicFile.class).equalTo("id", id).findFirst().getUri());
            final int nextId = id + 1;

            try {

                // 현재 재생중인 정보
                mRetriever = new MediaMetadataRetriever();
                mRetriever.setDataSource(this, uri);

                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(this, uri);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Toast.makeText(MusicService.this, "음악을 재생합니다", Toast.LENGTH_SHORT).show();
                        mp.start();


                        /**
                         * {@link com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment#updateResumeImageview(Boolean)}
                         * {@link com.hj.user.musicplayerproject.fragments.MainFragments.PlayerFragment#updateUI(Boolean)}
                         */
                        EventBus.getDefault().post(isPlaying());


                        /**
                         * {@link com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment#updateAlbumImage(MediaMetadataRetriever)}
                         */
                        EventBus.getDefault().post(mRetriever);

                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 다음 uri값 구해서 넣어주기 <- id++로


//                        if (mRealm.where(MusicFile.class).equalTo("id", nextId).count() != 0) {

                        playMusicList(nextId);
//                        }
                    }
                });

                // foreground service
//            showNotification();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public void nextMusic() {
        currentId++;

        if (currentId > mRealm.where(MusicFile.class).count()) {
            currentId = 1;
        }
        playMusicList(currentId);
    }

    public void prevMusic() {
        currentId--;
        if (currentId < 1) {
            currentId = (int) mRealm.where(MusicFile.class).count();
        }
        playMusicList(currentId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

}
