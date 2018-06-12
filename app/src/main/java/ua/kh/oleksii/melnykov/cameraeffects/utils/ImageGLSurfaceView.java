package ua.kh.oleksii.melnykov.cameraeffects.utils;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * <p> Created by Melnykov Oleksii on 09.06.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: cameraeffects, ua.kh.oleksii.melnykov.cameraeffects.utils </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class ImageGLSurfaceView extends GLSurfaceView {

    private Integer mWidth;
    private Integer mHeight;

    public ImageGLSurfaceView(Context context) {
        super(context);
    }

    public ImageGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setForceSize(Integer width, Integer height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidth != null && mHeight != null) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}