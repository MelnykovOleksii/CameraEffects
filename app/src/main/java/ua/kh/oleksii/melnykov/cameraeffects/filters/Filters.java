package ua.kh.oleksii.melnykov.cameraeffects.filters;

import android.support.annotation.StringRes;

import ua.kh.oleksii.melnykov.cameraeffects.R;

/**
 * <p> Created by Melnykov Oleksii on 19.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class Filters {

    public static FilterProgram switchProgramByType(TYPE type) {
        switch (type) {
            case NO_FILTER:
            default:
                return new NoFilterProgram();
            case CUSHION_DISTORTION:
                return new DistortionFilterProgram(true);
            case BARREL_DISTORTION:
                return new DistortionFilterProgram(false);
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
