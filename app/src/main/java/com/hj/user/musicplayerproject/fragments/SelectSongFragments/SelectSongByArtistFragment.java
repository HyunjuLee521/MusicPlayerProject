package com.hj.user.musicplayerproject.fragments.SelectSongFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.adapters.SelectSongByArtistListVIewAdapter;
import com.hj.user.musicplayerproject.models.ArtistName;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by USER on 2017-04-22.
 */

public class SelectSongByArtistFragment extends Fragment {


    private ListView mListView;
    private SelectSongByArtistListVIewAdapter mAdapter;

    private Realm mRealm;

    private ArrayList<ArtistName> mData;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_select_songbyartist, container, false);
    }

    @Subscribe
    public void getArtistNameList(ArrayList<ArtistName> mArtistNameData) {
//        Toast.makeText(getContext(), "SlectSongByArtistFragment의 getArtistNameList에 전달됨 : 마지막 cursor에 저장되어있는 data갯수 : " + mArtistNameData.size(), Toast.LENGTH_SHORT).show();

        mData = new ArrayList<ArtistName>();
        mData = mArtistNameData;

        mAdapter = new SelectSongByArtistListVIewAdapter(getContext(), mData);
        mListView.setAdapter(mAdapter);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.selectsongbyaritst_listview);
//        if (mData != null) {
//            mAdapter = new SelectSongByArtistListVIewAdapter(getContext(), mData);
//            mListView.setAdapter(mAdapter);
//        }


    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
