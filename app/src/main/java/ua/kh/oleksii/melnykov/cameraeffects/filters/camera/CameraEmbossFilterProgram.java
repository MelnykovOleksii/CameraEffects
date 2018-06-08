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
public class CameraEmbossFilterProgram extends FilterBaseProgram {

    private int mConvolutionLocation;
    private float[] mConvolution;

    private int mTexelWidthLocation;
    private float mTexelWidth;

    private int mTexelHeightLocation;
    private float mTexelHeight;

    private float mIntensity;

    public CameraEmbossFilterProgram() {
        mIntensity = 1.5f;
        mConvolution = calculateNewConvolution();
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
                "uniform mat3 mConvolution;\n" +
                "" +
                "void main() {\n" +
                "    vec4 bottomColor = texture2D(sTexture, mBottomTextureCoordinate);\n" +
                "    vec4 bottomLeftColor = texture2D(sTexture, mBottomLeftTextureCoordinate);\n" +
                "    vec4 bottomRightColor = texture2D(sTexture, mBottomRightTextureCoordinate);\n" +
                "    vec4 centerColor = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                "    vec4 leftColor = texture2D(sTexture, mLeftTextureCoordinate);\n" +
                "    vec4 rightColor = texture2D(sTexture, mRightTextureCoordinate);\n" +
                "    vec4 topColor = texture2D(sTexture, mTopTextureCoordinate);\n" +
                "    vec4 topRightColor = texture2D(sTexture, mTopRightTextureCoordinate);\n" +
                "    vec4 topLeftColor = texture2D(sTexture, mTopLeftTextureCoordinate);\n" +
                "" +
                "    vec4 resultColor = topLeftColor * mConvolution[0][0] + topColor " +
                "       * mConvolution[0][1] + topRightColor * mConvolution[0][2];\n" +
                "    resultColor += leftColor * mConvolution[1][0] + centerColor " +
                "       * mConvolution[1][1] + rightColor * mConvolution[1][2];\n" +
                "    resultColor += bottomLeftColor * mConvolution[2][0] + bottomColor " +
                "       * mConvolution[2][1] + bottomRightColor * mConvolution[2][2];\n" +
                "" +
                "    gl_FragColor = resultColor;\n" +
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


        mConvolutionLocation = GLES30.glGetUniformLocation(mProgramHandle, "mConvolution");
        GlUtil.checkLocation(mConvolutionLocation, "mConvolution");

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

        GLES20.glUniformMatrix3fv(mConvolutionLocation, 1, false, mConvolution, 0);
        GlUtil.checkGlError("glUniformMatrix3fv");
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
        return calculatePercentByValue(0f, 4f, mIntensity);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mIntensity = calculateValueByPercent(0f, 4f, newValue);
        mConvolution = calculateNewConvolution();
    }

    private float[] calculateNewConvolution() {
        return new float[]{
                mIntensity * (-2.0f), -mIntensity, 0.0f,
                -mIntensity, 1.0f, mIntensity,
                0.0f, mIntensity, mIntensity * 2.0f,
        };
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
