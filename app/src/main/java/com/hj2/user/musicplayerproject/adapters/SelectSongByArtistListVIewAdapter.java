package com.hj2.user.musicplayerproject.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hj2.user.musicplayerproject.models.ArtistName;

import java.util.ArrayList;

/**
 * Created by USER on 2017-04-25.
 */

public class SelectSongByArtistListVIewAdapter extends RecyclerView.Adapter<SelectSongByArtistListVIewAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<ArtistName> mData;




    // 온아이템클릭시 -> 콜백 위한 처리들
    // 1. 보내줄 정보 인터페이스로 정리
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // 2. 변수로 갖기
    OnItemClickListener mListener;

    // 3. 변수 외부에서 세팅할수 있게 연결
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public SelectSongByArtistListVIewAdapter(Context context, ArrayList<ArtistName> data) {
        mData = new ArrayList<ArtistName>();
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // 아이템 클릭 시
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(holder.itemView, position);
                }

            }
        });


        // 미디어 정보
        String artist = mData.get(position).getName();
        int count = mData.get(position).getCnt();


        holder.artistTextView.setText(artist);
        holder.countTextView.setText(" " + count);
        holder.countTextView.setTextSize(16);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView artistTextView;
        TextView countTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            countTextView = (TextView) itemView.findViewById(android.R.id.text2);
            artistTextView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }





}
