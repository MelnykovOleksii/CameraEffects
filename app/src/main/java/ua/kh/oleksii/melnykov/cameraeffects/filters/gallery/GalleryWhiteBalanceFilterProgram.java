package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES20;
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
        mTemperature = calculateTemperature(5000f);
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
                "const vec3 warmFilter = vec3(0.93, 0.54, 0.0);\n" +
                "const mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);\n" +
                "const mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);\n" +
                "" +
                "uniform float mTemperature;\n" +
                "uniform float mTint;\n" +
                "" +
                "" +
                "void main() {\n" +
                "	vec4 source = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                "	\n" +
                "	vec3 yiq = RGBtoYIQ * source.rgb; //adjusting mTint\n" +
                "	yiq.b = clamp(yiq.b + mTint*0.5226*0.1, -0.5226, 0.5226);\n" +
                "	vec3 rgb = YIQtoRGB * yiq;\n" +
                "" +
                "	vec3 processed = vec3(\n" +
                "		(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * " +
                "           (1.0 - warmFilter.r))), //adjusting temperature\n" +
                "		(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * " +
                "           (1.0 - warmFilter.g))), \n" +
                "		(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * " +
                "           (1.0 - warmFilter.b))));\n" +
                "" +
                "	gl_FragColor = vec4(mix(rgb, processed, mTemperature), source.a);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");


        mTemperatureLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTemperature");
        GlUtil.checkLocation(mTemperatureLocation, "mTemperature");

        mTintLocation = GLES30.glGetUniformLocation(mProgramHandle, "mTint");
        GlUtil.checkLocation(mTintLocation, "mTint");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glUniform1f(mTemperatureLocation, mTemperature);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mTintLocation, mTint);
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
        return calculatePercentByValue(1f, 10f, 5f);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        float newTemperature = calculateValueByPercent(1000f, 10000f, newValue);
        mTemperature = calculateTemperature(newTemperature);
    }

    @Override
    public int getSecondSettingsValue() {
        return calculatePercentByValue(0f, 1.5f, mTint);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        mTint = calculateValueByPercent(0f, 1.5f, newValue);
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

    private float calculateTemperature(float value) {
        return value < 5000f ?
                0.0004f * (value - 5000f) : 0.00006f * (value - 5000f);
    }

}
