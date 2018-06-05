package ua.kh.oleksii.melnykov.cameraeffects.utils;

/**
 * <p> Created by Melnykov Oleksii on 05-Jun-18. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.utils </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CalculateValues {

    public static int calculatePercentByValue(float minValue, float maxValue, float value) {
        if (minValue < 0)
            return (int) (((value + Math.abs(minValue)) * 100) / (maxValue + Math.abs(minValue)));
        return (int) ((value * 100 / maxValue) + minValue);
    }

    public static float calculateValueByPercent(float minValue, float maxValue, int percent) {
        return ((maxValue - minValue) * percent) / 100 + minValue;
    }

}
