package com.example.user.musicplayerproject.activities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.musicplayerproject.CursorRecyclerViewAdapter;
import com.example.user.musicplayerproject.MusicFile;
import com.example.user.musicplayerproject.R;

import java.util.ArrayList;

import io.realm.Realm;

public class SelectSongActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mGetPlaylistButton;
    private Realm mRealm;
    private ArrayList<Uri> mUriArrayLIst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mGetPlaylistButton = (Button) findViewById(R.id.get_playlist_button);

        // 렘 초기화
        mRealm = Realm.getDefaultInstance();

        // ArrayList 초기화
        mUriArrayLIst = new ArrayList<>();

//        mGetPlaylistButton.setOnClickListener(this);

        Cursor cursor = getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);


        final SongRecyclerAdapter adapter = new SongRecyclerAdapter(this, cursor);

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

                Toast.makeText(SelectSongActivity.this, "view.getTag() : " + view.getTag(), Toast.LENGTH_SHORT).show();


            }
        });

        mRecyclerView.setAdapter(adapter);


        mGetPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUriArrayLIst.size() > 0) {

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if(mRealm.where(MusicFile.class).count() > 0) {
                                mRealm.where(MusicFile.class).findAll().deleteAllFromRealm();
                            }
                        }
                    });

                    // TODO 선택된 파일을 Realm 에 저장
                    for (Uri uri : mUriArrayLIst) {
                        getSongToPlaylist(uri);
                    }


                    Toast.makeText(SelectSongActivity.this, "저장된 음악의 갯수" +
                            mRealm.where(MusicFile.class).count(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SelectSongActivity.this, "선택된 음악이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    // uri값 받아와서
    // Realm 테이블 MusicFile에 저장
    public void getSongToPlaylist(Uri uri) {

        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(MyUtils.getRealPath(this, uri));
        retriever.setDataSource(this, uri);

        // 미디어 정보
        final String mUri = uri.toString();
        final String title = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        final String artist = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST));
        final String duration = retriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_DURATION));

        // 오디오 앨범 자켓 이미지
        // bitmap -> String으로 변환하여 저장
        final String image = retriever.getEmbeddedPicture().toString();


        // TODO 렘에 저장
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                // 해당 값이 MusicFile에 기존에 이미 저장되어 있지 않을 때에만 처리
                if (realm.where(MusicFile.class)
                        .equalTo("uri", mUri)
                        .count() == 0) {

                    MusicFile musicFile = mRealm.createObject(MusicFile.class);

                    musicFile.setUri(mUri);
                    musicFile.setArtist(artist);
                    musicFile.setTitle(title);
                    musicFile.setImage(image);
                    musicFile.setDuration(duration);
                }

            }
        });


        if (mRealm.where(MusicFile.class).count() > 0) {
            MusicFile musicFile = mRealm.where(MusicFile.class).findFirst();
            Toast.makeText(this, "MusicFile에 들어간 파일 : " + musicFile.toString(), Toast.LENGTH_SHORT).show();
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

            // 오디오 앨범 자켓 이미지
//            byte albumImage[] = retriever.getEmbeddedPicture();
//            if (null != albumImage) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
//            }

            viewHolder.titleTextView.setText(title);
            viewHolder.artistTextView.setText(artist);

        }
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


