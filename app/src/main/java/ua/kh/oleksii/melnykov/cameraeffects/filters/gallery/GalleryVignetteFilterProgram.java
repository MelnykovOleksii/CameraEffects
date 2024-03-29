package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

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
public class GalleryVignetteFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    private int mVignetteCenterLocation;
    private PointF mVignetteCenter;

    private int mVignetteColorLocation;
    private float[] mVignetteColor;

    private int mVignetteStartLocation;
    private float mVignetteStart;

    private int mVignetteEndLocation;
    private float mVignetteEnd;

    public GalleryVignetteFilterProgram() {
        mVignetteCenter = new PointF(.5f, .5f);
        mVignetteColor = new float[]{0.0f, 0.0f, 0.0f};
        mVignetteStart = 0.3f;
        mVignetteEnd = 0.75f;
    }

    @Override
    protected String getVertexShader() {
        return "attribute vec4 mPosition;\n" +
                "attribute vec4 mInputTextureCoordinate;\n" +
                "varying vec2 mOutputTextureCoordinate;" +
                "" +
                "void main() {\n" +
                "    gl_Position = mPosition;\n" +
                "    mOutputTextureCoordinate = mInputTextureCoordinate.xy;\n" +
                "}";
    }

    @Override
    protected String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform sampler2D mInputImageTexture;\n" +
                "" +
                "uniform vec2 mVignetteCenter;\n" +
                "uniform vec3 mVignetteColor;\n" +
                "uniform float mVignetteStart;\n" +
                "uniform float mVignetteEnd;\n" +
                "" +
                "void main() {\n" +
                "   vec3 rgb = texture2D(mInputImageTexture, mOutputTextureCoordinate).rgb;\n" +
                "   float d = distance(mOutputTextureCoordinate, vec2(mVignetteCenter.x, mVignetteCenter.y));\n" +
                "   float percent = smoothstep(mVignetteStart, mVignetteEnd, d);\n" +
                "   gl_FragColor = vec4(mix(rgb.x, mVignetteColor.x, percent), " +
                "       mix(rgb.y, mVignetteColor.y, percent), mix(rgb.z, mVignetteColor.z, percent), 1.0);\n" +
                " }";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES30.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

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
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(mGLUniformTexture, 0);
        }

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
        float newX = x / screenWidth;
        float newY = y / maxHeight;
        mVignetteCenter = new PointF(newX < 0 ? newX * -1 : newX, newY < 0 ? newY * -1 : newY);
    }

}
