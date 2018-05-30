package ua.kh.oleksii.melnykov.cameraeffects.filters.camera;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

/**
 * <p> Created by Melnykov Oleksii on 18.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraDistortionFilterBaseProgram extends FilterBaseProgram {

    private boolean isCushionDistortion;

    private int mScaleLocation;
    private int mRadiusLocation;
    private int mCenterLocation;
    private int mAspectRatioLocation;

    private float mScale; // от -1.0 до 1.0
    private float mRadius; // от 0.0 до 1.0
    private PointF mCenter; // 0.5, 0.5
    private float mAspectRatio;

    public CameraDistortionFilterBaseProgram(boolean isCushionDistortion) {
        this.isCushionDistortion = isCushionDistortion;

        mCenter = new PointF(0.5f, 0.5f);
        mScale = isCushionDistortion ? -0.2f : 0.2f;
        mRadius = 0.2f;
    }

    @Override
    public String getVertexShader() {
        return "" +
                "uniform mat4 mTextureMatrix;\n" +
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
    public String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform samplerExternalOES sTexture;\n" +
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
                "   gl_FragColor = texture2D(sTexture, textureCoordinateToUse );    \n" +
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
    public boolean isNeedTwoSettingParameters() {
        return true;
    }

    @Override
    public void setTexSize(int width, int height) {
        super.setTexSize(width, height);
        mAspectRatio = (float) height / width;
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mRadius = 1f * (float) newValue / 100;
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        float value = 1f * (float) newValue / 100;
        mScale = isCushionDistortion ? value * -1 : value;
    }

    @Override
    public int getFirstSettingsValue() {
        return (int) (mRadius * 100f / 1f);
    }

    @Override
    public int getSecondSettingsValue() {
        int value = (int) (mScale * 100f / 1f);

        return value < 0 ? value * -1 : value;
    }

    @Override
    public int getFirstLeftIconResId() {
        return R.drawable.ic_left;
    }

    @Override
    public int getFirstRightIconResId() {
        return R.drawable.ic_right;
    }

    @Override
    public int getSecondLeftIconResId() {
        return R.drawable.ic_left;
    }

    @Override
    public int getSecondRightIconResId() {
        return R.drawable.ic_right;
    }

}