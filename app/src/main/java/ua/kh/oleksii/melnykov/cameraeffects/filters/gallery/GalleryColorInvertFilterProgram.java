package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
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
public class GalleryColorInvertFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    public GalleryColorInvertFilterProgram() {
    }

    @Override
    protected String getVertexShader() {
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
    protected String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform sampler2D mInputImageTexture;\n" +
                "" +
                "void main() {\n" +
                "    vec4 textureColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "    gl_FragColor = vec4((1.0 - textureColor.rgb), textureColor.w);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES30.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(mGLUniformTexture, 0);
        }
    }

    @Override
    public boolean isNeedFirstSettingParameter() {
        return false;
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
        return 0;
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
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
