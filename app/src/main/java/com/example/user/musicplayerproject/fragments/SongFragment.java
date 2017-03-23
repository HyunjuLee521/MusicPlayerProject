package com.example.user.musicplayerproject.fragments;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.musicplayerproject.MusicFile;
import com.example.user.musicplayerproject.R;
import com.example.user.musicplayerproject.ustils.MyUtils;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;


/**
 * Created by USER on 2017-03-22.
 */

public class SongFragment extends Fragment implements View.OnClickListener {


    public static final int PICK_AUDIO_REQUEST_CODE = 1000;
    private Realm mRealm;

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        Button pickButton = (Button) view.findViewById(R.id.pick_button);
        pickButton.setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pick_button:
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Music"), PICK_AUDIO_REQUEST_CODE);

                break;


            default:
                break;


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 사진 PICK 해온 후 처리
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            final Uri uri = data.getData();
            // 테스트
//            Toast.makeText(getActivity(), uri + "", Toast.LENGTH_SHORT).show();

            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(MyUtils.getRealPath(getContext(), uri));


            // 미디어 정보
            final String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
            final String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
            final String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

            // 오디오 앨범 자켓 이미지
            final String image = retriever.getEmbeddedPicture().toString();

            // TODO 렘에 저장
            // 픽 한 음악 파일 렘에 저장
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MusicFile musicFile = mRealm.createObject(MusicFile.class);
                    musicFile.setArtist(artist);
                    musicFile.setTitle(title);
                    musicFile.setDuration(duration);
                    musicFile.setImage(image);
                    musicFile.setUri(uri.toString());

                }
            });




            // TODO
            // 가져온 파일 리스트로 뿌리기
            RealmResults<MusicFile> results = mRealm.where(MusicFile.class).findAll();

        }
    }

}
