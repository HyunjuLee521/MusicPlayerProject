package com.hj.user.musicplayerproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hj.user.musicplayerproject.MusicFile;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.fragments.SelectSongBySongFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.realm.Realm;

public class SelectSongActivity extends AppCompatActivity {

    private SelectSongBySongFragment mSelectSongBySongFragment;
    private Realm mRealm;
    private ArrayList<Uri> selectedSongUriList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        // 렘 초기화
        mRealm = Realm.getDefaultInstance();

        // 프래그먼트 초기화
        mSelectSongBySongFragment = new SelectSongBySongFragment();

        // 뷰페이저에 어댑터 꽂기
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);


        // "플레이리스트에 추가하기" 버튼 눌렀을 때
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        selectedSongUriList = mSelectSongBySongFragment.getSelectedSongUriArrayList();
                        break;

                    default:
                        break;
                }


                if (selectedSongUriList.size() > 0) {
//                    mRealm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            if (mRealm.where(MusicFile.class).count() > 0) {
//                                mRealm.where(MusicFile.class).findAll().deleteAllFromRealm();
//                            }
//                        }
//                    });

                    for (Uri uri : selectedSongUriList) {
                        getSongToPlaylist(uri);
                    }

                    Toast.makeText(SelectSongActivity.this, selectedSongUriList.size() + " 개의 음악을 플레이리스트에 추가합니다"
                            , Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    Toast.makeText(SelectSongActivity.this, "선택된 음악이 없습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mSelectSongBySongFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 렘 닫기
        mRealm.close();
    }


    // uri값 받아와서
    // Realm 테이블 MusicFile에 저장
    public void getSongToPlaylist(Uri uri) {

        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(MyUtils.getRealPath(this, uri));
        retriever.setDataSource(this, uri);

        // 미디어 정보
        final String mUri = uri.toString();
        final String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        final String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
        final String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

        // 오디오 앨범 자켓 이미지
        // bitmap -> String으로 변환하여 저장
        final String image;

//             오디오 앨범 자켓 이미지
        byte albumImage[] = retriever.getEmbeddedPicture();
        if (null != albumImage) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
            image = BitMapToString(bitmap);
        } else {
            image = "nothing";
        }




        // 렘에 저장
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                MusicFile musicFile = mRealm.createObject(MusicFile.class);

                musicFile.setUri(mUri);
                musicFile.setArtist(artist);
                musicFile.setTitle(title);
                musicFile.setImage(image);
                musicFile.setDuration(duration);


                // TODO id값 부여
                Number currentIdNum = mRealm.where(MusicFile.class).max("id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                musicFile.setId(nextId);
                mRealm.insertOrUpdate(musicFile); // using insert API


            }
        });


        if (mRealm.where(MusicFile.class).count() > 0) {
            MusicFile musicFile = mRealm.where(MusicFile.class).findFirst();
//            Toast.makeText(this, "MusicFile에 들어간 파일 : " + musicFile.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    public static String BitMapToString(Bitmap bitmap) {
        String temp;
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);

        } else {
            temp = "nothing";
        }

        return temp;
    }


}
