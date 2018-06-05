package ua.kh.oleksii.melnykov.cameraeffects.filters.gallery;

import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculatePercentByValue;
import static ua.kh.oleksii.melnykov.cameraeffects.utils.CalculateValues.calculateValueByPercent;

public class GalleryHighlightShadowFilterProgram extends FilterBaseProgram {

    private int mGLUniformTexture;

    private int mShadowsLocation;
    private float mShadows;

    private int mHighlightsLocation;
    private float mHighlights;


    public GalleryHighlightShadowFilterProgram() {
        mShadows = 0f;
        mHighlights = 1f;
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
                "const vec3 mLuminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
                "" +
                "uniform float mShadows;\n" +
                "uniform float mHighlights;\n" +
                "" +
                "void main() {\n" +
                " 	vec4 source = texture2D(mInputImageTexture, mOutputTextureCoordinate);\n" +
                " 	float luminance = dot(source.rgb, mLuminanceWeighting);\n" +
                "" +
                " 	float shadow = clamp((pow(luminance, 1.0/(mShadows+1.0)) + (-0.76)*pow(luminance, " +
                "       2.0/(mShadows+1.0))) - luminance, 0.0, 1.0);\n" +
                " 	float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-mHighlights)) + " +
                "       (-0.8)*pow(1.0-luminance, 2.0/(2.0-mHighlights)))) - luminance, -1.0, 0.0);\n" +
                " 	vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * " +
                "       ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
                "" +
                " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
                "}";
    }

    @Override
    protected void optionalSetup() {
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramHandle, "mInputImageTexture");
        GlUtil.checkLocation(mGLUniformTexture, "mInputImageTexture");


        mShadowsLocation = GLES30.glGetUniformLocation(mProgramHandle, "mShadows");
        GlUtil.checkLocation(mShadowsLocation, "mShadows");

        mHighlightsLocation = GLES30.glGetUniformLocation(mProgramHandle, "mHighlights");
        GlUtil.checkLocation(mHighlightsLocation, "mHighlights");
    }

    @Override
    public void optionalDraw(int textureId) {
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glUniform1f(mShadowsLocation, mShadows);
        GlUtil.checkGlError("glUniform1f");

        GLES20.glUniform1f(mHighlightsLocation, mHighlights);
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
        return calculatePercentByValue(0f, 1f, mShadows);
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        mShadows = calculateValueByPercent(0f, 1f, newValue);
    }

    @Override
    public int getSecondSettingsValue() {
        return calculatePercentByValue(0f, 1f, mHighlights);
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        mHighlights = calculateValueByPercent(0f, 1f, newValue);
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
