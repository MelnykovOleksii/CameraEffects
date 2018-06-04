package ua.kh.oleksii.melnykov.cameraeffects.filters;

import android.support.annotation.StringRes;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraColorFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraContrastFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraDistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraNoFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryColorFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryContrastFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryDistortionFilterProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryNoFilterProgram;

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
            case NO_FILTER:
            default:
                return new CameraNoFilterProgram();
            case CUSHION_DISTORTION:
                return new CameraDistortionFilterProgram(true);
            case BARREL_DISTORTION:
                return new CameraDistortionFilterProgram(false);
            case CONTRAST:
                return new CameraContrastFilterProgram();
            case COLOR:
                return new CameraColorFilterProgram();
        }
    }

    public static FilterBaseProgram switchProgramByTypeForGallery(TYPE type) {
        switch (type) {
            case NO_FILTER:
            default:
                return new GalleryNoFilterProgram();
            case CUSHION_DISTORTION:
                return new GalleryDistortionFilterProgram(true);
            case BARREL_DISTORTION:
                return new GalleryDistortionFilterProgram(false);
            case CONTRAST:
                return new GalleryContrastFilterProgram();
            case COLOR:
                return new GalleryColorFilterProgram();
        }
    }

    public enum TYPE {
        NO_FILTER(R.string.filter_no),
        CUSHION_DISTORTION(R.string.filter_distortion_cushion),
        BARREL_DISTORTION(R.string.filter_distortion_barrel),
        CONTRAST(R.string.filter_contrast),
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
