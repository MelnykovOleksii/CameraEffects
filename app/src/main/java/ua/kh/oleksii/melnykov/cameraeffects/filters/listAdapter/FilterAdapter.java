package ua.kh.oleksii.melnykov.cameraeffects.filters.listAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.Filters;

/**
 * <p> Created by Melnykov Oleksii on 19.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.camera </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterHolder> {

    private final int viewHeight;
    private final int viewWidth;

    private OnItemClickCallback mClickCallback;

    private int mLastPosition;

    public FilterAdapter(int height, OnItemClickCallback onItemClickCallback) {
        viewHeight = height;
        viewWidth = height / 4 * 3;

        mClickCallback = onItemClickCallback;
        mLastPosition = -1;
    }

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.height = viewHeight;
        layoutParams.width = viewWidth;
        itemView.setLayoutParams(layoutParams);
        return new FilterHolder(itemView, (position, isSecondClick) -> {
            mClickCallback.onItemClick(position, position != 0 && mLastPosition == position);
            mLastPosition = position;
        });
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position) {
        holder.bind(Filters.TYPE.values()[position]);
    }

    @Override
    public int getItemCount() {
        return Filters.TYPE.values().length;
    }

    public interface OnItemClickCallback {
        void onItemClick(int position, Boolean isSecondClick);
    }

}
