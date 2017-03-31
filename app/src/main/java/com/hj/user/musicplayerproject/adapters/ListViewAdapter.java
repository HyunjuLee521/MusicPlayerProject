package com.hj.user.musicplayerproject.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hj.user.musicplayerproject.MusicFile;
import com.hj.user.musicplayerproject.R;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by USER on 2017-03-25.
 */

public class ListViewAdapter extends RealmBaseAdapter<MusicFile> implements ListAdapter{


    private static class ViewHolder {
        TextView titleTextview;
        TextView artistTextview;
        ImageView albumImageview;
    }


    public ListViewAdapter(@Nullable OrderedRealmCollection<MusicFile> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.songlist_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.titleTextview = (TextView) convertView.findViewById(R.id.title_textview);
            viewHolder.artistTextview = (TextView) convertView.findViewById(R.id.artist_textview);
            viewHolder.albumImageview = (ImageView) convertView.findViewById(R.id.album_imageview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (adapterData != null) {
            final MusicFile item = adapterData.get(position);
            viewHolder.titleTextview.setText(item.getTitle());
            viewHolder.artistTextview.setText(item.getArtist());
            if (item.getImage().equals("nothing")) {
                viewHolder.albumImageview.setImageResource(R.drawable.ic_fast_forward_black_24dp);
            } else {
                viewHolder.albumImageview.setImageBitmap(StringToBitMap(item.getImage()));
            }


        }

        return convertView;
    }



    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
