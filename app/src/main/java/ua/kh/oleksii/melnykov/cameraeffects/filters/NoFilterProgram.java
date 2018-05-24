package ua.kh.oleksii.melnykov.cameraeffects.filters;

import ua.kh.oleksii.melnykov.cameraeffects.R;

/**
 * <p> Created by Melnykov Oleksii on 5/22/2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class NoFilterProgram extends FilterProgram {

    @Override
    public String getShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                "}";
    }

    @Override
    public void optionalDraw() {
        // nothing to do
    }

    @Override
    public boolean isNeedTwoSettingParameters() {
        return false;
    }

    @Override
    public int getFirstSettingsValue() {
        return 0;
    }

    @Override
    public void setFirstSettingsValue(int newValue) {
        // nothing to do
    }

    @Override
    public int getSecondSettingsValue() {
        return 0;
    }

    @Override
    public void setSecondSettingsValue(int newValue) {
        // nothing to do
    }

    @Override
    public int getFirstLeftIconResId() {
        return R.drawable.ic_left;
    }

    @Override
    public int getFirstRightIconResId() {
        return R.drawable.ic_right;
    }

    @Override
    public int getSecondLeftIconResId() {
        return R.drawable.ic_left;
    }

    @Override
    public int getSecondRightIconResId() {
        return R.drawable.ic_right;
    }

}