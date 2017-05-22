package com.hj.user.musicplayerproject.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.activities.MainActivity;
import com.hj.user.musicplayerproject.models.FavoriteMusicFile;
import com.hj.user.musicplayerproject.models.MusicFile;
import com.hj.user.musicplayerproject.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by USER on 2017-03-31.
 */

public class MusicService extends Service {

    // PlaylistFragment
    // 리스트뷰에서 아이템(곡) 클릭
    public static String ACTION_PLAY = "play";

    public static String ACTION_FAVORITE_PLAY = "favorite";

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

    private Uri currentUri;
    private int currentIndex;

    private int currentId;


    private ArrayList<Uri> mSongUriList;


    private int mIndex = 0;

    private int mPlayModeDefaultIs1andFavoriteIs2;
    private String playMode;
    private MediaSessionCompat mSession;


    @Override
    public void onCreate() {
        super.onCreate();
        // 렘 초기화
        mRealm = Realm.getDefaultInstance();

        mAudioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);

        // 이벤트 버스 연결
        EventBus.getDefault().register(this);


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
        mSongUriList = new ArrayList<Uri>();

        playMode = "repeat";


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 이벤트 버스 해제
        EventBus.getDefault().unregister(this);

        // 렘 닫기
        mRealm.close();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                requestAudioFocus();
                mPlayModeDefaultIs1andFavoriteIs2 = 1;

//                getSongUriFromRealm(1);

