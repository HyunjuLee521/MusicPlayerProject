package com.hj.user.musicplayerproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.models.ArtistName;

import java.util.ArrayList;

/**
 * Created by USER on 2017-04-25.
 */

public class SelectSongByArtistListVIewAdapter extends BaseAdapter {
    private ArrayList<ArtistName> mData;
    private Context mContext;


    public SelectSongByArtistListVIewAdapter(Context context, ArrayList<ArtistName> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // convertView : 재사용 되는 뷰
        if (convertView == null) {
            viewHolder = new ViewHolder();

            // 뷰를 새로 만들 때
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.artist_list, parent, false);

            // 레이아웃 들고 오기
            TextView artistNameTextview = (TextView) convertView.findViewById(R.id.artist_name_textview);
            TextView artistCountTextview = (TextView) convertView.findViewById(R.id.artist_count_textview);

            // 뷰 홀더에 넣는다
            viewHolder.artistName = artistNameTextview;
            viewHolder.artistCount = artistCountTextview;


            convertView.setTag(viewHolder);
        } else {
            // 재사용 할 때
            viewHolder = (ViewHolder) convertView.getTag();
        }


        // 데이터
        ArtistName data = mData.get(position);

        // 화면에 뿌리기
        viewHolder.artistName.setText(data.getName());
        viewHolder.artistCount.setText(data.getCnt() + "곡");

        return convertView;
    }


    // findViewById로 가져온 View 들을 보관
    private static class ViewHolder {
        TextView artistName;
        TextView artistCount;

    }
}
