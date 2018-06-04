package ua.kh.oleksii.melnykov.cameraeffects.filters;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.nio.FloatBuffer;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

/**
 * <p> Created by Melnykov Oleksii on 18.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public abstract class FilterBaseProgram {

    private static final int KERNEL_SIZE = 9;
    protected int mProgramHandle;

    // нужно для корректной ориентации камеры
    protected int muTexMatrixLoc;
    protected int muKernelLoc;
    protected int muTexOffsetLoc;
    protected int muColorAdjustLoc;
    private int maPositionLoc;
    private int maTextureCoordLoc;
    private float[] mKernel = new float[KERNEL_SIZE];
    private float[] mTexOffset;
    private float mColorAdjust;

    protected FilterBaseProgram() {
    }

    protected abstract String getVertexShader();

    protected abstract String getShader();

    public int getProgramHandle() {
        setup();
        return mProgramHandle;
    }

    private void setup() {
        mProgramHandle = GlUtil.createProgram(getVertexShader(),
                getShader());

        if (mProgramHandle == 0)
            throw new RuntimeException("Unable to create program");

        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "mPosition");
        GlUtil.checkLocation(maPositionLoc, "mPosition");

        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "mInputTextureCoordinate");
        GlUtil.checkLocation(maTextureCoordLoc, "mInputTextureCoordinate");

        optionalSetup();
    }

    protected abstract void optionalSetup();

    public void draw(@NonNull FloatBuffer vertexBuffer, @NonNull FloatBuffer texBuffer, int textureId,
                     @Nullable Integer vertexCount, @Nullable Integer coordsPerVertex,
                     @Nullable Integer vertexStride, @Nullable float[] texMatrix,
                     @Nullable Integer texStride) {

        GlUtil.checkGlError("draw start");

        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        if (vertexCount == null || coordsPerVertex == null || vertexStride == null ||
                texMatrix == null || texStride == null) {
            onDrawFromGallery(vertexBuffer, texBuffer, textureId);
        } else {
            onDrawFromCamera(textureId, texMatrix, coordsPerVertex, vertexStride,
                    vertexBuffer, texStride, texBuffer, vertexCount);
        }

    }

    private void onDrawFromGallery(FloatBuffer vertexBuffer, FloatBuffer texBuffer, int textureId) {
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(maPositionLoc);

        texBuffer.position(0);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0,
                texBuffer);

        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);

        optionalDraw(textureId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void onDrawFromCamera(int textureId, float[] texMatrix, Integer coordsPerVertex,
                                  int vertexStride, FloatBuffer vertexBuffer, int texStride,
                                  FloatBuffer texBuffer, int vertexCount) {

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, texStride, texBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        if (muKernelLoc >= 0) {
            GLES20.glUniform1fv(muKernelLoc, KERNEL_SIZE, mKernel, 0);
            GLES20.glUniform2fv(muTexOffsetLoc, KERNEL_SIZE, mTexOffset, 0);
            GLES20.glUniform1f(muColorAdjustLoc, mColorAdjust);
        }

        optionalDraw(textureId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
        GlUtil.checkGlError("glDrawArrays");

        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

    protected void setKernel(float[] values) {
        if (values.length != KERNEL_SIZE) {
            throw new IllegalArgumentException("Kernel size is " + values.length +
                    " vs. " + KERNEL_SIZE);
        }
        System.arraycopy(values, 0, mKernel, 0, KERNEL_SIZE);
        mColorAdjust = 0f;
    }

    public void setTexSize(int width, int height) {
        float rw = 1.0f / width;
        float rh = 1.0f / height;

        mTexOffset = new float[]{
                -rw, -rh, 0f, -rh, rw, -rh,
                -rw, 0f, 0f, 0f, rw, 0f,
                -rw, rh, 0f, rh, rw, rh
        };
    }

    public abstract void optionalDraw(int textureId);

    public abstract boolean isNeedFirstSettingParameter();

    public abstract boolean isNeedSecondSettingParameters();

    public abstract boolean isNeedThirdSettingParameters();

    public abstract void setFirstSettingsValue(int newValue);

    public abstract void setSecondSettingsValue(int newValue);

    public abstract int getThirdSettingsValue();

    public abstract int getFirstSettingsValue();

    public abstract int getSecondSettingsValue();

    public abstract void setThirdSettingsValue(int newValue);

    public abstract boolean isTouchListenerEnable();

    public void release() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
    }

    public abstract void setTouchCoordinate(float x, float y, int screenWidth, int screenHeight,
                                            @Nullable CameraType cameraType);
}