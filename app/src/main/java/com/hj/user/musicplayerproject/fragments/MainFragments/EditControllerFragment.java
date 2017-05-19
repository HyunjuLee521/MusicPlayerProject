package com.hj.user.musicplayerproject.fragments.MainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hj.user.musicplayerproject.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by USER on 2017-05-01.
 */

public class EditControllerFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mUpLinearlayout;
    private LinearLayout mDownLinearlayout;
    private LinearLayout mCancelLinearlayout;
    private LinearLayout mDeleteLinearlayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_controller_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCancelLinearlayout = (LinearLayout) view.findViewById(R.id.cancel_linearlayout);
        mCancelLinearlayout.setOnClickListener(this);

        mDeleteLinearlayout = (LinearLayout) view.findViewById(R.id.delete_linearlayout);
        mDeleteLinearlayout.setOnClickListener(this);

        mUpLinearlayout = (LinearLayout) view.findViewById(R.id.up_linearlayout);
        mUpLinearlayout.setOnClickListener(this);

        mDownLinearlayout = (LinearLayout) view.findViewById(R.id.down_linearlayout);
        mDownLinearlayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.delete_linearlayout:
                /**
                 * {@link com.hj.user.musicplayerproject.fragments.MainFragments.PlaylistFragment#editPlaylist(Integer)}
                 */
                EventBus.getDefault().post(4);
                break;


            case R.id.cancel_linearlayout:
                /**
                 * {@link com.hj.user.musicplayerproject.fragments.MainFragments.PlaylistFragment#editPlaylist(Integer)}
                 */
                EventBus.getDefault().post(5);
                break;






            default:
                break;


        }


    }
}
