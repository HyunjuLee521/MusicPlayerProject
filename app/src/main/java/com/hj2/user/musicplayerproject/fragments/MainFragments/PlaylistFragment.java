package com.hj2.user.musicplayerproject.fragments.MainFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hj2.user.musicplayerproject.R;
import com.hj2.user.musicplayerproject.activities.MainActivity;
import com.hj2.user.musicplayerproject.activities.SelectSongActivity;
import com.hj2.user.musicplayerproject.adapters.PlaylistRecyclerviewAdapter;
import com.hj2.user.musicplayerproject.models.MusicFile;
import com.hj2.user.musicplayerproject.services.MusicService;
import com.hj2.user.musicplayerproject.utils.MyUtils;

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
    private SparseBooleanArray mSelectedIdItem;


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
                 * {@link MainActivity#changeLayoutToEdit(String)}
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
//                Toast.makeText(getContext(), id + "", Toast.LENGTH_SHORT).show();

                // TODO 서비스 Intent 보내기
                Intent intent = new Intent(getActivity(), MusicService.class);
                intent.putExtra("id", id);
                intent.putExtra("uri", uri.toString());
                intent.putExtra("position", position);

                intent.setAction(MusicService.ACTION_PLAY);
                getActivity().startService(intent);

            }
        });


        mSelectedIdItem = new SparseBooleanArray();


        mAdapter2.setOnItemClickListener(new PlaylistRecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Uri uri, int position, int id) {

                // 온클릭 리스너 안에서 액션모드 활성화 상태로 편집모드와 음악재생모드 구분

                mAdapter2.toggleSelection(String.valueOf(id), view, position);
                // toggleSelection(position);


                if (mSelectedIdItem.get(position, false)) {
                    // 이미 선택되어 있을 때 -> 선택 해제

                    mSelectedIdItem.delete(position);

                    mEditPlaylistSelectedId.remove((Integer) id);
                    mEditPlaylistSelectedUri.remove(uri.toString());
                    mEditPlaylistSelectedPosition.remove((Integer) position);

//            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
                } else {
                    // 선택되어 있지 않을 때 -> 선택

                    mSelectedIdItem.put(position, true);
                    //  view.setBackgroundColor(Color.YELLOW);
//            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();

                    mEditPlaylistSelectedId.add(id);
                    mEditPlaylistSelectedUri.add(uri.toString());
                    mEditPlaylistSelectedPosition.add(position);
                }


//                Toast.makeText(getContext(), "mSelectedIdItem " + mSelectedIdItem.get(position) + " 선택된 아이디 갯수 " + mEditPlaylistSelectedId.toString(), Toast.LENGTH_SHORT).show();
//                mAdapter2.setSelect(position);
//                mAdapter2.notifyItemChanged(position);

//                Toast.makeText(getContext(), "getTag : " + view.getTag() + " / arraylist size : " + mEditPlaylistSelectedId.size(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getContext(), "arraylist size " + mEditPlaylistSelectedId.size(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Subscribe
    public void clearAllSelectedItem(MyUtils.clearSelectedItemEvent event) {
        mAdapter2.clearSelection();

        mSelectedIdItem.clear();

        mEditPlaylistSelectedId.clear();
        mEditPlaylistSelectedUri.clear();
        mEditPlaylistSelectedPosition.clear();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // "가져오기" 버튼 눌렀을 때
            case R.id.pick_button:

                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(getContext(), SelectSongActivity.class);
                        // 이벤트 버스로 바꿔야 하나? 순환참조 발생안한다! 그냥 써도 괜찮아
                        // 주거니 받거니
                        getActivity().startActivityForResult(intent, MOVE_SELECTSONG_REQUEST_CODE);
                        getActivity().overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    }
                };

                // TODO 메시지 안나옴
                new TedPermission(getContext()).setPermissionListener(permissionListener)
                        .setRationaleMessage("[필수권한] 이 기능은 외부 저장소에 접근 권한이 필요합니다.")
                        .setDeniedMessage(new StringModifier("[필수권한] 이 기능은 외부 저장소에 접근 권한이 필요합니다.")
                                .newLine()
                                .newLine()
                                .addText("설정 메뉴에서 언제든지 권한을 변경 할 수 있습니다. [설정] - [권한] 으로 이동하셔서 권한을 허용하신후 이용하시기 바랍니다.")
                                .end())
                        .setPermissions(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();


                break;

            default:
                break;

        }
    }


    public class StringModifier {
        private String string;

        public StringModifier(String string) {
            this.string = string;
        }

        /**
         * 해당 문자열에 줄바꿈을 적용한다.
         */
        public StringModifier newLine() {
            string += "\n";
            return this;
        }

        /**
         * 해당 문자열에 텍스트를 추가한다.
         */
        public StringModifier addText(CharSequence addedText) {
            string += addedText;
            return this;
        }

        /**
         * 해당 문자열에 trim 을 한다.
         */
        public StringModifier trim() {
            string = string.trim();
            return this;
        }

        /**
         * 최종적으로 모든 값이 적용된 문자열을 리턴한다.
         */
        public String end() {
            return string;
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
            case 4:
                // 삭제시
//                Toast.makeText(getContext(), "이벤트버스 도착 " + integer, Toast.LENGTH_SHORT).show();


//                Toast.makeText(getContext(), "deleteSelectedItem하기 전 " + mEditPlaylistSelectedId.toString(), Toast.LENGTH_SHORT).show();

                if (mEditPlaylistSelectedId.size() == 0) {
//                    Toast.makeText(getContext(), "선택된 아이템이 없습니다", Toast.LENGTH_SHORT).show();
                    mRecyclerview.setAdapter(mAdapter2);
                    break;
                }


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

        // clear
        mSelectedIdItem.clear();

        mEditPlaylistSelectedId.clear();
        mEditPlaylistSelectedUri.clear();
        mEditPlaylistSelectedPosition.clear();

        mAdapter2.clearSelection();

    }


    // uri값 받아와서
    // Realm 테이블 MusicFile에 저장
    public void getSongToPlaylist(Uri uri) {

        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(MyUtils.getRealPath(this, uri));
        retriever.setDataSource(getContext(), uri);

        // 미디어 정보
        final String mUri = uri.toString();
        final String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        final String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
        final String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

        // 오디오 앨범 자켓 이미지
        // bitmap -> String으로 변환하여 저장
        final byte[] image2;


//             오디오 앨범 자켓 이미지
        byte albumImage[] = retriever.getEmbeddedPicture();
        if (null != albumImage) {
            // 바이트 -> 비트맵
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
            // 비트맵 -> String
            image2 = albumImage;

        } else {
            image2 = null;

        }


        // 렘에 저장
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                MusicFile musicFile = mRealm.createObject(MusicFile.class);

                musicFile.setUri(mUri);
                musicFile.setArtist(artist);
                musicFile.setTitle(title);
                musicFile.setDuration(duration);
                musicFile.setImage2(image2);


                Number currentIdNum = mRealm.where(MusicFile.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 0;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                musicFile.setId(nextId);
                mRealm.insertOrUpdate(musicFile); // using insert API


            }
        });


        if (mRealm.where(MusicFile.class).count() > 0) {
            MusicFile musicFile = mRealm.where(MusicFile.class).findFirst();


        }

    }

}
