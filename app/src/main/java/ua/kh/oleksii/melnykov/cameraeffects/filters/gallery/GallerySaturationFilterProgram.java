package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculatePercentByValue;
import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculateValueByPercent;

public class GallerySaturationFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;
    private int mSaturationLocation;
    private float mSaturation;

    public GallerySaturationFilterProgram() {
        mSaturation = 1.7f;
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
                " const vec3 mLuminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                "" +
                "uniform float mSaturation;\n" +
                "" +
                "void main() {\n" +
                "    vec4 textureColor = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "    float luminance = dot(textureColor.rgb, mLuminanceWeighting);\n" +
                "    vec3 greyScaleColor = vec3(luminance);\n" +
                "" +
                "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, mSaturation), textureColor.w);\n" +
                " }";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");

        mSaturationLocation = GLES30.glGetUniformLocation(mProgramHandle, "mSaturation");
        GlUtil.checkLocation(mSaturationLocation, "mSaturation");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glUniform1f(mSaturationLocation, mSaturation);
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
        return calculatePercentByValue(0f, 2f, mSaturation);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mSaturation = calculateValueByPercent(0f, 2f, newValue);
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
