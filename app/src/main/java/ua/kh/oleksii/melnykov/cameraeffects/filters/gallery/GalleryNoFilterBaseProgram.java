package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES20;

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
public class GalleryNoFilterBaseProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    public GalleryNoFilterBaseProgram() {

    }

    @Override
    public String getVertexShader() {
        return "" +
                "attribute vec4 mPosition;\n" +
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
        return "" +
                "uniform sampler2D mInputImageTexture;\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "" +
                "void main() {\n" +
                "     gl_FragColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "}";
    }


    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle,
                "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
    }

    @Override
    public boolean isNeedTwoSettingParameters() {
        return false;
    }

    @Override
    public int getSecondSettingsValue() {
        return 0;
    }

    @Override
    public void setSecondSettingsValue(int newValue) {

    }

    @Override
    public int getFirstSettingsValue() {
        return 0;
    }

    @Override
    public void setFirstSettingsValue(int newValue) {

    }

    @Override
    public int getFirstLeftIconResId() {
        return 0;
    }

    @Override
    public int getFirstRightIconResId() {
        return 0;
    }

    @Override
    public int getSecondLeftIconResId() {
        return 0;
    }

    @Override
    public int getSecondRightIconResId() {
        return 0;
    }

}