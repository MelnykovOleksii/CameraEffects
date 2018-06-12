package ua.kh.oleksii.melnykov.cameraeffects.filters.camera;

import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

/**
 * <p> Created by Melnykov Oleksii on 5/22/2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraNoFilterProgram extends FilterBaseProgram {

    public CameraNoFilterProgram() {

    }

    @Override
    protected String getVertexShader() {
        return "" +
                "uniform mat4 mTextureMatrix;\n" +

                "attribute vec4 mPosition;\n" +
                "attribute vec4 mInputTextureCoordinate;\n" +
                "varying vec2 mOutputTextureCoordinate;" +
                "" +
                "void main() {\n" +
                "    gl_Position = mPosition;\n" +
                "    mOutputTextureCoordinate = (mTextureMatrix * mInputTextureCoordinate).xy;\n" +
                "}";
    }

    @Override
    public String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        muTexMatrixLoc = GLES30.glGetUniformLocation(mProgramHandle, "mTextureMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "mTextureMatrix");

        muKernelLoc = GLES30.glGetUniformLocation(mProgramHandle, "uKernel");
        if (muKernelLoc < 0) {
            muKernelLoc = -1;
            muTexOffsetLoc = -1;
            muColorAdjustLoc = -1;
        } else {
            muTexOffsetLoc = GLES30.glGetUniformLocation(mProgramHandle, "uTexOffset");
            GlUtil.checkLocation(muTexOffsetLoc, "uTexOffset");

            muColorAdjustLoc = GLES30.glGetUniformLocation(mProgramHandle, "uColorAdjust");
            GlUtil.checkLocation(muColorAdjustLoc, "uColorAdjust");

            setKernel(new float[]{0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f});
            setTexSize(256, 256);
        }
    }


    @Override
    public void optionalDraw(int textureId) {
        // nothing to do
    }

    @Override
    public boolean isNeedFirstSettingParameter() {
        return false;
    }

    @Override
    public boolean isNeedSecondSettingParameters() {
        return false;
    }

    @Override
    public boolean isNeedThirdSettingParameters() {
        return false;
    }

    @Override
    public int getSecondSettingsValue() {
        return 0;
    }

    @Override
    public int getThirdSettingsValue() {
        return 0;
    }

    @Override
    public void setThirdSettingsValue(int newValue) {

    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        // nothing to do
    }

    @Override
    public boolean isTouchListenerEnable() {
        return false;
    }

    @Override
    public int getFirstSettingsValue() {
        return 0;
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        // nothing to do
    }

    @Override
    public void setTouchCoordinate(float x, float y, int screenWidth, int screenHeight, CameraType cameraType) {

    }

}