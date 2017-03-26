package com.example.user.musicplayerproject.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.musicplayerproject.CursorRecyclerViewAdapter;
import com.example.user.musicplayerproject.MusicFile;
import com.example.user.musicplayerproject.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by USER on 2017-03-26.
 */

public class SelectSongBySongFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Button mGetPlaylistButton;
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
        mGetPlaylistButton = (Button) view.findViewById(R.id.get_playlist_button);


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


        // "플레이리스트로 가져오기" 버튼 클릭시
        mGetPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUriArrayLIst.size() > 0) {

//                    mRealm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            if (mRealm.where(MusicFile.class).count() > 0) {
//                                mRealm.where(MusicFile.class).findAll().deleteAllFromRealm();
//                            }
//                        }
//                    });

                    // TODO 선택된 파일을 Realm 에 저장
                    for (Uri uri : mUriArrayLIst) {
                        getSongToPlaylist(uri);
                    }

                    Toast.makeText(getActivity(), mUriArrayLIst.size() + " 개의 음악을 플레이리스트에 추가합니다"
                            , Toast.LENGTH_SHORT).show();

                    // TODO 액티비티 닫기
                    Intent intent = new Intent();

                    getActivity().finish();

                } else {
                    Toast.makeText(getActivity(), "선택된 음악이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 렘 닫기
        mRealm.close();
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
        final String image;

//             오디오 앨범 자켓 이미지
        byte albumImage[] = retriever.getEmbeddedPicture();
        if (null != albumImage) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
            // TODO 비트맵 -> String으로 변환
            image = BitMapToString(bitmap);
        } else {
            image = "nothing";
        }


        // TODO 렘에 저장
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                MusicFile musicFile = mRealm.createObject(MusicFile.class);

                musicFile.setUri(mUri);
                musicFile.setArtist(artist);
                musicFile.setTitle(title);
                musicFile.setImage(image);
                musicFile.setDuration(duration);


            }
        });


        if (mRealm.where(MusicFile.class).count() > 0) {
            MusicFile musicFile = mRealm.where(MusicFile.class).findFirst();
//            Toast.makeText(this, "MusicFile에 들어간 파일 : " + musicFile.toString(), Toast.LENGTH_SHORT).show();
        }


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


    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    public static String BitMapToString(Bitmap bitmap) {
        String temp;
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);

        } else {
            temp = "nothing";
        }

        return temp;
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
