package com.hj.user.musicplayerproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.fragments.MainFragments.EditControllerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.MusicControllerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.PlayerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.PlaylistFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    // git에 올리기
    private PlayerFragment mPlayerFragment;
    private PlaylistFragment mPlaylistFragment;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayerFragment = new PlayerFragment();
        mPlaylistFragment = new PlaylistFragment();

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);


        // 처음에 띄어줄 화면
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                new MusicControllerFragment()).commit();


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            new MusicControllerFragment()).commit();
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
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
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


    @Subscribe
    public void changeLayoutToEdit(String str) {
//        Toast.makeText(this, "이벤트버스 수신 값 " + str, Toast.LENGTH_SHORT).show();

        if (str.equals("편집")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MusicControllerFragment()).commit();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new EditControllerFragment()).commit();
        }

    }

}
