package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.content.Intent;
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
import org.greenrobot.eventbus.Subscribe;

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
    private PlaylistRecyclerviewAdapter mAdapter1;
    private PlaylistRecyclerviewAdapter mAdapter2;

    private RecyclerView mRecyclerview;


    private Button mPickButton;
    private RealmResults<MusicFile> musicFileRealmResults;


    private ArrayList<Integer> mEditPlaylistSelectedId;
    private ArrayList<String> mEditPlaylistSelectedUri;
    private ArrayList<Integer> mEditPlaylistSelectedPosition;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 렘 초기화
        mRealm = Realm.getDefaultInstance();
        // TODO 오류 : 렘 마이그레이션?

        mEditPlaylistSelectedId = new ArrayList<Integer>();
        mEditPlaylistSelectedUri = new ArrayList<String>();
        mEditPlaylistSelectedPosition = new ArrayList<Integer>();

        // 초기화
//        mActionModeCallback = new ActionModeCallback();


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
        setHasOptionsMenu(true);

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


            // 방법 1
//            case R.id.action_editting:
//
//                // TODO
//                enableActionMode();
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);


            // 방법 2
            case R.id.action_editting:
                /**
                 * {@link com.hj.user.musicplayerproject.activities.MainActivity#changeLayoutToEdit(String)}
                 */
                if (item.getTitle().equals("편집")) {
                    // 편집 모드

                    item.setTitle("확인");
                    EventBus.getDefault().post("확인");
                    mPickButton.setVisibility(View.INVISIBLE);

                    mRecyclerview.setAdapter(mAdapter2);

                } else {
                    // 재생 모드


                    item.setTitle("편집");
                    EventBus.getDefault().post("편집");
                    mPickButton.setVisibility(View.VISIBLE);


                    mAdapter2.clearSelection();
                    mRecyclerview.setAdapter(mAdapter1);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void viewPickbutton() {
        mPickButton.setVisibility(View.VISIBLE);
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

        // TODO 이벤트버스로 보내기


//
//        for (int i = 1; i <= mRealm.where(MusicFile.class).count(); i++) {
//            MusicFile musicFile = mRealm.where(MusicFile.class).equalTo("id", i).findFirst();
//
//            // retriever값 가져와 -> 이미지 byte 꺼내기
//            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
////        retriever.setDataSource(MyUtils.getRealPath(this, uri));
//
//            retriever.setDataSource(getContext(), Uri.parse(musicFile.getUri()));
////             오디오 앨범 자켓 이미지
//            final byte albumImage[] = retriever.getEmbeddedPicture();
//
//        }


        mAdapter1 = new PlaylistRecyclerviewAdapter(getContext(), musicFileRealmResults, 1);
        mRecyclerview.setAdapter(mAdapter1);

        mAdapter2 = new PlaylistRecyclerviewAdapter(getContext(), musicFileRealmResults, 2);

        // TODO RecyclerView itemClick시


        mAdapter1.setOnItemClickListener(new PlaylistRecyclerviewAdapter.OnItemClickListener() {
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

                intent.setAction(MusicService.ACTION_PLAY);
                getActivity().startService(intent);

            }
        });


        mAdapter2.setOnItemClickListener(new PlaylistRecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Uri uri, int position, int id) {

                // 온클릭 리스너 안에서 액션모드 활성화 상태로 편집모드와 음악재생모드 구분

                mAdapter2.toggleSelection(String.valueOf(id), view, position);
                // toggleSelection(position);


                if (view.getTag() == null || (boolean) view.getTag() == false) {
                    mEditPlaylistSelectedId.add(id);
                    mEditPlaylistSelectedUri.add(uri.toString());
                    mEditPlaylistSelectedPosition.add(position);

                } else {
                    mEditPlaylistSelectedId.remove((Integer) id);
                    mEditPlaylistSelectedUri.remove(uri.toString());
                    mEditPlaylistSelectedPosition.remove((Integer) position);
                }

//                mAdapter2.setSelect(position);
//                mAdapter2.notifyItemChanged(position);

//                Toast.makeText(getContext(), "getTag : " + view.getTag() + " / arraylist size : " + mEditPlaylistSelectedId.size(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getContext(), "arraylist size " + mEditPlaylistSelectedId.size(), Toast.LENGTH_SHORT).show();
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

    @Subscribe
    public void changeMode1Adapter(Integer position) {
        if (mRecyclerview.getAdapter() != mAdapter1) {
            mRecyclerview.setAdapter(mAdapter1);
        }
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


    @Subscribe
    public void editPlaylist(Integer integer) {

        switch (integer) {
            case 2:
                // 위로
                moveUpSelectedItem();

                int size = mRealm.where(MusicFile.class).findAll().size();
                Toast.makeText(getContext(), "MusicFile.class 의 크기 " + size, Toast.LENGTH_SHORT).show();

                mAdapter2.notifyDataSetChanged();
                mRecyclerview.setAdapter(mAdapter2);



                break;


            case 4:
                // 삭제시
//                Toast.makeText(getContext(), "이벤트버스 도착 " + integer, Toast.LENGTH_SHORT).show();


//                Toast.makeText(getContext(), "deleteSelectedItem하기 전 " + mEditPlaylistSelectedId.toString(), Toast.LENGTH_SHORT).show();

                deleteSelectedItem();
                mAdapter2.notifyDataSetChanged();
                mRecyclerview.setAdapter(mAdapter2);

//                Toast.makeText(getContext(), "deleteSelectedItem하고 나서 " + mEditPlaylistSelectedId.toString(), Toast.LENGTH_SHORT).show();


//                Toast.makeText(getContext(), "들어옴", Toast.LENGTH_SHORT).show();
//                Toast.makeText(getContext(), "렘 음악갯수 " + mRealm.where(MusicFile.class).count(), Toast.LENGTH_SHORT).show();

                break;

            case 5:
                // 선택취소시
                mAdapter2.clearSelection();

                mEditPlaylistSelectedId.clear();
                mEditPlaylistSelectedUri.clear();
                mEditPlaylistSelectedPosition.clear();

                mRecyclerview.setAdapter(mAdapter2);


                break;


            default:
                break;

        }


    }

    private void deleteSelectedItem() {
//        for (final int position : mEditPlaylistSelectedId) {

        // 1. 렘에서 지우기
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                // 해당 포지션의 아이템 렘에서 지우기
//                    List<Integer> myList = new ArrayList<Integer>();
                Integer[] wrapperIdArr = mEditPlaylistSelectedId.toArray(new Integer[mEditPlaylistSelectedId.size()]);

                mRealm.where(MusicFile.class)
                        .in("id", wrapperIdArr)
                        .findAll()
                        .deleteAllFromRealm();


            }
        });

//        }


        // clear
        mEditPlaylistSelectedId.clear();
        mEditPlaylistSelectedUri.clear();
//        mEditPlaylistSelectedPosition.clear();

        mAdapter2.clearSelection();

    }


    private void moveUpSelectedItem() {

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


            }
        });


        // clear
        mEditPlaylistSelectedId.clear();
        mEditPlaylistSelectedUri.clear();
//        mEditPlaylistSelectedPosition.clear();

        mAdapter2.clearSelection();

    }

    private void moveDownSelectedItem() {


    }


}
