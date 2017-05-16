package com.hj.user.musicplayerproject.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.fragments.SelectSongFragments.SelectSongByArtistFragment;
import com.hj.user.musicplayerproject.fragments.SelectSongFragments.SelectSongBySongFragment;
import com.hj.user.musicplayerproject.models.MusicFile;
import com.hj.user.musicplayerproject.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;


public class SelectSongActivity extends AppCompatActivity {

    private SelectSongBySongFragment mSelectSongBySongFragment;
    private SelectSongByArtistFragment mSelectSongByArtistFragment;

    private Realm mRealm;
    private ArrayList<Uri> selectedSongUriList;
    private ViewPager mViewPager;

    private boolean page1isCreated;
    private Boolean songlistIsCreated;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);


        // 렘 초기화
        mRealm = Realm.getDefaultInstance();

        // 프래그먼트 초기화
        mSelectSongBySongFragment = new SelectSongBySongFragment();
        mSelectSongByArtistFragment = new SelectSongByArtistFragment();

        // 뷰페이저에 어댑터 꽂기
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter mAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        mTabLayout.setupWithViewPager(mViewPager);

        selectedSongUriList = new ArrayList<Uri>();

        // "플레이리스트에 추가하기" 버튼 눌렀을 때
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                    case 1:
                        if (mSelectSongBySongFragment.getSelectedSongUriArrayList() != null) {
                            addToSelectedSongUriList(mSelectSongBySongFragment.getSelectedSongUriArrayList());
                        }
                        if (page1isCreated && songlistIsCreated && mSelectSongByArtistFragment.getSelectedSongUriArrayList() != null) {
                            addToSelectedSongUriList(mSelectSongByArtistFragment.getSelectedSongUriArrayList());
                        }
//                        selectedSongUriList = mSelectSongBySongFragment.getSelectedSongUriArrayList();
                        Log.d("", "onClick: " + selectedSongUriList.toString());
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


//                    Intent intent = new Intent();
//                    setResult(RESULT_OK, intent);


                    /**
                     * {@link com.hj.user.musicplayerproject.services.MusicService#getSelectedUriArraylist(com.hj.user.musicplayerproject.utils.MyUtils.sendSongUriEvent)}
                     */


                    MyUtils.sendSongUriEvent event = new MyUtils.sendSongUriEvent(selectedSongUriList);
                    EventBus.getDefault().post(event);

                    finish();

                } else {
                    Toast.makeText(SelectSongActivity.this, "선택된 음악이 없습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // page 1 붙었는지
        page1isCreated = false;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    page1isCreated = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // songlist 붙었는지
        songlistIsCreated = false;
    }



    @Subscribe
    public void songlistIsCreated(Boolean isCreated) {
        songlistIsCreated = isCreated;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public void addToSelectedSongUriList(ArrayList<Uri> fragmentSongUriList) {
        for (Uri uri : fragmentSongUriList) {
            selectedSongUriList.add(uri);
        }
    }


    // mSelectSongByArtistFragment에서 mAdapter2가 세팅된 상황, 뒤로가기 눌렀을 때 -
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0
                && mViewPager.getCurrentItem() == 1
                && mSelectSongByArtistFragment.getAdapterStatus() == 2) {

            Toast.makeText(SelectSongActivity.this, "뒤로가기 테스트", Toast.LENGTH_SHORT).show();
            mSelectSongByArtistFragment.changeAdapter();

        } else {
            super.onBackPressed();
        }

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

                case 1:
                    return mSelectSongByArtistFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        // 제목 표시
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "전체곡";
                case 1:
                    return "아티스트";
            }
            return null;
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

                MusicFile musicFile = mRealm.createObject(MusicFile.class);

                musicFile.setUri(mUri);
                musicFile.setArtist(artist);
                musicFile.setTitle(title);
                musicFile.setDuration(duration);
                musicFile.setImage2(image2);


                // TODO id값 부여
                Number currentIdNum = mRealm.where(MusicFile.class).max("id");
                int nextId;
                if (currentIdNum == null) {
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
        }

    }


}
