package com.hj.user.musicplayerproject.models;

import java.util.Arrays;

import io.realm.RealmObject;

/**
 * Created by USER on 2017-05-19.
 */

public class FavoriteMusicFile extends RealmObject {


    int id;
    String uri;
    String title;
    String artist;
    String duration;

    byte[] image2;
    int position;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public byte[] getImage2() {
        return image2;
    }

    public void setImage2(byte[] image2) {
        this.image2 = image2;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public String toString() {
        return "FavoriteMusicFile{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration='" + duration + '\'' +
                ", image2=" + Arrays.toString(image2) +
                ", position=" + position +
                '}';
    }
}
