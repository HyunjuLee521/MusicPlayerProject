package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hj.user.musicplayerproject.R;

/**
 * Created by USER on 2017-05-01.
 */

public class EditControllerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_controller_fragment, container, false);
    }
}
