package com.virgil.choosephotos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by virgil on 2016/11/28 13:43.
 */

public class ShowImageAdapter extends RecyclerView.Adapter {

    private Context mContext;//上下文
    private ArrayList<String> imgList = new ArrayList<>();//数据源
    private LayoutInflater mInflater;
    private OnItemClickedListener mListener = null;
    private Intent mIntent = new Intent();
    RecyclerView recyclerView;

    public ShowImageAdapter(Context context, ArrayList<String> imgList, RecyclerView _recyclerView) {
        mContext = context;
        this.imgList = imgList;
        recyclerView = _recyclerView;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface OnItemClickedListener {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }

    public void setOnItemClickedListener(OnItemClickedListener _listener) {
        mListener = _listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            if (position < imgList.size()) {//加载其他图片
                Glide.with(mContext)
                        .load(imgList.get(position))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(((MyViewHolder) holder).image);
            } else {//加载最后一张固定的加号图片
                if (imgList.size() == 9) {
                    //当选择的图片数量已经达到上限9张是，隐藏加号图片
                    ((MyViewHolder) holder).image.setVisibility(View.GONE);
                } else {
                    ((MyViewHolder) holder).image.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(R.drawable.icon_addpic_unfocused)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(((MyViewHolder) holder).image);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return imgList.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            //初始化标签
            image = (ImageView) itemView.findViewById(R.id.item_rv_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = recyclerView.getChildPosition(v);
                        mListener.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position=recyclerView.getChildPosition(v);
                    return mListener.onItemLongClick(position);
                }
            });
        }
    }
}
