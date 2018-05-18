package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.app.Activity;
import android.os.Build;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Модуль, который служит для переключения между двумя версиями камеры
 * {@link android.hardware.Camera} и {@link android.hardware.camera2}
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraModule {

    private Activity mContext;

    public interface Callback {
        void OnOpenedCameraSize(int previewWidth, int previewHeight);
    }

    /**
     * Модуль, который служит для переключения между двумя версиями камеры
     *
     * @param context контекст для работы камеры
     */
    public CameraModule(Activity context) {
        mContext = context;
    }

    /**
     * Возвращает поддерживаемый экземпляр камеры в зависимости от версии Android
     *
     * @return поддерживаемый экземпляр камеры
     */
    public CameraInterface provideSupportCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return new CameraNew(mContext);
        else return new CameraOld(mContext);
    }

}