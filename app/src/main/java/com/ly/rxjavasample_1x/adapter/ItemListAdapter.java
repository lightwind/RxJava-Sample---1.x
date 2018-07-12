package com.ly.rxjavasample_1x.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ly.rxjavasample_1x.R;
import com.ly.rxjavasample_1x.model.Item;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Create by LiuYang on 2018/7/11 15:49
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.DebounceViewHolder> {

    private List<Item> mImages;

    public void setItems(List<Item> images) {
        mImages = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DebounceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new DebounceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DebounceViewHolder holder, int position) {
        Item image = mImages.get(position);
        Glide.with(holder.itemView.getContext()).load(image.imageUrl).into(holder.mImageIv);
        holder.mDescriptionTv.setText(image.description);
    }

    @Override
    public int getItemCount() {
        return mImages == null ? 0 : mImages.size();
    }

    class DebounceViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageIv)
        ImageView mImageIv;
        @Bind(R.id.descriptionTv)
        TextView mDescriptionTv;

        DebounceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
