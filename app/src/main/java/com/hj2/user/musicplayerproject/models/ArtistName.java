package com.hj2.user.musicplayerproject.models;

/**
 * Created by USER on 2017-04-25.
 */

public class ArtistName {
    String name;
    int cnt;


    public ArtistName(String name, int cnt) {
        this.name = name;
        this.cnt = cnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "ArtistName{" +
                "name='" + name + '\'' +
                ", cnt=" + cnt +
                '}';
    }
}
