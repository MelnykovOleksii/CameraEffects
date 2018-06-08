package ua.kh.oleksii.melnykov.cameraeffects.filters.camera;

import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculatePercentByValue;
import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculateValueByPercent;

/**
 * <p> Created by Melnykov Oleksii on 18.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraPixelationFilterProgram extends FilterBaseProgram {

    private int mWidthFactorLocation;
    private float mWidthFactor;

    private int mHeightFactorLocation;
    private float mHeightFactor;

    private int mPixelLocation;
    private float mPixel;

    public CameraPixelationFilterProgram() {
        mWidthFactor = 1f;
        mHeightFactor = 1f;
        mPixel = 20f;
    }

    @Override
    protected String getVertexShader() {
        return "uniform mat4 mTextureMatrix;\n" +
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
    protected String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "" +
                "uniform float mWidthFactor;\n" +
                "uniform float mHeightFactor;\n" +
                "uniform float mPixel;\n" +
                "" +
                "void main() {\n" +
                "  vec2 uv  = mOutputTextureCoordinate.xy;\n" +
                "  float dx = mPixel * mWidthFactor;\n" +
                "  float dy = mPixel * mHeightFactor;\n" +
                "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
                "  vec3 tc = texture2D(sTexture, coord).xyz;\n" +
                "  gl_FragColor = vec4(tc, 1.0);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "mTextureMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "mTextureMatrix");

        muKernelLoc = GLES20.glGetUniformLocation(mProgramHandle, "uKernel");
        if (muKernelLoc < 0) {
            muKernelLoc = -1;
            muTexOffsetLoc = -1;
            muColorAdjustLoc = -1;
        } else {
            muTexOffsetLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexOffset");
            GlUtil.checkLocation(muTexOffsetLoc, "uTexOffset");
            muColorAdjustLoc = GLES20.glGetUniformLocation(mProgramHandle, "uColorAdjust");
            GlUtil.checkLocation(muColorAdjustLoc, "uColorAdjust");

            setKernel(new float[]{0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f});
            setTexSize(256, 256);
        }

        mWidthFactorLocation = GLES30.glGetUniformLocation(mProgramHandle, "mWidthFactor");
        GlUtil.checkLocation(mWidthFactorLocation, "mWidthFactor");

        mHeightFactorLocation = GLES30.glGetUniformLocation(mProgramHandle, "mHeightFactor");
        GlUtil.checkLocation(mHeightFactorLocation, "mHeightFactor");

        mPixelLocation = GLES30.glGetUniformLocation(mProgramHandle, "mPixel");
        GlUtil.checkLocation(mPixelLocation, "mPixel");
    }

    @Override
    public void optionalDraw(int textureId) {
        GLES20.glUniform1f(mWidthFactorLocation, mWidthFactor);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mHeightFactorLocation, mHeightFactor);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mPixelLocation, mPixel);
        GlUtil.checkGlError("glUniform1f");
    }

    @Override
    public void setTexSize(int width, int height) {
        super.setTexSize(width, height);
        mWidthFactor = 1f / width;
        mHeightFactor = 1f / height;
    }

    @Override
    public boolean isNeedFirstSettingParameter() {
        return true;
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
    public int getFirstSettingsValue() {
        return calculatePercentByValue(1f, 100f, mPixel);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mPixel = calculateValueByPercent(1f, 100f, newValue);
    }

    @Override
    public int getSecondSettingsValue() {
        return 0;
    }

    @Override
    public void setSecondSettingsValue(int newValue) {

    }

    @Override
    public int getThirdSettingsValue() {
        return 0;
    }

    @Override
    public void setThirdSettingsValue(int newValue) {

    }

    @Override
    public boolean isTouchListenerEnable() {
        return false;
    }

    @Override
    public void setTouchCoordinate(float x, float y, int screenWidth, int screenHeight, CameraType cameraType) {

    }

}
