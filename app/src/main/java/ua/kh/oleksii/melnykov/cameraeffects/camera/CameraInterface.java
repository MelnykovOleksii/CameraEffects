package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.graphics.SurfaceTexture;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Интерфейс работы с камерой
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public interface CameraInterface {

    /**
     * Проверка на наличие фронтальной камеры у устройства
     *
     * @return true - фронтальная камера есть, false - фронтальной камеры нет
     */
    boolean hasFrontCamera();

    /**
     * Проверка на наличие задней камеры у устройства
     *
     * @return true - задняя камера есть, false - задней камеры нет
     */
    boolean hasBackCamera();

    void openCameraByType(CameraType cameraType);

    void handleSetSurfaceTexture(SurfaceTexture surfaceTexture);

    void closeCamera();

    void setCameraReadyCallback(CameraModule.Callback callback);

}
