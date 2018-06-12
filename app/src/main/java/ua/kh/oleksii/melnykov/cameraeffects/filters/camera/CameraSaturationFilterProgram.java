package ua.kh.oleksii.melnykov.cameraeffects.filters.camera;

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
public class CameraSaturationFilterProgram extends FilterBaseProgram {

    private int mSaturationLocation;
    private float mSaturation;

    public CameraSaturationFilterProgram() {
        mSaturation = 1.7f;
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
                "const vec3 mLuminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                "" +
                "uniform float mSaturation;\n" +
                "" +
                "void main() {\n" +
                "    vec4 textureColor = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                "    float luminance = dot(textureColor.rgb, mLuminanceWeighting);\n" +
                "    vec3 greyScaleColor = vec3(luminance);\n" +
                "" +
                "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, mSaturation), textureColor.w);\n" +
                " }";
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

        mSaturationLocation = GLES30.glGetUniformLocation(mProgramHandle, "mSaturation");
        GlUtil.checkLocation(mSaturationLocation, "mSaturation");
    }

    @Override
    public void optionalDraw(int textureId) {
        GLES30.glUniform1f(mSaturationLocation, mSaturation);
        GlUtil.checkGlError("glUniform1f");
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
        return calculatePercentByValue(0f, 2f, mSaturation);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mSaturation = calculateValueByPercent(0f, 2f, newValue);
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
