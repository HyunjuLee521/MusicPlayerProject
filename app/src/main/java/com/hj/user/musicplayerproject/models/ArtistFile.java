package com.hj.user.musicplayerproject.models;

import io.realm.RealmObject;

/**
 * Created by USER on 2017-04-25.
 */

public class ArtistFile extends RealmObject {
    int id;
    String artistName;
    int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        return "ArtistFile{" +
                "id=" + id +
                ", artistName='" + artistName + '\'' +
                ", count=" + count +
                '}';
    }
}
