package com.example.user.musicplayerproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.user.musicplayerproject.MusicFile;
import com.example.user.musicplayerproject.R;
import com.example.user.musicplayerproject.activities.SelectSongActivity;
import com.example.user.musicplayerproject.adapters.ListViewAdapter;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by USER on 2017-03-22.
 */

public class SongFragment extends Fragment implements View.OnClickListener {


    public static final int PICK_AUDIO_REQUEST_CODE = 1000;
    public static final int MOVE_SELECTSONG_REQUEST_CODE = 1000;
    private Realm mRealm;
    private ListViewAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 렘 초기화
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 렘 닫기
        mRealm.close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO ListView - RealmAdapter만들어 꽂기
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.requestFocusFromTouch();


        Button pickButton = (Button) view.findViewById(R.id.pick_button);
        pickButton.setOnClickListener(this);


        // OrderedRealmCollection <MusicFile> 생성
        RealmResults<MusicFile> musicFileRealmResults = mRealm.where(MusicFile.class).findAll();

        adapter = new ListViewAdapter(musicFileRealmResults);
        listView.setAdapter(adapter);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // "가져오기" 버튼 눌렀을 때
            case R.id.pick_button:
                Intent intent = new Intent(getContext(), SelectSongActivity.class);
                // TODO 이벤트 버스로 바꿔야 하나?
                // 주거니 받거니
                getActivity().startActivityForResult(intent, MOVE_SELECTSONG_REQUEST_CODE);
                getActivity().overridePendingTransition(0, 0);
                break;

            default:
                break;

        }
    }

    public void scrollDown () {
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount());
    }

}
