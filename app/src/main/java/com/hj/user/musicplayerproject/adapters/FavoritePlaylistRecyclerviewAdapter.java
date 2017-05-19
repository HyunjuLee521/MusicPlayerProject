package com.hj.user.musicplayerproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.models.FavoriteMusicFile;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by USER on 2017-05-19.
 */

public class FavoritePlaylistRecyclerviewAdapter extends RealmRecyclerViewAdapter<FavoriteMusicFile, FavoritePlaylistRecyclerviewAdapter.MyViewHolder> {
    private Context mContext;


    // 온아이템클릭시 -> 콜백 위한 처리들
    // 1. 보내줄 정보 인터페이스로 정리
    public interface OnItemClickListener {
        void onItemClick(View view, Uri uri, int position, int id);
    }

    // 2. 변수로 갖기
    OnItemClickListener mListener;

    // 3. 변수 외부에서 세팅할수 있게 연결
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public FavoritePlaylistRecyclerviewAdapter(Context context, @Nullable OrderedRealmCollection<FavoriteMusicFile> data) {
        super(data, true);
        setHasStableIds(true);

        mContext = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.realm_recyclerview, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final FavoriteMusicFile obj = getItem(position);

        // 아이템 클릭시
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    Uri uri = Uri.parse(obj.getUri());
                    int id = obj.getId();
                    mListener.onItemClick(v, uri, position, id);
                }
            }
        });


        // 뿌리기
        holder.titleTextview.setText(obj.getTitle());
        holder.artistTextview.setText(obj.getArtist());

        if (obj.getImage2() != null) {
            Glide.with(mContext).load(obj.getImage2()).into(holder.albumImageview);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.albumImageview);
        }

    }


    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextview;
        TextView artistTextview;
        ImageView albumImageview;

        MyViewHolder(View view) {
            super(view);
            titleTextview = (TextView) view.findViewById(R.id.realm_recyclerview_title_textview);
            artistTextview = (TextView) view.findViewById(R.id.realm_recyclerview_artist_textview);
            albumImageview = (ImageView) view.findViewById(R.id.realm_recyclerview_album_imageview);

        }
    }
}
