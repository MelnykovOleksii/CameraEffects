package ua.kh.oleksii.melnykov.cameraeffects.filters.listAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.Filters;

/**
 * <p> Created by Melnykov Oleksii on 19.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.camera.bind </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class FilterHolder extends RecyclerView.ViewHolder {

    private ImageView mPreviewImage;
    private TextView mFilterName;

    FilterHolder(View itemView, FilterAdapter.OnItemClickCallback clickCallback) {
        super(itemView);

        mPreviewImage = itemView.findViewById(R.id.item_filter_image);
        mFilterName = itemView.findViewById(R.id.item_filter_name);

        View viewClick = itemView.findViewById(R.id.item_filter_click);
        viewClick.setOnClickListener(v -> clickCallback.onItemClick(
                getAdapterPosition(), null));
    }

    public void bind(Filters.TYPE type) {
        mPreviewImage.setImageResource(R.drawable.filter_image);
        mFilterName.setText(type.getStringId());
    }
}
