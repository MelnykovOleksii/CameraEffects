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
public class CameraWhiteBalanceFilterProgram extends FilterBaseProgram {

    private int mTemperatureLocation;
    private float mTemperature;

    private int mTintLocation;
    private float mTint;

    public CameraWhiteBalanceFilterProgram() {
        mTemperature = 2f;
        mTint = 0f;
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
                "const vec3 warmFilter = vec3(0.93, 0.54, 0.0);\n" +
                "const mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);\n" +
                "const mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);\n" +
                "" +
                "uniform float mTemperature;\n" +
                "uniform float mTint;\n" +
                "" +
                "" +
                "void main() {\n" +
                "	vec4 source = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                "	\n" +
                "	vec3 yiq = RGBtoYIQ * source.rgb;\n" +
                "	yiq.g = clamp(yiq.g + mTemperature * 0.05226, -0.5226, 0.5226);\n" +
                "	yiq.b = clamp(yiq.b + mTint * 0.05226, -0.5226, 0.5226);\n" +
                "	vec3 rgb = YIQtoRGB * yiq;\n" +
                "" +
                "	gl_FragColor = vec4(rgb, source.a);\n" +
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

        mTemperatureLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTemperature");
        GlUtil.checkLocation(mTemperatureLocation, "mTemperature");

        mTintLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTint");
        GlUtil.checkLocation(mTintLocation, "mTint");
    }

    @Override
    public void optionalDraw(int textureId) {
        GLES30.glUniform1f(mTemperatureLocation, mTemperature);
        GlUtil.checkGlError("glUniform1f");

        GLES30.glUniform1f(mTintLocation, mTint);
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
        return calculatePercentByValue(-18f, 18f, mTemperature);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mTemperature = calculateValueByPercent(-18f, 18f, newValue);
    }

    @Override
    public int getSecondSettingsValue() {
        return calculatePercentByValue(-10f, 10f, mTint);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        mTint = calculateValueByPercent(-10f, 10f, newValue);
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
