package com.hj.user.musicplayerproject.fragments.SelectSongFragments;

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
import com.hj.user.musicplayerproject.models.ArtistName;

import org.greenrobot.eventbus.EventBus;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.selectsongbysong_recyclerview);


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

        private ArrayList<ArtistName> mArtistNameData;
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

            mArtistNameData = new ArrayList<ArtistName>();
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


            // 아티스트 뽑기
            boolean isDuplicated = false;
            for (ArtistName artistName : mArtistNameData) {
                if (artistName.getName().equals(artist)) {
                    artistName.setCnt(artistName.getCnt() + 1);
                    isDuplicated = true;
//                    Toast.makeText(mContext, "data갯수 : " + mArtistNameData.size() + " /중복된 아티스트 " + artistName.getName() + "에 넣는다" + artistName.getCnt(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if (!isDuplicated) {
                mArtistNameData.add(new ArtistName(artist, 1));
//                Toast.makeText(mContext, "data갯수 : " + mArtistNameData.size() + " /새로운 아티스트 " + artist + "에 cnt1 넣는다", Toast.LENGTH_SHORT).show();
            }


            // TODO Realm으로 뽑기
//            Realm realm = Realm.getDefaultInstance();
//
//            // 렘에 아티스트 이름 저장
//            if (realm.where(ArtistFile.class).equalTo("artistName", artist).count() > 0) {
//                // 기존에 저장되어있던 artist라면 , count + 1  (update)
//
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        ArtistFile mArtist = realm.where(ArtistFile.class)
//                                .equalTo("artistName", artist)
//                                .findFirst();
//
//                        mArtist.setCount(mArtist.getCount() + 1);
//                    }
//                });
//
//            } else {
//                // 처음 나온 artist라면 , id값 부여하고 count = 1로 하여 렘에 저장
//
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//
//                        ArtistFile artistFile = realm.createObject(ArtistFile.class);
//                        artistFile.setArtistName(artist);
//                        artistFile.setCount(1);
//
//                        // TODO id값 부여
//                        Number currentIdNum = realm.where(ArtistFile.class).max("id");
//                        int nextId;
//                        if (currentIdNum == null) {
//                            nextId = 1;
//                        } else {
//                            nextId = currentIdNum.intValue() + 1;
//                        }
//                        artistFile.setId(nextId);
//
//                        realm.insertOrUpdate(artistFile); // using insert API
//
//                    }
//                });
//            }
//
//
//            realm.close();


//            viewHolder.artistTextView.setText(artist);

            if (cursor.isLast()) {
//                Toast.makeText(mContext, "마지막 cursor에 저장되어있는 data갯수 : " + mArtistNameData.size(), Toast.LENGTH_SHORT).show();
                /**
                 * {@link com.hj.user.musicplayerproject.fragments.SelectSongFragments.SelectSongByArtistFragment#getArtistNameList(ArrayList<ArtistName>)}
                 */
                EventBus.getDefault().post(mArtistNameData);

            }


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
