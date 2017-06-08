package com.hj2.user.musicplayerproject.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hj2.user.musicplayerproject.R;
import com.hj2.user.musicplayerproject.models.MusicFile;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by USER on 2017-05-01.
 */

public class PlaylistRecyclerviewAdapter extends RealmRecyclerViewAdapter<MusicFile, PlaylistRecyclerviewAdapter.MyViewHolder> {


    private Context mContext;

    // 1 -> 플레이리스트 / 2 -> 편집
    private int mMode;

    // 선택아이템담을 배열선언
    private SparseBooleanArray mSelectedItem;


    // 온아이템클릭시 -> 콜백 위한 처리들
    // 1. 보내줄 정보 인터페이스로 정리
    public interface OnItemClickListener {
        void onItemClick(View view, Uri uri, int position, int id);
    }

    // 2. 변수로 갖기
    OnItemClickListener mListener;

    // 3. 변수 외부에서 세팅할수 있게 연결
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public PlaylistRecyclerviewAdapter(Context context, @Nullable OrderedRealmCollection<MusicFile> data, int mode) {
        super(data, true);
        setHasStableIds(true);

        mContext = context;
        mMode = mode;

        // 배열 초기화
        this.mSelectedItem = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mSeletedPositionArraylist = new ArrayList<Integer>();

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.realm_recyclerview, parent, false);
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final MusicFile obj = getItem(position);
        //noinspection ConstantConditions


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    Uri uri = Uri.parse(obj.getUri());
                    int id = obj.getId();
                    mListener.onItemClick(v, uri, position, id);
                }

                if (mMode == 2) {
                    if (v.getTag() == null || (boolean) v.getTag() == false) {
                        v.setTag(true);
//                        v.setBackgroundColor(Color.RED);

                    } else {
                        v.setTag(false);
//                        v.setBackgroundColor(Color.WHITE);
                    }
                }
            }
        });


        // TODO 수정
//        // 선택됐을 때
//        if (mSeletedPositionArraylist.contains(position)) {
//            holder.itemView.setBackgroundColor(Color.RED);
//        } else {
//            holder.itemView.setBackgroundColor(Color.WHITE);
//        }

        // 선택됐을 때
        holder.itemView.setActivated(mSelectedItem.get(position, false));


        holder.titleTextview.setText(obj.getTitle());
        holder.artistTextview.setText(obj.getArtist());


        // 이미지 뿌리기
        if (obj.getImage2() != null) {
            Glide.with(mContext).load(obj.getImage2()).into(holder.albumImageview);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.albumImageview);
        }
    }


    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextview;
        TextView artistTextview;
        ImageView albumImageview;

        MyViewHolder(View view) {
            super(view);
            titleTextview = (TextView) view.findViewById(R.id.realm_recyclerview_title_textview);
            artistTextview = (TextView) view.findViewById(R.id.realm_recyclerview_artist_textview);
            albumImageview = (ImageView) view.findViewById(R.id.realm_recyclerview_album_imageview);

        }
    }


    // 선택됐을때
    private ArrayList<Integer> mSeletedPositionArraylist;

    public void setSelect(int position) {

        if (mSeletedPositionArraylist.contains(position)) {
            mSeletedPositionArraylist.remove((Integer) position);
        } else {
            mSeletedPositionArraylist.add(position);
        }

    }


    // 선택상태 토글
    public void toggleSelection(String id, View view, int position) {

        if (mSelectedItem.get(position, false)) {
            // 이미 선택되어 있을 때 -> 선택 해제

            mSelectedItem.delete(position);

//            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
        } else {
            // 선택되어 있지 않을 때 -> 선택

            mSelectedItem.put(position, true);
            //  view.setBackgroundColor(Color.YELLOW);
//            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();

        }
        notifyItemChanged(position);

    }


    // 모든 항목의 선택상태 지우기
    public void clearSelection() {
        mSelectedItem.clear();
        notifyDataSetChanged();
    }

    // 선택된 항목의 개수
    public int getSelectedItemCount() {
        return mSelectedItem.size();
    }

    // 선택된 아이템 리스트 반환 메서드
    public List<Integer> getSelectedItem() {
        List<Integer> item = new ArrayList<>(mSelectedItem.size());
        for (int i = 0; i < mSelectedItem.size(); i++) {
            item.add(mSelectedItem.keyAt(i));
        }
        return item;
    }




}
