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

import com.bumptech.glide.Glide;
import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.models.MusicFile;

import java.io.ByteArrayOutputStream;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by USER on 2017-03-25.
 */

public class PlaylistListViewAdapter extends RealmBaseAdapter<MusicFile> implements ListAdapter {


    private static class ViewHolder {
        TextView titleTextview;
        TextView artistTextview;
        ImageView albumImageview;
    }


    public PlaylistListViewAdapter(@Nullable OrderedRealmCollection<MusicFile> data) {
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
            viewHolder.artistTextview = (TextView) convertView.findViewById(R.id.artist_name_textview);
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
//                viewHolder.albumImageview.setImageResource(R.drawable.ic_fast_forward_black_24dp);

                Glide.with(parent.getContext()).load(R.drawable.ic_fast_forward_black_24dp).into(viewHolder.albumImageview);
            } else {

                // TODO 이미지 보였다 안보였다 함
//                viewHolder.albumImageview.setImageBitmap(StringToBitMap(item.getImage()));
                Glide.with(parent.getContext()).load(bitmapToByteArray(StringToBitMap(item.getImage()))).into(viewHolder.albumImageview);

            }

        }

        return convertView;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
