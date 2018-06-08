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
public class CameraToonFilterProgram extends FilterBaseProgram {

    private int mTexelWidthLocation;
    private float mTexelWidth;

    private int mTexelHeightLocation;
    private float mTexelHeight;

    private int mThresholdLocation;
    private float mThreshold;

    private int mQuantizationLevelsLocation;
    private float mQuantizationLevels;

    public CameraToonFilterProgram() {
        mThreshold = .2f;
        mQuantizationLevels = 10f;
    }

    @Override
    protected String getVertexShader() {
        return "uniform mat4 mTextureMatrix;\n" +
                "attribute vec4 mPosition;\n" +
                "attribute vec4 mInputTextureCoordinate;\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "" +
                "uniform float mTexelWidth; \n" +
                "uniform float mTexelHeight; \n" +
                "" +
                "varying vec2 mLeftTextureCoordinate;\n" +
                "varying vec2 mRightTextureCoordinate;\n" +
                "varying vec2 mTopTextureCoordinate;\n" +
                "varying vec2 mTopLeftTextureCoordinate;\n" +
                "varying vec2 mTopRightTextureCoordinate;\n" +
                "varying vec2 mBottomTextureCoordinate;\n" +
                "varying vec2 mBottomLeftTextureCoordinate;\n" +
                "varying vec2 mBottomRightTextureCoordinate;\n" +
                "" +
                "void main() {\n" +
                "    gl_Position = mPosition;\n" +
                "    vec2 widthStep = vec2(mTexelWidth, 0.0);\n" +
                "    vec2 heightStep = vec2(0.0, mTexelHeight);\n" +
                "    vec2 widthHeightStep = vec2(mTexelWidth, mTexelHeight);\n" +
                "    vec2 widthNegativeHeightStep = vec2(mTexelWidth, -mTexelHeight);\n" +
                "    mOutputTextureCoordinate = (mTextureMatrix * mInputTextureCoordinate).xy;\n" +
                "" +
                "    mLeftTextureCoordinate = mOutputTextureCoordinate - widthStep;\n" +
                "    mRightTextureCoordinate = mOutputTextureCoordinate + widthStep;\n" +
                "    mTopTextureCoordinate = mOutputTextureCoordinate - heightStep;\n" +
                "    mTopLeftTextureCoordinate = mOutputTextureCoordinate - widthHeightStep;\n" +
                "    mTopRightTextureCoordinate = mOutputTextureCoordinate + widthNegativeHeightStep;\n" +
                "    mBottomTextureCoordinate = mOutputTextureCoordinate + heightStep;\n" +
                "    mBottomLeftTextureCoordinate = mOutputTextureCoordinate - widthNegativeHeightStep;\n" +
                "    mBottomRightTextureCoordinate = mOutputTextureCoordinate + widthHeightStep;\n" +
                "}";
    }

    @Override
    protected String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "" +
                "varying vec2 mLeftTextureCoordinate;\n" +
                "varying vec2 mRightTextureCoordinate;\n" +
                "varying vec2 mTopTextureCoordinate;\n" +
                "varying vec2 mTopLeftTextureCoordinate;\n" +
                "varying vec2 mTopRightTextureCoordinate;\n" +
                "varying vec2 mBottomTextureCoordinate;\n" +
                "varying vec2 mBottomLeftTextureCoordinate;\n" +
                "varying vec2 mBottomRightTextureCoordinate;\n" +
                "" +
                "uniform float mThreshold;\n" +
                "uniform float mQuantizationLevels;\n" +
                "" +
                "const vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "" +
                "void main() {\n" +
                "   vec4 textureColor = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                "" +
                "   float bottomLeftIntensity = texture2D(sTexture, mBottomLeftTextureCoordinate).r;\n" +
                "   float topRightIntensity = texture2D(sTexture, mTopRightTextureCoordinate).r;\n" +
                "   float topLeftIntensity = texture2D(sTexture, mTopLeftTextureCoordinate).r;\n" +
                "   float bottomRightIntensity = texture2D(sTexture, mBottomRightTextureCoordinate).r;\n" +
                "   float leftIntensity = texture2D(sTexture, mLeftTextureCoordinate).r;\n" +
                "   float rightIntensity = texture2D(sTexture, mRightTextureCoordinate).r;\n" +
                "   float bottomIntensity = texture2D(sTexture, mBottomTextureCoordinate).r;\n" +
                "   float topIntensity = texture2D(sTexture, mTopTextureCoordinate).r;\n" +
                "   float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + " +
                "       bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "   float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + " +
                "       bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "" +
                "   float mag = length(vec2(h, v));\n" +
                "   vec3 posterizedImageColor = floor((textureColor.rgb * mQuantizationLevels) + 0.5) " +
                "       / mQuantizationLevels;\n" +
                "   float thresholdTest = 1.0 - step(mThreshold, mag);\n" +
                "" +
                "   gl_FragColor = vec4(posterizedImageColor * thresholdTest, textureColor.a);\n" +
                "}\n";
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

        mThresholdLocation = GLES30.glGetUniformLocation(mProgramHandle, "mThreshold");
        GlUtil.checkLocation(mThresholdLocation, "mThreshold");

        mQuantizationLevelsLocation = GLES30.glGetUniformLocation(mProgramHandle, "mQuantizationLevels");
        GlUtil.checkLocation(mQuantizationLevelsLocation, "mQuantizationLevels");

        mTexelWidthLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTexelWidth");
        GlUtil.checkLocation(mTexelWidthLocation, "mTexelWidth");

        mTexelHeightLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTexelHeight");
        GlUtil.checkLocation(mTexelHeightLocation, "mTexelHeight");
    }

    @Override
    public void setTexSize(int width, int height) {
        super.setTexSize(width, height);
        mTexelWidth = 1f / width;
        mTexelHeight = 1f / height;
    }

    @Override
    public void optionalDraw(int textureId) {
        GLES20.glUniform1f(mTexelWidthLocation, mTexelWidth);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mTexelHeightLocation, mTexelHeight);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mThresholdLocation, mThreshold);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mQuantizationLevelsLocation, mQuantizationLevels);
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
        return calculatePercentByValue(0.1f, 0.4f, mThreshold);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mThreshold = calculateValueByPercent(0.1f, 0.4f, newValue);
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
