package com.example.user.musicplayerproject.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by USER on 2017-03-25.
 */

//TODO Realm 어댑터 구현
public class ListViewAdapter extends RealmBaseAdapter implements ListAdapter{


    public ListViewAdapter(@Nullable OrderedRealmCollection data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
