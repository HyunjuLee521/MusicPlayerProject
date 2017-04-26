package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hj.user.musicplayerproject.models.MusicFile;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.activities.SelectSongActivity;
import com.hj.user.musicplayerproject.adapters.PlaylistListViewAdapter;
import com.hj.user.musicplayerproject.services.MusicService;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by USER on 2017-03-22.
 */

public class PlaylistFragment extends Fragment implements View.OnClickListener {


    public static final int PICK_AUDIO_REQUEST_CODE = 1000;
    public static final int MOVE_SELECTSONG_REQUEST_CODE = 1000;
    private Realm mRealm;
    private PlaylistListViewAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 렘 초기화
        mRealm = Realm.getDefaultInstance();
        // TODO 오류 : 렘 마이그레이션?


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

        listView = (ListView) view.findViewById(R.id.list_view);
        listView.requestFocusFromTouch();


        Button pickButton = (Button) view.findViewById(R.id.pick_button);
        pickButton.setOnClickListener(this);


        // OrderedRealmCollection <MusicFile> 생성
        final RealmResults<MusicFile> musicFileRealmResults = mRealm.where(MusicFile.class).findAll();

        adapter = new PlaylistListViewAdapter(musicFileRealmResults);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 리스트뷰 아이템 클릭시 -> 해당 음악 아이템 서비스로 넘기기
                //
                // 해당 아이템의 아이디 토스트로 띄우기
                int temp = mRealm.where(MusicFile.class).equalTo("id", position + 1).findFirst().getId();
                Toast.makeText(getContext(), temp + "", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), MusicService.class);
                intent.putExtra("id", temp);
                intent.setAction(MusicService.ACTION_PLAY);
                getActivity().startService(intent);

            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // "가져오기" 버튼 눌렀을 때
            case R.id.pick_button:
                Intent intent = new Intent(getContext(), SelectSongActivity.class);
                // 이벤트 버스로 바꿔야 하나? 순환참조 발생안한다! 그냥 써도 괜찮아
                // 주거니 받거니
                getActivity().startActivityForResult(intent, MOVE_SELECTSONG_REQUEST_CODE);
                getActivity().overridePendingTransition(0, 0);
                break;

            default:
                break;

        }
    }

    public void scrollDown() {
        // TODO 오류 reference null object
        //        adapter.notifyDataSetChanged();
        listView.setSelection((int) mRealm.where(MusicFile.class).count() - 1);
    }

}
