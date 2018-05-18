package ua.kh.oleksii.melnykov.cameraeffects.filters;

/**
 * <p> Created by Melnykov Oleksii on 18.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.filters </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class Filters {

    public enum TYPE {
        NO_FILTER(new float[]{1.0f, 1.0f, 1.0f, 1.0f});

        float[] mColors;

        TYPE(float[] filterColors) {
            mColors = filterColors;
        }

        public float[] toColor() {
            return mColors;
        }
    }

}
