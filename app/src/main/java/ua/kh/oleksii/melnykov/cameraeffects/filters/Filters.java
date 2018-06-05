package ua.kh.oleksii.melnykov.cameraeffects.filters;

import android.support.annotation.StringRes;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraBrightnessFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraColorFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraContrastFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraDistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraGammaFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraHighlightShadowFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraNoFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraSaturationFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraWhiteBalanceFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryBrightnessFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryColorFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryContrastFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryDistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryGammaFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryHighlightShadowFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryNoFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GallerySaturationFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryWhiteBalanceFilterProgram;

/**
 * <p> Created by Melnykov Oleksii on 19.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class Filters {

    public static FilterBaseProgram switchProgramByTypeForCamera(TYPE type) {
        switch (type) {

            // Без фильтра
            case NO_FILTER:
            default:
                return new CameraNoFilterProgram();

            // Коррекция
            case CUSHION_DISTORTION:
                return new CameraDistortionFilterProgram(true);
            case BARREL_DISTORTION:
                return new CameraDistortionFilterProgram(false);
            case CONTRAST:
                return new CameraContrastFilterProgram();
            case GAMMA:
                return new CameraGammaFilterProgram();
            case BRIGHTNESS:
                return new CameraBrightnessFilterProgram();
            case SATURATION:
                return new CameraSaturationFilterProgram();
            case HIGHLIGHT_SHADOW:
                return new CameraHighlightShadowFilterProgram();
            case WHITE_BALANCE:
                return new CameraWhiteBalanceFilterProgram();

            // Фильтры (c 9й позиции)
            case COLOR:
                return new CameraColorFilterProgram();
        }
    }

    public static FilterBaseProgram switchProgramByTypeForGallery(TYPE type) {
        switch (type) {

            // Без фильтра
            case NO_FILTER:
            default:
                return new GalleryNoFilterProgram();

            // Коррекция
            case CUSHION_DISTORTION:
                return new GalleryDistortionFilterProgram(true);
            case BARREL_DISTORTION:
                return new GalleryDistortionFilterProgram(false);
            case CONTRAST:
                return new GalleryContrastFilterProgram();
            case GAMMA:
                return new GalleryGammaFilterProgram();
            case BRIGHTNESS:
                return new GalleryBrightnessFilterProgram();
            case SATURATION:
                return new GallerySaturationFilterProgram();
            case HIGHLIGHT_SHADOW:
                return new GalleryHighlightShadowFilterProgram();
            case WHITE_BALANCE:
                return new GalleryWhiteBalanceFilterProgram();

            // Фильтры (c 9й позиции)
            case COLOR:
                return new GalleryColorFilterProgram();
        }
    }

    public enum TYPE {
        // Без фильтра
        NO_FILTER(R.string.filter_no),
        // Коррекция
        CUSHION_DISTORTION(R.string.filter_distortion_cushion),
        BARREL_DISTORTION(R.string.filter_distortion_barrel),
        CONTRAST(R.string.filter_contrast),
        GAMMA(R.string.filter_gamma),
        BRIGHTNESS(R.string.filter_brightness),
        SATURATION(R.string.filter_saturation),
        HIGHLIGHT_SHADOW(R.string.filter_highlight_shadow),
        WHITE_BALANCE(R.string.filter_white_balance),
        // Фильтры (c 9й позиции)
        COLOR(R.string.filter_color);

        @StringRes
        private int nameId;

        TYPE(@StringRes int name) {
            nameId = name;
        }

        @StringRes
        public int getStringId() {
            return nameId;
        }
    }

}
