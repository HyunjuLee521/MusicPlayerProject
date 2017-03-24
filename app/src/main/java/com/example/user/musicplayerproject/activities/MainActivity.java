package com.example.user.musicplayerproject.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.user.musicplayerproject.R;
import com.example.user.musicplayerproject.fragments.ListFragment;
import com.example.user.musicplayerproject.fragments.PlayerFragment;
import com.example.user.musicplayerproject.fragments.SongFragment;

public class MainActivity extends AppCompatActivity {

    // git에 올리기
    private PlayerFragment mPlayerFragment;
    private SongFragment mSongFragment;
    private ListFragment mListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayerFragment = new PlayerFragment();
        mSongFragment = new SongFragment();
        mListFragment = new ListFragment();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

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
                    return mSongFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}