package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Класс, реализующий работу с камерой, которая с версии SDK 21 стала Deprecated
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
class CameraOld implements CameraInterface {

    private Activity mContext;
    private CameraModule.Callback mCallback;

    CameraOld(Activity context) {
        mContext = context;
    }

    @Override
    public boolean hasFrontCamera() {
        return hasCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    public boolean hasBackCamera() {
        return hasCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void openCameraByType(CameraType cameraType) {

    }

    @Override
    public void handleSetSurfaceTexture(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void closeCamera() {

    }

    @Override
    public void setCameraReadyCallback(CameraModule.Callback callback) {
        mCallback = callback;
    }

    /**
     * Выполняется проверка на наличие заданной кмеры (фронтальной или задней)
     *
     * @param cameraFacing фронтальная {@link Camera.CameraInfo#CAMERA_FACING_FRONT}
     *                     или задняя камеры {@link Camera.CameraInfo#CAMERA_FACING_BACK}
     * @return true - запрашиваемая камера поддерживается устройством, false - нет
     */
    private boolean hasCamera(int cameraFacing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                return true;
            }
        }
        return false;
    }

}
