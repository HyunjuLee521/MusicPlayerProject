package com.hj2.user.musicplayerproject.fragments.SelectSongFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hj2.user.musicplayerproject.R;

/**
 * Created by USER on 2017-03-26.
 */

public class SendSongToPlaylistFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_song_to_playlist_fragment, container, false);
    }

}
