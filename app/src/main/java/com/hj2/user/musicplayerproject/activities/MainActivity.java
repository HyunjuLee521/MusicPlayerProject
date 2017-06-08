package com.hj2.user.musicplayerproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hj2.user.musicplayerproject.R;
import com.hj2.user.musicplayerproject.fragments.MainFragments.EditControllerFragment;
import com.hj2.user.musicplayerproject.fragments.MainFragments.FavoritePlaylistFragment;
import com.hj2.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment;
import com.hj2.user.musicplayerproject.fragments.MainFragments.PlayerFragment;
import com.hj2.user.musicplayerproject.fragments.MainFragments.PlaylistFragment;
import com.hj2.user.musicplayerproject.services.MusicService;
import com.hj2.user.musicplayerproject.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    // git에 올리기
    private PlayerFragment mPlayerFragment;
    private PlaylistFragment mPlaylistFragment;
    private FavoritePlaylistFragment mFavoritePlaylistFragment;

    private MusicControllerFragment mMusicControllerFragment;
    private EditControllerFragment mEditControllerFragment;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static String ACTION_RESTART_MAIN = "restart_main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().getAction().equals(ACTION_RESTART_MAIN)) {
            /**
             * {@link MusicService#getRestartMainSignal(MyUtils.restartMainEvent)}
             */
            MyUtils.restartMainEvent event = new MyUtils.restartMainEvent(1);
            EventBus.getDefault().post(event);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayerFragment = new PlayerFragment();
        mPlaylistFragment = new PlaylistFragment();
        mFavoritePlaylistFragment = new FavoritePlaylistFragment();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);

        mMusicControllerFragment = new MusicControllerFragment();

//        // 처음에 띄어줄 화면
//        getSupportFragmentManager().beginTransaction().add(R.id.container,
//                new MusicControllerFragment()).commit();


        getSupportFragmentManager().beginTransaction().add(R.id.container,
                mMusicControllerFragment).commit();


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    // TODO
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            mMusicControllerFragment).commit();

                    /**
                     * {@link PlaylistFragment#clearAllSelectedItem(MyUtils.clearSelectedItemEvent)}
                     */

                    MyUtils.clearSelectedItemEvent event = new MyUtils.clearSelectedItemEvent(true);
                    EventBus.getDefault().post(event);

                }
                if (position == 1) {
                    /**
                     * {@link PlaylistFragment#changeMode1Adapter(Integer)}
                     */
                    EventBus.getDefault().post(position);

                    mPlaylistFragment.viewPickbutton();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                    return mPlayerFragment;
                case 1:
                    return mPlaylistFragment;

                case 2:
                    return mFavoritePlaylistFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "플레이어";
                case 1:
                    return "재생목록";
                case 2:
                    return "즐겨찾기";
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPlaylistFragment.scrollDown();


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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

//        getSupportFragmentManager().beginTransaction().replace(R.id.container,
//                new MusicControllerFragment()).commit();

    }


    // TODO 바꾸기

    @Subscribe
    public void changeLayoutToEdit(String str) {

//        Toast.makeText(this, "도착 값 " + str, Toast.LENGTH_SHORT).show();

        if (str.equals("편집")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MusicControllerFragment()).commit();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new EditControllerFragment()).commit();
        }

    }


    @Subscribe
    public void finishMainActivity(MyUtils.finishEvent event) {
        finish();
    }


}
