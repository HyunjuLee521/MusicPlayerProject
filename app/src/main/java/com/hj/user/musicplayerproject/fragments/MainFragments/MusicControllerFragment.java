package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hj.user.musicplayerproject.R;
import com.hj.user.musicplayerproject.services.MusicService;

/**
 * Created by USER on 2017-03-22.
 */

public class MusicControllerFragment extends Fragment {

    private boolean mBound = false;
    private MusicService mService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_controller_fragment, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }

    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

//            // UI 갱신
//            updateUI(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
