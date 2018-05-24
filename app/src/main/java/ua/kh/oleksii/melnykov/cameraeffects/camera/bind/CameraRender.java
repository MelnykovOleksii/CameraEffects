package ua.kh.oleksii.melnykov.cameraeffects.camera.bind;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.annotation.Nullable;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.Filters;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.camera </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraRender implements GLSurfaceView.Renderer {

    private static final int SIZEOF_FLOAT = 4;
    private int mIncomingWidth;
    private int mIncomingHeight;
    private boolean mIncomingSizeUpdated;
    private GL10 mGL10;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    @Nullable
    private Handler mCameraHandler;
    private float[] mSTMatrix = new float[16];
    private FloatBuffer mVertexArray;
    private int mVertexCount;
    private int mCoordsPerVertex;
    private int mTexCoordStride;
    private float mRatio;
    private FilterProgram mProgram;
    private Filters.TYPE mTYPE;
    private Filters.TYPE mNewType;

    private FloatBuffer mTexCoordArray = GlUtil.createFloatBuffer(new float[]{
            0.0f, 0.0f,     // 0 bottom left
            1.0f, 0.0f,     // 1 bottom right
            0.0f, 1.0f,     // 2 top left
            1.0f, 1.0f      // 3 top right
    });

    public CameraRender(@Nullable CameraHandler cameraHandler) {
        mCameraHandler = cameraHandler;
        mTextureId = -1;

        mIncomingSizeUpdated = false;
        mIncomingWidth = mIncomingHeight = -1;
        mTYPE = Filters.TYPE.NO_FILTER;
        mNewType = Filters.TYPE.NO_FILTER;
    }

    public FilterProgram getProgram() {
        return mProgram;
    }

    public void setCameraHandler(@Nullable Handler cameraHandler) {
        mCameraHandler = cameraHandler;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (mCameraHandler == null) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        } else {
            mProgram = Filters.switchProgramByType(mTYPE);
            mGL10 = gl;
            mRatio = 1f;
            mCoordsPerVertex = 2;
            mVertexCount = 4;
            initVertexArray();
            mTexCoordStride = 2 * SIZEOF_FLOAT;
            mTextureId = mProgram.createTextureObject();
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                    CameraHandler.MSG_SET_SURFACE_TEXTURE, mSurfaceTexture));
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mCameraHandler == null) {
            gl.glViewport(0, 0, width, height);
        } else {

            if (mIncomingHeight == -1 || mIncomingWidth == -1) {
                mRatio = 1f;
                initVertexArray();
            } else {
                mRatio = ((width / (float) mIncomingHeight * 2f)
                        / (height / (float) mIncomingWidth))
                        / 2f;
                initVertexArray();
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mCameraHandler == null) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        } else {
            mSurfaceTexture.updateTexImage();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            if (mIncomingWidth <= 0 || mIncomingHeight <= 0) return;
            if (mTYPE != mNewType) changeProgram();
            if (mIncomingSizeUpdated) {
                mProgram.setTexSize(mIncomingWidth, mIncomingHeight);
                mIncomingSizeUpdated = false;
            }
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
            mProgram.draw(
                    mVertexArray,
                    mVertexCount,
                    mCoordsPerVertex,
                    mTexCoordStride,
                    mSTMatrix,
                    mTexCoordArray,
                    mTextureId,
                    mTexCoordStride);
        }
    }

    public void setCameraPreviewSize(int previewWidth, int previewHeight,
                                     int SurfaceViewWidth, int SurfaceViewHeight) {
        mIncomingWidth = previewWidth;
        mIncomingHeight = previewHeight;
        if (mGL10 != null)
            onSurfaceChanged(mGL10, SurfaceViewWidth, SurfaceViewHeight);
        mIncomingSizeUpdated = true;
    }

    public void notifyPausing() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mProgram != null) {
            mProgram.release();
            mProgram = null;
        }
    }

    private void initVertexArray() {
        mVertexArray = GlUtil.createFloatBuffer(new float[]{
                -1.0f, -mRatio,   // 0 bottom left
                1.0f, -mRatio,   // 1 bottom right
                -1.0f, mRatio,   // 2 top left
                1.0f, mRatio,   // 3 top right
        });
    }

    public void changeFilter(int position) {
        mNewType = Filters.TYPE.values()[position];
    }

    private void changeProgram() {
        mProgram.release();
        mProgram = Filters.switchProgramByType(mNewType);

        mIncomingSizeUpdated = true;
        mTYPE = mNewType;
    }

}