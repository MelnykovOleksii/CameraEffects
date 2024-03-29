package ua.kh.oleksii.melnykov.cameraeffects.gallery.bind;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView.Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.Filters;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

/**
 * <p> Created by Melnykov Oleksii on 24-May-18. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.gallery.bind </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class ImageRenderer implements Renderer {

    private static final int NO_IMAGE = -1;

    private static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };
    public final Object mSurfaceChangedWaiter = new Object();
    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private Filters.TYPE mTYPE;
    private Filters.TYPE mNewType;
    private FilterBaseProgram mFilter;
    private int mGLTextureId = NO_IMAGE;
    private SurfaceTexture mSurfaceTexture = null;
    private int mOutputWidth;
    private int mOutputHeight;
    private int mImageWidth;
    private int mImageHeight;

    private int mTextureId;
    private Bitmap mBitmap;
    private CallbackTakeBitmap mCallbackTakeBitmap;

    public ImageRenderer() {
        mTYPE = Filters.TYPE.NO_FILTER;
        mNewType = Filters.TYPE.NO_FILTER;

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        if (mBitmap == null) {
            GLES30.glClearColor(0f, 0f, 0f, 1f);
            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        } else {
            mFilter = Filters.switchProgramByTypeForGallery(mTYPE);
            mTextureId = mFilter.getProgramHandle();
        }
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        GLES30.glViewport(0, 0, width, height);
        GLES30.glUseProgram(mTextureId);
        adjustImageScaling();
        synchronized (mSurfaceChangedWaiter) {
            mSurfaceChangedWaiter.notifyAll();
        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        if (mBitmap == null) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        } else {

            if (mFilter == null) {
                mFilter = Filters.switchProgramByTypeForGallery(mTYPE);
                mTextureId = mFilter.getProgramHandle();
            }

            loadBitmap();
            if (mTYPE != mNewType) changeProgram();
            mFilter.setTexSize(mOutputWidth, mOutputHeight);
            mFilter.draw(mGLCubeBuffer, mGLTextureBuffer, mGLTextureId,
                    null, null, null, null, null);

            if (mCallbackTakeBitmap != null) {
                mCallbackTakeBitmap.takeBitmap(takeBitmap(gl));
                mCallbackTakeBitmap = null;
            }
        }

        if (mSurfaceTexture != null) mSurfaceTexture.updateTexImage();
    }

    private Bitmap takeBitmap(GL10 mGL) {
        final int width = mOutputWidth;
        final int height = mOutputHeight;

        IntBuffer ib = IntBuffer.allocate(width * height);
        IntBuffer ibt = IntBuffer.allocate(width * height);
        mGL.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ibt.put((height - i - 1) * width + j, ib.get(i * width + j));
            }
        }

        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.argb(0, 255, 255, 255));
        mBitmap.copyPixelsFromBuffer(ibt);
        return mBitmap;
    }

    private void loadBitmap() {
        mGLTextureId = GlUtil.loadTexture(mBitmap, mGLTextureId);
        mImageWidth = mBitmap.getWidth();
        mImageHeight = mBitmap.getHeight();

        adjustImageScaling();
    }

    private void changeProgram() {
        mFilter.release();
        mFilter = Filters.switchProgramByTypeForGallery(mNewType);
        mFilter.getProgramHandle();
        mTYPE = mNewType;
    }

    public void setImageBitmap(final Bitmap bitmap) {
        mBitmap = bitmap;

        if (mBitmap == null) {
            return;
        }
        Bitmap resizedBitmap = null;
        if (mBitmap.getWidth() % 2 == 1) {
            resizedBitmap = Bitmap.createBitmap(mBitmap.getWidth() + 1, mBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas can = new Canvas(resizedBitmap);
            can.drawARGB(0x00, 0x00, 0x00, 0x00);
            can.drawBitmap(mBitmap, 0, 0, null);
        }
        if (resizedBitmap != null) mBitmap = resizedBitmap;
    }

    protected int getFrameWidth() {
        return mOutputWidth;
    }

    protected int getFrameHeight() {
        return mOutputHeight;
    }

    private void adjustImageScaling() {
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;

        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        float[] cube = new float[]{
                CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
        };

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);
    }

    public void changeFilter(int position) {
        mNewType = Filters.TYPE.values()[position];
    }

    public FilterBaseProgram getProgram() {
        return mFilter;
    }

    public void setCallbackTakeBitmap(CallbackTakeBitmap callbackTakeBitmap) {
        mCallbackTakeBitmap = callbackTakeBitmap;
    }

    public interface CallbackTakeBitmap {
        void takeBitmap(Bitmap image);
    }
}
