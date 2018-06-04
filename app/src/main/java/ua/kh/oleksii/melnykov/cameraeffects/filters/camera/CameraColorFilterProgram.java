package ua.kh.oleksii.melnykov.cameraeffects.filters.camera;

import android.opengl.GLES20;
import android.opengl.GLES30;

import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.FilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.utils.GlUtil;

public class CameraColorFilterProgram extends FilterBaseProgram {

    private int mColorFilterLocation;
    private float[] mColorFilter;

    public CameraColorFilterProgram() {
        mColorFilter = new float[4];
        mColorFilter[0] = 0.55f;    // R
        mColorFilter[1] = 0.44f;    // G
        mColorFilter[2] = 0.74f;    // B
        mColorFilter[3] = 1.0f;     // A
    }

    @Override
    protected String getVertexShader() {
        return "uniform mat4 mTextureMatrix;\n" +
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
    protected String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 mOutputTextureCoordinate;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "" +
                "uniform vec4 mColorFilter;\n" +
                "" +
                "void main() {\n" +
                "    vec4 tc = texture2D(sTexture, mOutputTextureCoordinate);\n" +
                "    gl_FragColor = vec4(tc.r * mColorFilter.r, tc.g * mColorFilter.g, tc.b" +
                " * mColorFilter.b, tc.a * mColorFilter.a);\n" +
                "}\n";
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

        mColorFilterLocation = GLES30.glGetUniformLocation(mProgramHandle, "mColorFilter");
        GlUtil.checkLocation(mColorFilterLocation, "mColorFilter");
    }

    @Override
    public void optionalDraw(int textureId) {
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
