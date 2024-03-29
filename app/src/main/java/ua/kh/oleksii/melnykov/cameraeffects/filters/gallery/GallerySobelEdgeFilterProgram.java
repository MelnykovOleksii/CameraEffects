package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

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
public class GallerySobelEdgeFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    private int mTexelWidthLocation;
    private float mTexelWidth;

    private int mTexelHeightLocation;
    private float mTexelHeight;

    private float mLineSize;

    private Integer mTexWidth, mTexHeight;

    public GallerySobelEdgeFilterProgram() {
        mLineSize = 1f;
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
                "void main() {\n" +
                "   vec4 textureColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "" +
                "   float bottomLeftIntensity = texture2D(mInputImageTexture, mLeftTextureCoordinate).r;\n" +
                "   float topRightIntensity = texture2D(mInputImageTexture, mTopRightTextureCoordinate).r;\n" +
                "   float topLeftIntensity = texture2D(mInputImageTexture, mTopLeftTextureCoordinate).r;\n" +
                "   float bottomRightIntensity = texture2D(mInputImageTexture, mBottomRightTextureCoordinate).r;\n" +
                "   float leftIntensity = texture2D(mInputImageTexture, mLeftTextureCoordinate).r;\n" +
                "   float rightIntensity = texture2D(mInputImageTexture, mRightTextureCoordinate).r;\n" +
                "   float bottomIntensity = texture2D(mInputImageTexture, mBottomTextureCoordinate).r;\n" +
                "   float topIntensity = texture2D(mInputImageTexture, mTopTextureCoordinate).r;\n" +
                "   float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + " +
                "       bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "   float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + " +
                "       bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "   float mag = length(vec2(h, v));\n" +
                "   gl_FragColor = vec4(vec3(mag), textureColor.a);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES30.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

        mTexelWidthLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTexelWidth");
        GlUtil.checkLocation(mTexelWidthLocation, "mTexelWidth");

        mTexelHeightLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTexelHeight");
        GlUtil.checkLocation(mTexelHeightLocation, "mTexelHeight");
    }

    @Override
    public void setTexSize(int width, int height) {
        super.setTexSize(width, height);
        mTexWidth = width;
        mTexHeight = height;
        calculateNewTexel();
    }

    private void calculateNewTexel() {
        mTexelWidth = mLineSize / mTexWidth;
        mTexelHeight = mLineSize / mTexHeight;
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(mGLUniformTexture, 0);
        }

        if (mTexHeight != null && mTexWidth != null) {
            GLES30.glUniform1f(mTexelWidthLocation, mTexelWidth);
            GlUtil.checkGlError("glUniform1f");

            GLES30.glUniform1f(mTexelHeightLocation, mTexelHeight);
            GlUtil.checkGlError("glUniform1f");
        }
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
        return calculatePercentByValue(0.1f, 5f, mLineSize);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mLineSize = calculateValueByPercent(0.1f, 5f, newValue);
        calculateNewTexel();
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
