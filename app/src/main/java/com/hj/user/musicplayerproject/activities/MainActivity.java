package com.hj.user.musicplayerproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.fragments.MainFragments.PlayerFragment;
import com.hj.user.musicplayerproject.fragments.MainFragments.PlaylistFragment;

public class MainActivity extends AppCompatActivity {

    // git에 올리기
    private PlayerFragment mPlayerFragment;
    private PlaylistFragment mPlaylistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayerFragment = new PlayerFragment();
        mPlaylistFragment = new PlaylistFragment();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

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
}
