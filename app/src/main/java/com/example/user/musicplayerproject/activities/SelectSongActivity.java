package com.example.user.musicplayerproject.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.user.musicplayerproject.R;
import com.example.user.musicplayerproject.fragments.SelectSongBySongFragment;

public class SelectSongActivity extends AppCompatActivity {

    private SelectSongBySongFragment mSelectSongBySongFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        mSelectSongBySongFragment = new SelectSongBySongFragment();


        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

//        viewPager.getCurrentItem()


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


}
