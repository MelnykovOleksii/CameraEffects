package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

public class GalleryContrastFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;
    private int mContrastLocation;

    private float mContrast;

    public GalleryContrastFilterProgram() {
        mContrast = 1.2f; // 0.5f - 4f
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
                "uniform float mContrast;\n" +
                "" +
                "void main() {\n" +
                "     vec4 textureColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * mContrast + vec3(0.5)), textureColor.w);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES30.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

        mContrastLocation = GLES30.glGetUniformLocation(mProgramHandle, "mContrast");
        GlUtil.checkLocation(mContrastLocation, "mContrast");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(mGLUniformTexture, 0);
        }

        GLES30.glUniform1f(mContrastLocation, mContrast);
        GlUtil.checkGlError("glUniform1f");
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
        return (int) ((mContrast * 100f / 4f) + 0.5f);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mContrast = (4f - 0.5f) * newValue / 100f + 0.5f;
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
