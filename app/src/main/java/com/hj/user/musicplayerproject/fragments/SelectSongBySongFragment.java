package com.hj.user.musicplayerproject.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hj.user.musicplayerproject.CursorRecyclerViewAdapter;
import com.hj.user.musicplayerproject.R;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by USER on 2017-03-26.
 */

public class SelectSongBySongFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Realm mRealm;
    private ArrayList<Uri> mUriArrayLIst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 렘 초기화
        mRealm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_select_songbysong, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        // ArrayList 초기화
        mUriArrayLIst = new ArrayList<>();

//        mGetPlaylistButton.setOnClickListener(this);

        Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);


        final SongRecyclerAdapter adapter = new SongRecyclerAdapter(getContext(), cursor);

        // 어댑터.아이템온클릭리스너
        adapter.setOnItemClickListener(new SongRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Uri uri) {

                if (view.getTag() == null || (boolean) view.getTag() == false) {
                    view.setTag(true);
                    view.setBackgroundColor(Color.RED);
                    mUriArrayLIst.add(uri);

                } else {
                    view.setTag(false);
                    view.setBackgroundColor(Color.WHITE);
                    mUriArrayLIst.remove(uri);
                }

//                Toast.makeText(SelectSongActivity.this, "view.getTag() : " + view.getTag(), Toast.LENGTH_SHORT).show();

            }
        });

        // 리사이클러 뷰에 어댑터 꽂기
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 렘 닫기
        mRealm.close();
    }


    // 어댑터
    public static class SongRecyclerAdapter extends CursorRecyclerViewAdapter<ViewHolder> {

        private Context mContext;

        // 온아이템클릭시 -> 콜백 위한 처리들
        // 1. 보내줄 정보 인터페이스로 정리
        public interface OnItemClickListener {
            void onItemClick(View view, Uri uri);
        }

        // 2. 변수로 갖기
        OnItemClickListener mListener;

        // 3. 변수 외부에서 세팅할수 있게 연결
        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }


        public SongRecyclerAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(
                        cursor.getColumnIndexOrThrow(BaseColumns._ID)));

                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(viewHolder.itemView, uri);
                    }
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

        }
    }

    // mUriArrayList return하는 메서드
    public ArrayList<Uri> getSelectedSongUriArrayList() {
        return mUriArrayLIst;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView titleTextView;
        TextView artistTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(android.R.id.text1);
            artistTextView = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }

}
