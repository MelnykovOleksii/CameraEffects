package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

public class GalleryColorFilterProgram extends FilterBaseProgram {

    private int mColorFilterLocation;
    private int mGLUniformTexture;

    private float[] mColorFilter;

    public GalleryColorFilterProgram() {
        mColorFilter = new float[4];
        mColorFilter[0] = 0.55f;    // R
        mColorFilter[1] = 0.44f;    // G
        mColorFilter[2] = 0.74f;    // B
        mColorFilter[3] = 1.0f;     // A
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
        return "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform sampler2D mInputImageTexture;\n" +
                "" +
                "uniform vec4 mColorFilter;\n" +
                "" +
                "void main() {\n" +
                "    vec4 tc = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "    gl_FragColor = vec4(tc.r * mColorFilter.r, tc.g * mColorFilter.g, tc.b" +
                " * mColorFilter.b, tc.a * mColorFilter.a);\n" +
                "}\n";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

        mColorFilterLocation = GLES30.glGetUniformLocation(mProgramHandle, "mColorFilter");
        GlUtil.checkLocation(mColorFilterLocation, "mColorFilter");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glUniform4fv(mColorFilterLocation, 1, mColorFilter, 0);
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
        return (int) (mColorFilter[0] * 100f - 0.1f);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mColorFilter[0] = (1f - 0.1f) * newValue / 100 + 0.1f;
    }

    @Override
    public int getSecondSettingsValue() {
        return (int) (mColorFilter[1] * 100 - 0.1f);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        mColorFilter[1] = (1f - 0.1f) * newValue / 100 + 0.1f;
    }

    @Override
    public int getThirdSettingsValue() {
        return (int) (mColorFilter[2] * 100 - 0.1f);
    }

    @Override
    public void setThirdSettingsValue(int newValue) {
        mColorFilter[2] = (1f - 0.1f) * newValue / 100 + 0.1f;
    }

    @Override
    public boolean isTouchListenerEnable() {
        return false;
    }

    @Override
    public void setTouchCoordinate(float x, float y, int screenWidth, int screenHeight, CameraType cameraType) {

    }

}
