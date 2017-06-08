package com.hj2.user.musicplayerproject.fragments.MainFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hj2.user.musicplayerproject.R;
import com.hj2.user.musicplayerproject.adapters.FavoritePlaylistRecyclerviewAdapter;
import com.hj2.user.musicplayerproject.models.FavoriteMusicFile;
import com.hj2.user.musicplayerproject.services.MusicService;

import io.realm.Realm;
import io.realm.RealmResults;

public class FavoritePlaylistFragment extends Fragment {

    private Realm mRealm;

    private RecyclerView mRecyclerview;
    private RealmResults<FavoriteMusicFile> favoriteMusicFileRealmResults;
    private FavoritePlaylistRecyclerviewAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_song_fragment, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerview = (RecyclerView) view.findViewById(R.id.recycler_view);

        favoriteMusicFileRealmResults = mRealm.where(FavoriteMusicFile.class).findAll();
        mAdapter = new FavoritePlaylistRecyclerviewAdapter (getContext(), favoriteMusicFileRealmResults);

        mRecyclerview.setAdapter(mAdapter);



        mAdapter.setOnItemClickListener(new FavoritePlaylistRecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Uri uri, int position, int id) {

                // 리스트뷰 아이템 클릭시 -> 해당 음악 아이템 서비스로 넘기기
                //
                // 해당 아이템의 아이디 토스트로 띄우기
//                int temp = mRealm.where(MusicFile.class).equalTo("id", id).findFirst().getId();
                Toast.makeText(getContext(), id + "", Toast.LENGTH_SHORT).show();

                // TODO 서비스 Intent 보내기
                Intent intent = new Intent(getActivity(), MusicService.class);
                intent.putExtra("id", id);
                intent.putExtra("uri", uri.toString());
                intent.putExtra("position", position);

                intent.setAction(MusicService.ACTION_FAVORITE_PLAY);
                getActivity().startService(intent);
            }
        });

    }
}
