package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

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
public class GalleryEmbossFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    private int mConvolutionLocation;
    private float[] mConvolution;

    private int mTexelWidthLocation;
    private float mTexelWidth;

    private int mTexelHeightLocation;
    private float mTexelHeight;

    private float mIntensity;

    public GalleryEmbossFilterProgram() {
        mIntensity = 1.5f;
        mConvolution = calculateNewConvolution();
    }

    @Override
    protected String getVertexShader() {
        return "attribute vec4 mPosition;\n" +
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
                "    mOutputTextureCoordinate = mInputTextureCoordinate.xy;\n" +
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
                "uniform sampler2D mInputImageTexture;\n" +
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
                "    vec4 bottomColor = texture2D(mInputImageTexture, mBottomTextureCoordinate);\n" +
                "    vec4 bottomLeftColor = texture2D(mInputImageTexture, mBottomLeftTextureCoordinate);\n" +
                "    vec4 bottomRightColor = texture2D(mInputImageTexture, mBottomRightTextureCoordinate);\n" +
                "    vec4 centerColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "    vec4 leftColor = texture2D(mInputImageTexture, mLeftTextureCoordinate);\n" +
                "    vec4 rightColor = texture2D(mInputImageTexture, mRightTextureCoordinate);\n" +
                "    vec4 topColor = texture2D(mInputImageTexture, mTopTextureCoordinate);\n" +
                "    vec4 topRightColor = texture2D(mInputImageTexture, mTopRightTextureCoordinate);\n" +
                "    vec4 topLeftColor = texture2D(mInputImageTexture, mTopLeftTextureCoordinate);\n" +
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
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

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
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

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
