package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

/**
 * <p> Created by Melnykov Oleksii on 30-May-18. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.gallery.bind </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class GalleryDistortionFilterProgram extends FilterBaseProgram {

    private boolean isCushionDistortion;
    private int mScaleLocation;
    private int mRadiusLocation;
    private int mCenterLocation;
    private int mAspectRatioLocation;
    private float mScale; // от -1.0 до 1.0
    private float mRadius; // от 0.0 до 1.0
    private PointF mCenter; // 0.5, 0.5
    private float mAspectRatio;
    private int mGLUniformTexture;

    public GalleryDistortionFilterProgram(boolean isCushionDistortion) {
        this.isCushionDistortion = isCushionDistortion;

        mCenter = new PointF(0.5f, 0.5f);
        mScale = this.isCushionDistortion ? -0.2f : 0.2f;
        mRadius = 0.2f;
    }

    @Override
    public String getVertexShader() {
        return "attribute vec4 mPosition;\n" +
                "attribute vec4 mInputTextureCoordinate;\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "" +
                "void main() {\n" +
                "    gl_Position = mPosition;\n" +
                "    mOutputTextureCoordinate = mInputTextureCoordinate.xy;\n" +
                "}";
    }

    @Override
    public String getShader() {
        return "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform sampler2D mInputImageTexture;\n" +
                "" +
                "uniform float aspectRatio;\n" +
                "uniform vec2 center;\n" +
                "uniform float radius;\n" +
                "uniform float scale;\n" +
                "" +
                "void main() {\n" +
                "   vec2 textureCoordinateToUse = vec2(mOutputTextureCoordinate.x, " +
                "       (mOutputTextureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "   float dist = distance(center, textureCoordinateToUse);\n" +
                "   textureCoordinateToUse = mOutputTextureCoordinate;\n" +
                "" +
                "   if (dist < radius) {\n" +
                "       textureCoordinateToUse -= center;\n" +
                "       float percent = 1.0 - ((radius - dist) / radius) * scale;\n" +
                "       percent = percent * percent;\n" +
                "       textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
                "       textureCoordinateToUse += center;\n" +
                "   }\n" +
                "" +
                "   gl_FragColor = texture2D(mInputImageTexture, textureCoordinateToUse );\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

        mScaleLocation = GLES30.glGetUniformLocation(mProgramHandle, "scale");
        GlUtil.checkLocation(mScaleLocation, "scale");

        mRadiusLocation = GLES30.glGetUniformLocation(mProgramHandle, "radius");
        GlUtil.checkLocation(mRadiusLocation, "radius");

        mCenterLocation = GLES30.glGetUniformLocation(mProgramHandle, "center");
        GlUtil.checkLocation(mCenterLocation, "center");

        mAspectRatioLocation = GLES30.glGetUniformLocation(mProgramHandle, "aspectRatio");
        GlUtil.checkLocation(mAspectRatioLocation, "aspectRatio");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glUniform1f(mScaleLocation, mScale);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mRadiusLocation, mRadius);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mAspectRatioLocation, mAspectRatio);
        GlUtil.checkGlError("glUniform1f");

        float[] vec2 = new float[2];
        vec2[0] = mCenter.x;
        vec2[1] = mCenter.y;
        GLES20.glUniform2fv(mCenterLocation, 1, vec2, 0);
        GlUtil.checkGlError("glUniform2fv");
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
    public void setTexSize(int width, int height) {
        super.setTexSize(width, height);
        mAspectRatio = (float) height / width;
    }

    @Override
    public int getFirstSettingsValue() {
        return (int) (mRadius * 100f / 1f);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mRadius = 1f * (float) newValue / 100;
    }

    @Override
    public int getSecondSettingsValue() {
        int value = (int) (mScale * 100f / 1f);
        return value < 0 ? value * -1 : value;
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        float value = 1f * (float) newValue / 100;
        mScale = isCushionDistortion ? value * -1 : value;
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
        return true;
    }

    @Override
    public void setTouchCoordinate(float x, float y, int screenWidth, int screenHeight, CameraType cameraType) {
        int maxHeight = screenHeight * 3 / 4;
        float newX = x / screenWidth;
        float newY = y / maxHeight;
        mCenter = new PointF(newX < 0 ? newX * -1 : newX, newY < 0 ? newY * -1 : newY);

    }

}