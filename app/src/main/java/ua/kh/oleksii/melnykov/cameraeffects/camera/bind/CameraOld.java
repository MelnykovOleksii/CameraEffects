package ua.kh.oleksii.melnykov.cameraeffects.camera.bind;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
    private Camera mCamera;
    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;

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
        Integer id = getCameraIdByType(cameraType);
        if (id == null) return;

        mCamera = Camera.open(id);
        mCamera.setDisplayOrientation(90);
        List<Camera.Size> lSupportedPreviewSizes = mCamera
                .getParameters().getSupportedPreviewSizes();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Camera.Size optimalPreviewSize = getOptimalPreviewSize(lSupportedPreviewSizes,
                width, height);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(optimalPreviewSize.width,
                optimalPreviewSize.height);
        mCamera.setParameters(parameters);

        mCallback.OnOpenedCameraSize(optimalPreviewSize.width,
                optimalPreviewSize.height);
    }

    @Override
    public void handleSetSurfaceTexture(SurfaceTexture surfaceTexture) {
        surfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mCamera.startPreview();
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void setCameraReadyCallback(CameraModule.Callback callback) {
        mCallback = callback;
    }

    @Override
    public void setOnFrameAvailableCallback(SurfaceTexture.OnFrameAvailableListener onFrameAvailableCallback) {
        mOnFrameAvailableListener = onFrameAvailableCallback;
    }

    @Override
    public void takePhoto() {
        mCamera.takePicture(null, null, (pBytes, pCamera) -> {
            if (pBytes != null) {
                File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Camera Effects");
                imageRoot.mkdir();

                File image = new File(imageRoot, "Camera Effects" + ".jpg");

                OutputStream output = null;
                try {
                    try {
                        output = new FileOutputStream(image);
                        output.write(pBytes);

                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                } catch (IOException pE) {
                    pE.printStackTrace();
                }
            }

        });
    }

    private Integer getCameraIdByType(CameraType cameraType) {
        if (cameraType == CameraType.NONE) return null;
        int lensFacing = cameraType == CameraType.BACK || cameraType == CameraType.ONLY_BACK ?
                Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;

        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == lensFacing) {
                return i;
            }
        }

        return null;
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

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }

        return optimalSize;
    }

}
