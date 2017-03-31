package com.hj.user.musicplayerproject;

import io.realm.RealmObject;

/**
 * Created by USER on 2017-03-23.
 */

public class MusicFile extends RealmObject{

    int id;
    String uri;
    String title;
    String artist;
    String duration;
    String image;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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


    @Override
    public String toString() {
        return "MusicFile{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration='" + duration + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
