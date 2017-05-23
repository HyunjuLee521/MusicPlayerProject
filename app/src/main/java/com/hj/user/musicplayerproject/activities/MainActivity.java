package com.hj.user.musicplayerproject.activities;

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

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.fragments.MainFragments.EditControllerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.FavoritePlaylistFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.PlayerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.PlaylistFragment;
import com.hj.user.musicplayerproject.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    // git에 올리기
    private PlayerFragment mPlayerFragment;
    private PlaylistFragment mPlaylistFragment;
    private FavoritePlaylistFragment mFavoritePlaylistFragment;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static String ACTION_RESTART_MAIN = "restart_main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().getAction().equals(ACTION_RESTART_MAIN)) {
            /**
             * {@link com.hj.user.musicplayerproject.services.MusicService#getRestartMainSignal(MyUtils.restartMainEvent)}
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


        // 처음에 띄어줄 화면
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                new MusicControllerFragment()).commit();


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    /**
                     * {@link com.hj.user.musicplayerproject.fragments.MainFragments.PlaylistFragment#changeMode1Adapter(Integer)}
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
    public void finishMainActivity (MyUtils.finishEvent event) {
        finish();
    }




}
