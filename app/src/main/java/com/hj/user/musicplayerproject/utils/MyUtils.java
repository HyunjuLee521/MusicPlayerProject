package com.hj.user.musicplayerproject.utils;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by USER on 2017-05-16.
 */

public class MyUtils {


    public static class sendSongUriEvent {
        public sendSongUriEvent(ArrayList<Uri> uriArrayList) {
            this.uriArrayList = uriArrayList;
        }

        public ArrayList<Uri> uriArrayList;
    }

    public static class editPlaylistEvent {
        public editPlaylistEvent(ArrayList<Integer> positionArrayList) {
            this.positionArrayList = positionArrayList;

        }
        public ArrayList<Integer> positionArrayList;
    }


}
