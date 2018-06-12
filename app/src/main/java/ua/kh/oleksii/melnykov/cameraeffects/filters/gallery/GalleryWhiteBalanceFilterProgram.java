package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculatePercentByValue;
import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculateValueByPercent;

public class GalleryWhiteBalanceFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    private int mTemperatureLocation;
    private float mTemperature;

    private int mTintLocation;
    private float mTint;


    public GalleryWhiteBalanceFilterProgram() {
        mTemperature = 2f;
        mTint = 0f;
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
                "const mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.211, -0.522, 0.311);\n" +
                "const mat3 YIQtoRGB = mat3(1.0, 0.956, 0.623, 1.0, -0.272, -0.648, 1.0, -1.105, 1.705);\n" +
                "" +
                "uniform float mTemperature;\n" +
                "uniform float mTint;\n" +
                "" +
                "" +
                "void main() {\n" +
                "	vec4 source = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "" +
                "	vec3 yiq = RGBtoYIQ * source.rgb;\n" +
                "	yiq.g = clamp(yiq.g + mTemperature * 0.05226, -0.5226, 0.5226);\n" +
                "	yiq.b = clamp(yiq.b + mTint * 0.05226, -0.5226, 0.5226);\n" +
                "	vec3 rgb = YIQtoRGB * yiq;\n" +
                "" +
                "	gl_FragColor = vec4(rgb, source.a);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES30.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");


        mTemperatureLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTemperature");
        GlUtil.checkLocation(mTemperatureLocation, "mTemperature");

        mTintLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTint");
        GlUtil.checkLocation(mTintLocation, "mTint");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glUniform1i(mGLUniformTexture, 0);
        }

        GLES30.glUniform1f(mTemperatureLocation, mTemperature);
        GlUtil.checkGlError("glUniform1f");

        GLES30.glUniform1f(mTintLocation, mTint);
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
        return false;
    }

    @Override
    public int getFirstSettingsValue() {
        return calculatePercentByValue(-18f, 18f, mTemperature);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mTemperature = calculateValueByPercent(-18f, 18f, newValue);
    }

    @Override
    public int getSecondSettingsValue() {
        return calculatePercentByValue(-10f, 10f, mTint);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        mTint = calculateValueByPercent(-10f, 10f, newValue);
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