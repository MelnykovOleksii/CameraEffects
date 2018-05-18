package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ua.kh.oleksii.melnykov.cameraeffects.filters.DistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FullFrameRect;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.camera </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraRender implements GLSurfaceView.Renderer {

    private int mIncomingWidth;
    private int mIncomingHeight;
    private boolean mIncomingSizeUpdated;
    private GL10 mGL10;
    private FullFrameRect mFullScreen;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private Handler mCameraHandler;
    private float[] mSTMatrix = new float[16];

    public CameraRender(CameraHandler cameraHandler) {
        mCameraHandler = cameraHandler;
        mTextureId = -1;

        mIncomingSizeUpdated = false;
        mIncomingWidth = mIncomingHeight = -1;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGL10 = gl;

        mFullScreen = new FullFrameRect(
                new DistortionFilterProgram());

        mTextureId = mFullScreen.createTextureObject();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                CameraHandler.MSG_SET_SURFACE_TEXTURE, mSurfaceTexture));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mIncomingHeight == -1 || mIncomingWidth == -1) {
            mFullScreen.setRatio(1f);
        } else {
            mFullScreen.setRatio(((width / (float) mIncomingHeight * 2f)
                    / (height / (float) mIncomingWidth))
                    / 2f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (mIncomingWidth <= 0 || mIncomingHeight <= 0) return;

        if (mIncomingSizeUpdated) {
            mFullScreen.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
            mIncomingSizeUpdated = false;
        }

        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        mFullScreen.drawFrame(mTextureId, mSTMatrix);
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
        if (mFullScreen != null) {
            mFullScreen.release();
            mFullScreen = null;
        }
    }
}
