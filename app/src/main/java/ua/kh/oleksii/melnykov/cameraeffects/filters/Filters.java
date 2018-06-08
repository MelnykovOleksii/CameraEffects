package ua.kh.oleksii.melnykov.cameraeffects.filters;

import android.support.annotation.StringRes;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraBrightnessFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraColorFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraColorInvertFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraContrastFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraDistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraEmbossFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraGammaFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraHighlightShadowFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraNoFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraPixelationFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraPosterizeFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraSaturationFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraSobelEdgeFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraToonFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraVignetteFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraWhiteBalanceFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryBrightnessFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryColorFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryColorInvertFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryContrastFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryDistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryEmbossFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryGammaFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryHighlightShadowFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryNoFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryPixelationFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryPosterizeFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GallerySaturationFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GallerySobelEdgeFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryToonFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryVignetteFilterProgram;
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
            case INVERT:
                return new CameraColorInvertFilterProgram();
            case PIXELATION:
                return new CameraPixelationFilterProgram();
            case POSTERIZE:
                return new CameraPosterizeFilterProgram();
            case SOBEL_EDGE:
                return new CameraSobelEdgeFilterProgram();
            case EMBOSS:
                return new CameraEmbossFilterProgram();
            case VIGNETTE:
                return new CameraVignetteFilterProgram();
            case TOON:
                return new CameraToonFilterProgram();
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
            case INVERT:
                return new GalleryColorInvertFilterProgram();
            case PIXELATION:
                return new GalleryPixelationFilterProgram();
            case POSTERIZE:
                return new GalleryPosterizeFilterProgram();
            case SOBEL_EDGE:
                return new GallerySobelEdgeFilterProgram();
            case EMBOSS:
                return new GalleryEmbossFilterProgram();
            case VIGNETTE:
                return new GalleryVignetteFilterProgram();
            case TOON:
                return new GalleryToonFilterProgram();
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
        COLOR(R.string.filter_color),
        INVERT(R.string.filter_invert),
        PIXELATION(R.string.filter_pixelation),
        POSTERIZE(R.string.filter_posterize),
        SOBEL_EDGE(R.string.filter_sobel_edge),
        EMBOSS(R.string.filter_emboss),
        VIGNETTE(R.string.filter_vignette),
        TOON(R.string.filter_toon);

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