                if (playMode.equals("shuffle")) {
                    getShuffleSongUri(mPlayModeDefaultIs1andFavoriteIs2);
                } else if (playMode.equals("play_one")) {
                    getRepeatOneSongUri();
                } else {
                    getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);
                }

                // 방법2 : uri
                currentUri = Uri.parse(intent.getStringExtra("uri"));
                int position = intent.getIntExtra("position", -1);

                playMusicList2(currentUri, position);


            } else if (ACTION_RESUME.equals(action) && mMediaPlayer != null) {
                if (playMode.equals("shuffle")) {
                    getShuffleSongUri(mPlayModeDefaultIs1andFavoriteIs2);
                } else if (playMode.equals("play_one")) {
                    getRepeatOneSongUri();
                } else {
                    getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);
                }

                clickResumeButton();
            } else if (ACTION_PREV.equals(action) && mMediaPlayer != null) {

                prevMusic3();
            } else if (ACTION_NEXT.equals(action) && mMediaPlayer != null) {


                nextMusic3();
            } else if (ACTION_FAVORITE_PLAY.equals(action)) {
                requestAudioFocus();
                mPlayModeDefaultIs1andFavoriteIs2 = 2;

//                getSongUriFromRealm(2);

                if (playMode.equals("shuffle")) {
                    getShuffleSongUri(mPlayModeDefaultIs1andFavoriteIs2);
                } else if (playMode.equals("play_one")) {
                    getRepeatOneSongUri();
                } else {
                    getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);
                }

                currentUri = Uri.parse(intent.getStringExtra("uri"));
                int position = intent.getIntExtra("position", -1);

                playMusicList2(currentUri, position);

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

    public Uri getCurrentUri() {
        return currentUri;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getCurrentId() {
        return currentId;
    }


    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }


    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> uriArraylist = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String uri = a.optString(i);
                    uriArraylist.add(uri);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return uriArraylist;
    }


    public void nextMusic3() {

//        if (mPlayModeDefaultIs1andFavoriteIs2 == 1) {
//            getSongUriFromRealm(1);
//        } else if (mPlayModeDefaultIs1andFavoriteIs2 == 2) {
//            getSongUriFromRealm(2);
//        }

        if (playMode.equals("shuffle")) {
            getShuffleSongUri(mPlayModeDefaultIs1andFavoriteIs2);
        } else if (playMode.equals("play_one")) {
            getRepeatOneSongUri();
        } else {
            getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);
        }

        mIndex++;
        if (mIndex > mSongUriList.size() - 1) {
            mIndex = 0;
        }
        playMusicList2(mSongUriList.get(mIndex), mIndex);
    }


    public void prevMusic3() {

//        if (mPlayModeDefaultIs1andFavoriteIs2 == 1) {
//            getSongUriFromRealm(1);
//        } else if (mPlayModeDefaultIs1andFavoriteIs2 == 2) {
//            getSongUriFromRealm(2);
//        }

        if (playMode.equals("shuffle")) {
            getShuffleSongUri(mPlayModeDefaultIs1andFavoriteIs2);
        } else if (playMode.equals("play_one")) {
            getRepeatOneSongUri();
        } else {
            getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);
        }

        mIndex--;
        if (mIndex < 0) {
            mIndex = mSongUriList.size() - 1;
        }
        playMusicList2(mSongUriList.get(mIndex), mIndex);

    }


    // Uri 값으로 플레이리스트 재생
    private void playMusicList2(final Uri uri, final int index) {
        try {
            currentUri = uri;
            currentIndex = index;

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

//                    getSongUriListFromRealm();

                    // TODO
//                    mIndex = mSongUriList.indexOf(uri);
                    mIndex = index;

//
//                    Playlist playlist = mRealm.where(Playlist.class)
//                            .findFirst();
//
//                    ArrayList<Uri> arrayList = playlist.getUriArrayList();
//
//                    mIndex = arrayList.indexOf(uri);


//                    Toast.makeText(MusicService.this, mIndex + "번 째 음악을 재생합니다", Toast.LENGTH_SHORT).show();
                    mp.start();


                    /**
                     * {@link com.hj.user.musicplayerproject.fragments.MainFragments.PlayerFragment#updateUI2(Boolean)}
                     * {@link com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment#updateResumeImageview(Boolean)}
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
                    nextMusic3();
                }
            });

            // foreground service
//            showNotification();

            getLolliNotification();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showNotification() {
        String title = mRetriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        String artist = mRetriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle(title);
//        builder.setContentText(artist);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view);
        remoteViews.setTextViewText(R.id.title_text, title);
        remoteViews.setTextViewText(R.id.artist_text, artist);

        builder.setCustomContentView(remoteViews);

        Bitmap bitmap = BitmapFactory.decodeResource(
                getResources(), R.mipmap.ic_launcher);

//        builder.setLargeIcon(bitmap);

        remoteViews.setImageViewBitmap(R.id.album_image, bitmap);

        // 알림을 클릭하면 수행될 인텐트
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // 클릭하면 날리기
        builder.setAutoCancel(true);

        // 색상
        builder.setColor(Color.YELLOW);

        // 기본 알림음
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        // 진동
        builder.setVibrate(new long[]{100, 200, 300});

        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction(ACTION_RESUME);
        PendingIntent stopPendingIntent = PendingIntent.getService(this,
                1, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 액션
//        builder.addAction(R.mipmap.ic_launcher, "중지", stopPendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "다음곡", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "이전곡", pendingIntent);

        // 알림 표시
        startForeground(1, builder.build());


    }


    private void getLolliNotification() {

        String title = mRetriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        String artist = mRetriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));

        MediaMetadataCompat metadataCompat = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .build();

        if (mSession == null) {
            mSession = new MediaSessionCompat(this, "tag", null, null);
            mSession.setMetadata(metadataCompat);
            mSession.setActive(true);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        builder.setShowWhen(false);
        builder.setStyle(new NotificationCompat.MediaStyle()
                .setMediaSession(mSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2));
        builder.setColor(Color.parseColor("#2196F3"));

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(artist);


//        if (mSession == null) {
//            mSession = new MediaSessionCompat(this, "tag", null, null);
//            mSession.setMetadata(metadataCompat);
//        }

        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction("stop");

        PendingIntent pendingIntent = PendingIntent.getService
                (this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
//        builder.setContentTitle(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
//        builder.setContentText(metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
//        builder.setSmallIcon(R.drawable.ic_audiotrack_black_24dp);
//        builder.setShowWhen(false);
//
//        MediaSessionCompat mSession;
//
//
//        builder.setStyle(new NotificationCompat.MediaStyle()
//                .setMediaSession(mSession.getSessionToken())
//                .setShowActionsInCompactView(0, 1, 2)
//                .setShowCancelButton(true));
//        builder.setColor(0xFFDB4437);


        //builder.setLargeIcon(metadataCompat.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
        builder.addAction(R.drawable.ic_fast_rewind_black_24dp, "prew", null);
        builder.addAction(R.drawable.ic_pause_circle_outline_black_24dp, "pause", null);
        builder.addAction(R.drawable.ic_fast_forward_black_24dp, "next", null);

        // 본체 눌렀을때 동작 설정
        Intent launchMusicActivity = new Intent(this, MainActivity.class);
        PendingIntent sender = PendingIntent.getActivity(this, 1, launchMusicActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(sender);


        // builder.setStyle(new NotificationCompat.Style())
//        return builder.build();


        startForeground(1, builder.build());
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
//        setIdPref(MusicService.this, "id", currentId);

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


                        // 이벤트 버스
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


    // 렘 -> 어레이리스트로 바꾸기
    public ArrayList<MusicFile> getModelList1() {
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

    public ArrayList<FavoriteMusicFile> getModelList2() {
        ArrayList<FavoriteMusicFile> list = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<FavoriteMusicFile> results = realm
                    .where(FavoriteMusicFile.class)
                    .findAll();
            list.addAll(realm.copyFromRealm(results));
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }


    public void getSongUriFromRealm(int mode) {

        switch (mode) {
            case 1:
                // 모드 1 - 재생목록
                ArrayList<MusicFile> arrayList1 = new ArrayList<>();
                arrayList1 = getModelList1();

//        ArrayList<String> temp = new ArrayList<String>();
//        temp.clear();

                // mSongUriList.clear();
                mSongUriList.clear();

                for (MusicFile musicFile : arrayList1) {
                    mSongUriList.add(Uri.parse(musicFile.getUri()));
//            temp.add(musicFile.getUri());
                }

//        Toast.makeText(this, "렘 어레이리스트로 바꾼 크기" + arrayList.size()
//                + "\n mSongUriList에 담긴 크기 " + mSongUriList.size(), Toast.LENGTH_SHORT).show();
//        setStringArrayPref(this, "uriList", temp);

                break;

            case 2:
                // 모드 2 - 즐겨찾기
                ArrayList<FavoriteMusicFile> arrayList2 = new ArrayList<>();
                arrayList2 = getModelList2();

                mSongUriList.clear();

                for (FavoriteMusicFile favoriteMusicFile : arrayList2) {
                    mSongUriList.add(Uri.parse(favoriteMusicFile.getUri()));
                }

                break;

            default:
                break;

        }

    }

    // TODO 플레이 모드 변경시
    @Subscribe
    public void getChangedPlayMode(MyUtils.changePlayModeEvent event) {
//        Toast.makeText(this, "변경된 플레이모드 " + event.playMode, Toast.LENGTH_SHORT).show();
        playMode = event.playMode;
//        if (playMode.equals("repeat")) {
//            getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);
//
//        } else if (playMode.equals("shuffle")) {
//            getShuffleSongUri(mPlayModeDefaultIs1andFavoriteIs2);
//
//        } else if (playMode.equals("play_one")) {
//            getRepeatOneSongUri();
//        }

    }

    // 한곡 반복
    private void getRepeatOneSongUri() {
        mSongUriList.clear();
        mSongUriList.add(currentUri);
    }

    // 셔플
    private void getShuffleSongUri(int mPlayModeDefaultIs1andFavoriteIs2) {
        getSongUriFromRealm(mPlayModeDefaultIs1andFavoriteIs2);

        int size = mSongUriList.size();
        ArrayList<Integer> orderedArraylist = new ArrayList<>(size);

        int randomIndex;
        boolean isValid;

        while (orderedArraylist.size() < size) {
            do {
                Random random = new Random();
                randomIndex = random.nextInt(size);

                if (orderedArraylist.contains((Integer) randomIndex)) {
                    isValid = false;
                } else {
                    isValid = true;
                    orderedArraylist.add(randomIndex);
                }

            } while (!isValid);
        }


//            Toast.makeText(this, "어레이리스트 size " + orderedArraylist.size()
//                    + "\n 어레이리스트 toString " + orderedArraylist.toString(), Toast.LENGTH_SHORT).show();


        ArrayList<Uri> newSongUriList = new ArrayList<>();
        for (int i = 0; i < mSongUriList.size(); i++) {
            int randomOrder = orderedArraylist.get(i);
            newSongUriList.add(mSongUriList.get(randomOrder));
        }


        mSongUriList.clear();
        orderedArraylist.clear();

        for (int i = 0; i < newSongUriList.size(); i++) {
            mSongUriList.add(newSongUriList.get(i));
        }

        newSongUriList.clear();

        Toast.makeText(this, "mSongUriList.toString " + mSongUriList.toString(), Toast.LENGTH_SHORT).show();
    }

}
