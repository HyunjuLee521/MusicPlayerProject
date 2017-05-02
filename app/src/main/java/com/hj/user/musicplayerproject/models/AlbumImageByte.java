package com.hj.user.musicplayerproject.models;

import java.util.Arrays;

/**
 * Created by USER on 2017-04-26.
 */

public class AlbumImageByte {
    byte[] albumImage;


    public AlbumImageByte(byte[] albumImage) {
        this.albumImage = albumImage;
    }

    public byte[] getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(byte[] albumImage) {
        this.albumImage = albumImage;
    }

    @Override
    public String toString() {
        return "AlbumImageByte{" +
                "albumImage=" + Arrays.toString(albumImage) +
                '}';
    }
}
