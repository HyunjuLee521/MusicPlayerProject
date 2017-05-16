package com.hj.user.musicplayerproject.fragments.SelectSongFragments;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hj.user.musicplayerproject.CursorRecyclerViewAdapter;
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

    private Realm mRealm;

    private ArrayList<ArtistName> mData;

    private RecyclerView mRecyclerview;
    private SelectSongByArtistListVIewAdapter mAdapter1;
    private SongRecyclerAdapter mAdapter2;

    private int mAdapterStatus;

    private ArrayList<Uri> mUriArrayLIst;


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

        mAdapterStatus = 1;
        mAdapter1 = new SelectSongByArtistListVIewAdapter(getContext(), mData);

        // 가수 클릭시, 해당 가수의 곡 리스트 보여주기
        mAdapter1.setOnItemClickListener(new SelectSongByArtistListVIewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getContext(), "클릭된 아이템의 포지션 " + position, Toast.LENGTH_SHORT).show();

                if (mData != null) {
//                    Toast.makeText(getContext(), "mData != null", Toast.LENGTH_SHORT).show();

//        index = mPosition; // 클릭한 mPosition
                    int index = position;

                    // 커서 어댑터 만들어
                    String where = MediaStore.Audio.Media.ARTIST + " = ?";
                    String whereArgs[] = {mData.get(index).getName()};
                    Cursor audioListCursor = getActivity().getContentResolver()
                            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    null,
                                    where,
                                    whereArgs, null);

                    // RecyclerView - Adapter 갈아 끼우기
                    mAdapterStatus = 2;
                    mAdapter2 = new SongRecyclerAdapter(getContext(), audioListCursor, index);
                    mRecyclerview.setAdapter(mAdapter2);

                    // 각 아이템 클릭 시
                    mAdapter2.setOnItemClickListener(new SongRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Uri uri, int position) {
//                            if (view.getTag() == null || (boolean) view.getTag() == false) {
////                                view.setTag(true);
////                                view.setBackgroundColor(Color.RED);
//                                mUriArrayLIst.add(uri);
//
//                            } else {
////                                view.setTag(false);
////                                view.setBackgroundColor(Color.WHITE);
//                                mUriArrayLIst.remove(uri);
//                            }

                            mAdapter2.toggleSelection(position, uri);

                        }

                    });

                }
            }
        });

        mRecyclerview.setAdapter(mAdapter1);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerview = (RecyclerView) view.findViewById(R.id.selectsongbyartist_recyclerview);
//        if (mData != null) {
//            mAdapter1 = new SelectSongByArtistListVIewAdapter(getContext(), mData);
//            mListView.setAdapter(mAdapter1);
//        }

        // ArrayList 초기화
        mUriArrayLIst = new ArrayList<>();


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


    public int getAdapterStatus() {
        return mAdapterStatus;
    }

    public void changeAdapter() {
        mAdapterStatus = 1;
        mRecyclerview.setAdapter(mAdapter1);
    }


    // SongRecyclerAdapter

    // 어댑터
    public static class SongRecyclerAdapter extends CursorRecyclerViewAdapter<ViewHolder> {

        private Context mContext;
        private int mPosition;

        // 선택아이템담을 배열선언
        private SparseBooleanArray mSelectedItem;


        // 온아이템클릭시 -> 콜백 위한 처리들
        // 1. 보내줄 정보 인터페이스로 정리
        public interface OnItemClickListener {
            void onItemClick(View view, Uri uri, int position);
        }

        // 2. 변수로 갖기
        OnItemClickListener mListener;

        // 3. 변수 외부에서 세팅할수 있게 연결
        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }


        public SongRecyclerAdapter(Context context, Cursor cursor, int position) {
            super(context, cursor);
            mContext = context;
            mPosition = position;


            // 배열 초기화
            this.mSelectedItem = new SparseBooleanArray();

            // ArrayList 초기화
            mSelectedUriArraylist = new ArrayList<Uri>();

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_song_item, parent, false));
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {

            final int position = cursor.getPosition();

            // 아이템 클릭 시
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(
                        cursor.getColumnIndexOrThrow(BaseColumns._ID)));

                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(viewHolder.itemView, uri, position);
                    }

//                        if (v.getTag() == null || (boolean) v.getTag() == false) {
//                            v.setTag(true);
//                            v.setBackgroundColor(Color.RED);
////                                mUriArrayLIst.add(uri);
//
//                        } else {
//                            v.setTag(false);
//                            v.setBackgroundColor(Color.WHITE);
////                                mUriArrayLIst.remove(uri);
//                        }
                }
            });


            // content://audio/media/1"
            final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(
                    cursor.getColumnIndexOrThrow(BaseColumns._ID)));

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mContext, uri);

            // 미디어 정보
            String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
            String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
            String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));


            viewHolder.titleTextView.setText(title);
            viewHolder.artistTextView.setText(artist);


            // 선택됐을 때
            viewHolder.itemView.setActivated(mSelectedItem.get(position, false));
        }


        // 선택된 Uri 배열
        private ArrayList<Uri> mSelectedUriArraylist;

        public ArrayList<Uri> getSelectedUriList() {
            return mSelectedUriArraylist;
        }


        // 선택상태 토글
        public void toggleSelection(int position, Uri uri) {

            if (mSelectedItem.get(position, false)) {
                // 이미 선택되어 있을 때 -> 선택 해제

                mSelectedItem.delete(position);
                mSelectedUriArraylist.remove(uri);

//                Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
            } else {
                // 선택되어 있지 않을 때 -> 선택

                mSelectedItem.put(position, true);
                mSelectedUriArraylist.add(uri);

                //  view.setBackgroundColor(Color.YELLOW);
//                Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();

            }
            notifyItemChanged(position);

        }

        // 모든 항목의 선택상태 지우기
        public void clearSelection() {
            mSelectedItem.clear();
            notifyDataSetChanged();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView titleTextView;
        TextView artistTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.title_textview);
            artistTextView = (TextView) itemView.findViewById(R.id.artist_textview);
        }
    }

    // mUriArrayList return하는 메서드
    public ArrayList<Uri> getSelectedSongUriArrayList() {
        return mAdapter2.getSelectedUriList();
    }


}