package ua.kh.oleksii.melnykov.cameraeffects.filters.camera;

import android.graphics.PointF;
import android.opengl.GLES30;

import java.nio.FloatBuffer;

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
public class CameraVignetteFilterProgram extends FilterBaseProgram {

    private int mVignetteCenterLocation;
    private PointF mVignetteCenter;

    private int mVignetteColorLocation;
    private float[] mVignetteColor;

    private int mVignetteStartLocation;
    private float mVignetteStart;

    private int mVignetteEndLocation;
    private float mVignetteEnd;

    public CameraVignetteFilterProgram() {
        mVignetteCenter = new PointF(.5f, .5f);
        mVignetteColor = new float[]{0.0f, 0.0f, 0.0f};
        mVignetteStart = 0.3f;
        mVignetteEnd = 0.75f;
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
                "uniform vec2 mVignetteCenter;\n" +
                "uniform vec3 mVignetteColor;\n" +
                "uniform float mVignetteStart;\n" +
                "uniform float mVignetteEnd;\n" +
                "" +
                "void main() {\n" +
                "   vec3 rgb = texture2D(sTexture, mOutputTextureCoordinate).rgb;\n" +
                "   float d = distance(mOutputTextureCoordinate, vec2(mVignetteCenter.x, mVignetteCenter.y));\n" +
                "   float percent = smoothstep(mVignetteStart, mVignetteEnd, d);\n" +
                "   gl_FragColor = vec4(mix(rgb.x, mVignetteColor.x, percent), " +
                "       mix(rgb.y, mVignetteColor.y, percent), mix(rgb.z, mVignetteColor.z, percent), 1.0);\n" +
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

        mVignetteCenterLocation = GLES30.glGetUniformLocation(mProgramHandle, "mVignetteCenter");
        GlUtil.checkLocation(mVignetteCenterLocation, "mVignetteCenter");

        mVignetteColorLocation = GLES30.glGetUniformLocation(mProgramHandle, "mVignetteColor");
        GlUtil.checkLocation(mVignetteColorLocation, "mVignetteColor");

        mVignetteStartLocation = GLES30.glGetUniformLocation(mProgramHandle, "mVignetteStart");
        GlUtil.checkLocation(mVignetteStartLocation, "mVignetteStart");

        mVignetteEndLocation = GLES30.glGetUniformLocation(mProgramHandle, "mVignetteEnd");
        GlUtil.checkLocation(mVignetteEndLocation, "mVignetteEnd");
    }

    @Override
    public void optionalDraw(int textureId) {
        float[] vec2 = new float[2];
        vec2[0] = mVignetteCenter.x;
        vec2[1] = mVignetteCenter.y;
        GLES30.glUniform2fv(mVignetteCenterLocation, 1, vec2, 0);
        GlUtil.checkGlError("glUniform2fv");

        GLES30.glUniform3fv(mVignetteColorLocation, 1, FloatBuffer.wrap(mVignetteColor));
        GlUtil.checkGlError("glUniform3fv");

        GLES30.glUniform1f(mVignetteStartLocation, mVignetteStart);
        GlUtil.checkGlError("glUniform1f");

        GLES30.glUniform1f(mVignetteEndLocation, mVignetteEnd);
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
        return true;
    }

    @Override
    public int getFirstSettingsValue() {
        return calculatePercentByValue(0f, .7f, mVignetteStart);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        float value = calculateValueByPercent(0f, .7f, newValue);
        mVignetteStart = value < mVignetteEnd ? value : mVignetteStart;
    }

    @Override
    public int getSecondSettingsValue() {
        return calculatePercentByValue(0f, 1f, mVignetteEnd);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        float value = calculateValueByPercent(0f, 1f, newValue);
        mVignetteEnd = value > mVignetteStart ? value : mVignetteEnd;
    }

    @Override
    public int getThirdSettingsValue() {
        return 0;
    }

    @Override
    public void setThirdSettingsValue(int newValue) {
        float value = calculateValueByPercent(0f, 3.1f, newValue);
        if (value >= 0f && value < 1f) {
            mVignetteColor = new float[]{value, 0.0f, 0.0f};
        } else if (value >= 1f && value < 2f) {
            mVignetteColor = new float[]{0.0f, value - 1, 0.0f};
        } else if (value >= 2 && value < 3f) {
            mVignetteColor = new float[]{0.0f, 0.0f, value - 2};
        } else {
            mVignetteColor = new float[]{1f, 1f, 1f};
        }
    }

    @Override
    public boolean isTouchListenerEnable() {
        return true;
    }

    @Override
    public void setTouchCoordinate(float x, float y, int screenWidth, int screenHeight, CameraType cameraType) {
        int maxHeight = screenHeight * 3 / 4;
        float newX = (x / screenWidth) - 1;
        float newY = (y / maxHeight) -
                (cameraType == CameraType.BACK || cameraType == CameraType.ONLY_BACK ? 0 : 1);
        mVignetteCenter = new PointF(newY < 0 ? newY * -1 : newY, newX < 0 ? newX * -1 : newX);
    }

}
