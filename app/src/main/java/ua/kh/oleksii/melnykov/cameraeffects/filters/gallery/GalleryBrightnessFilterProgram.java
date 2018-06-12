package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculatePercentByValue;
import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculateValueByPercent;

public class GalleryBrightnessFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;
    private int mBrightnessFilterLocation;
    private float mBrightness;

    public GalleryBrightnessFilterProgram() {
        mBrightness = 0.2f; // from -1.0 to 1.0
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
        return "varying  vec2 mOutputTextureCoordinate;\n" +
                " uniform sampler2D mInputImageTexture;\n" +
                "" +
                " uniform float mBrightness;\n" +
                "" +
                " void main() {" +
                "     vec4 textureColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "     gl_FragColor = vec4((textureColor.rgb + vec3(mBrightness)), textureColor.w);\n" +
                " }";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES30.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

        mBrightnessFilterLocation = GLES30.glGetUniformLocation(mProgramHandle, "mBrightness");
        GlUtil.checkLocation(mBrightnessFilterLocation, "mBrightness");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(mGLUniformTexture, 0);
        }

        GLES30.glUniform1f(mBrightnessFilterLocation, mBrightness);
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
        return calculatePercentByValue(-1f, 1f, mBrightness);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mBrightness = calculateValueByPercent(-1f, 1f, newValue);
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
