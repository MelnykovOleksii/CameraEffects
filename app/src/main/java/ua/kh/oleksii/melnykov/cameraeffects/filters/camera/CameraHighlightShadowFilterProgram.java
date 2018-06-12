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
public class CameraHighlightShadowFilterProgram extends FilterBaseProgram {

    private int mShadowsLocation;
    private float mShadows;

    private int mHighlightsLocation;
    private float mHighlights;

    public CameraHighlightShadowFilterProgram() {
        mShadows = 0f;
        mHighlights = 1f;
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
                "const vec3 mLuminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
                "" +
                "uniform float mShadows;\n" +
                "uniform float mHighlights;\n" +
                "" +
                "void main() {\n" +
                " 	vec4 source = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                " 	float luminance = dot(source.rgb, mLuminanceWeighting);\n" +
                "" +
                " 	float shadow = clamp((pow(luminance, 1.0/(mShadows+1.0)) + (-0.76)*pow(luminance, " +
                "       2.0/(mShadows+1.0))) - luminance, 0.0, 1.0);\n" +
                " 	float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-mHighlights)) + " +
                "       (-0.8)*pow(1.0-luminance, 2.0/(2.0-mHighlights)))) - luminance, -1.0, 0.0);\n" +
                " 	vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * " +
                "       ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
                "" +
                " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
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

        mShadowsLocation = GLES30.glGetUniformLocation(mProgramHandle, "mShadows");
        GlUtil.checkLocation(mShadowsLocation, "mShadows");

        mHighlightsLocation = GLES30.glGetUniformLocation(mProgramHandle, "mHighlights");
        GlUtil.checkLocation(mHighlightsLocation, "mHighlights");
    }

    @Override
    public void optionalDraw(int textureId) {
        GLES30.glUniform1f(mShadowsLocation, mShadows);
        GlUtil.checkGlError("glUniform1f");

        GLES30.glUniform1f(mHighlightsLocation, mHighlights);
        GlUtil.checkGlError("glUniform1f");
    }

    @Override
    public boolean isNeedFirstSettingParameter() {
        return true;
    }

    @Override
    public boolean isNeedSecondSettingParameters() {
        return true;
    }

    @Override
    public boolean isNeedThirdSettingParameters() {
        return false;
    }

    @Override
    public int getFirstSettingsValue() {
        return calculatePercentByValue(0f, 1f, mShadows);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mShadows = calculateValueByPercent(0f, 1f, newValue);
    }

    @Override
    public int getSecondSettingsValue() {
        return calculatePercentByValue(0f, 1f, mHighlights);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        mHighlights = calculateValueByPercent(0f, 1f, newValue);
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
