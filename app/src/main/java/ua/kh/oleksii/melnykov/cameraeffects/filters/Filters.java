package ua.kh.oleksii.melnykov.cameraeffects.filters;

import android.support.annotation.StringRes;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraDistortionFilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.camera.CameraNoFilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryDistortionFilterBaseProgram;
import ua.kh.oleksii.melnykov.cameraeffects.filters.gallery.GalleryNoFilterBaseProgram;

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
                return new CameraNoFilterBaseProgram();
            case CUSHION_DISTORTION:
                return new CameraDistortionFilterBaseProgram(true);
            case BARREL_DISTORTION:
                return new CameraDistortionFilterBaseProgram(false);
        }
    }

    public static FilterBaseProgram switchProgramByTypeForGallery(TYPE type) {
        switch (type) {
            case NO_FILTER:
            default:
                return new GalleryNoFilterBaseProgram();
            case CUSHION_DISTORTION:
                return new GalleryDistortionFilterBaseProgram(true);
            case BARREL_DISTORTION:
                return new GalleryDistortionFilterBaseProgram(false);
        }
    }


    public enum TYPE {
        NO_FILTER(R.string.filter_no),
        CUSHION_DISTORTION(R.string.filter_distortion_cushion),
        BARREL_DISTORTION(R.string.filter_distortion_barrel);

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
