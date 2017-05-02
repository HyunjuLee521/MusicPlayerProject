package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.activities.SelectSongActivity;
import com.hj.user.musicplayerproject.adapters.PlaylistRecyclerviewAdapter;
import com.hj.user.musicplayerproject.models.MusicFile;
import com.hj.user.musicplayerproject.services.MusicService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by USER on 2017-03-22.
 */

public class PlaylistFragment extends Fragment implements View.OnClickListener {


    public static final int PICK_AUDIO_REQUEST_CODE = 1000;
    public static final int MOVE_SELECTSONG_REQUEST_CODE = 1000;

    private Realm mRealm;
    private PlaylistRecyclerviewAdapter adapter2;

    private RecyclerView mRecyclerview;


    private ArrayList<byte[]> mAlbumimageByteArraylist;
    private Button mPickButton;
    private RealmResults<MusicFile> musicFileRealmResults;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 렘 초기화
        mRealm = Realm.getDefaultInstance();
        // TODO 오류 : 렘 마이그레이션?

        mAlbumimageByteArraylist = new ArrayList<byte[]>();

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

    // 메뉴 붙이기
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuInflater menuIflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editting:
                /**
                 * {@link com.hj.user.musicplayerproject.activities.MainActivity#changeLayoutToEdit(String)}
                 */
                if (item.getTitle().equals("편집")) {
                    item.setTitle("확인");
                    EventBus.getDefault().post("확인");
                    mPickButton.setVisibility(View.INVISIBLE);

                    mRecyclerview.setAdapter(adapter2);

                } else {
                    item.setTitle("편집");
                    EventBus.getDefault().post("편집");
                    mPickButton.setVisibility(View.VISIBLE);
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mRecyclerview = (RecyclerView) view.findViewById(R.id.recycler_view);
//        mRecyclerview.requestFocusFromTouch();


        mPickButton = (Button) view.findViewById(R.id.pick_button);
        mPickButton.setOnClickListener(this);


        // OrderedRealmCollection <MusicFile> 생성
        musicFileRealmResults = mRealm.where(MusicFile.class).findAll();


        mAlbumimageByteArraylist.clear();

        for (int i = 1; i <= mRealm.where(MusicFile.class).count(); i++) {
            MusicFile musicFile = mRealm.where(MusicFile.class).equalTo("id", i).findFirst();

            // retriever값 가져와 -> 이미지 byte 꺼내기
            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(MyUtils.getRealPath(this, uri));
            retriever.setDataSource(getContext(), Uri.parse(musicFile.getUri()));
//             오디오 앨범 자켓 이미지
            final byte albumImage[] = retriever.getEmbeddedPicture();
            mAlbumimageByteArraylist.add(albumImage);

        }


        Toast.makeText(getContext(), "mAlbumimageByteArraylist에 담긴 데이터 갯수 " + mAlbumimageByteArraylist.size(), Toast.LENGTH_SHORT).show();

        adapter2 = new PlaylistRecyclerviewAdapter(getContext(), musicFileRealmResults);
        mRecyclerview.setAdapter(adapter2);


        // TODO RecyclerView itemClick시


        adapter2.setOnItemClickListener(new PlaylistRecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Uri uri, int position) {

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
        if (mRealm.where(MusicFile.class).count() > 0) {
//            listView.setSelection((int) mRealm.where(MusicFile.class).count() - 1);

            int maxItem = (int) mRealm.where(MusicFile.class).count() - 1;
            mRecyclerview.scrollToPosition(maxItem);


        }
    }

}
