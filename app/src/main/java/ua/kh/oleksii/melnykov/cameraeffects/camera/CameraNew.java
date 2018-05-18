package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;

import java.util.Collections;

import ua.kh.oleksii.melnykov.cameraeffects.CameraActivity;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Класс, реализующий работу с камерой v2, которая доступна с версии SDK 21
 * {@link Build.VERSION_CODES#LOLLIPOP}
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class CameraNew implements CameraInterface {

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private final Activity mContext;
    private final CameraManager mCameraManager;
    private CameraModule.Callback mCallback;

    @Nullable
    private CameraDevice mCameraDevice;
    @Nullable
    private SurfaceTexture mSurfaceTexture;
    @Nullable
    private Size mSize;
    @Nullable
    private CameraCaptureSession mCameraCaptureSession;

    CameraNew(Activity context) {
        mContext = context;

        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public boolean hasFrontCamera() {
        try {
            return hasCamera(CameraCharacteristics.LENS_FACING_FRONT);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hasBackCamera() {
        try {
            return hasCamera(CameraCharacteristics.LENS_FACING_BACK);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void openCameraByType(CameraType cameraType) {
        try {
            String type = getCameraIdByType(cameraType);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || type == null) return;

            mCameraManager.openCamera(type, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    try {
                        mCameraDevice = camera;
                        CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(camera.getId());
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        int height = displaymetrics.heightPixels;
                        int width = displaymetrics.widthPixels;
                        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                        if (streamConfigurationMap != null) {
                            mSize = getOptimalPreviewSize(streamConfigurationMap
                                            .getOutputSizes(SurfaceTexture.class),
                                    width, height);
                            mCallback.OnOpenedCameraSize(
                                    mSize.getWidth(), mSize.getHeight());
                        }

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleSetSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        surfaceTexture.setOnFrameAvailableListener((CameraActivity) mContext);

        try {
            surfaceTexture.setDefaultBufferSize(mSize.getWidth(), mSize.getHeight());
            Surface surface = new Surface(surfaceTexture);
            CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW);
            captureRequest.addTarget(surface);
            mCameraDevice.createCaptureSession(Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            mCameraCaptureSession = cameraCaptureSession;
                            try {
                                captureRequest.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                captureRequest.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                CaptureRequest request = captureRequest.build();
                                mCameraCaptureSession.setRepeatingRequest(request,
                                        null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, null);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;

            if (mBackgroundThread != null) {
                mBackgroundThread.quitSafely();
                try {
                    mBackgroundThread.join();
                    mBackgroundThread = null;
                    mBackgroundHandler = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (null != mCameraCaptureSession) {
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
        }
    }

    @Override
    public void setCameraReadyCallback(CameraModule.Callback callback) {
        mCallback = callback;
    }

    /**
     * Выполняется проверка на наличие заданной кмеры (фронтальной или задней)
     *
     * @param lensFacing фронтальная {@link CameraCharacteristics#LENS_FACING_FRONT}
     *                   или задняя камеры {@link CameraCharacteristics#LENS_FACING_BACK}
     * @return true - запрашиваемая камера поддерживается устройством, false - нет
     * @throws CameraAccessException ошибка в камере из-за не предоставленного разрешения или
     *                               из-за того, что {@link android.hardware.camera2.CameraDevice}
     *                               больше не действителен
     */
    private boolean hasCamera(int lensFacing) throws CameraAccessException {
        String[] cameraIDs = mCameraManager.getCameraIdList();
        for (String cameraId : cameraIDs) {
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            Integer cameraDirection = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if (cameraDirection != null && cameraDirection == lensFacing)
                return true;
        }
        return false;
    }

    /**
     * Выполняет получение id камеры по {@link CameraType}
     *
     * @param cameraType тип необходимой камеры или NONE
     * @return id камеры или NULL
     * @throws CameraAccessException ошибка в камере из-за не предоставленного разрешения или
     *                               из-за того, что {@link android.hardware.camera2.CameraDevice}
     *                               больше не действителен
     */
    @Nullable
    private String getCameraIdByType(CameraType cameraType) throws CameraAccessException {
        if (cameraType == CameraType.NONE) return null;
        int lensFacing = cameraType == CameraType.BACK || cameraType == CameraType.ONLY_BACK ?
                CameraCharacteristics.LENS_FACING_BACK : CameraCharacteristics.LENS_FACING_FRONT;
        String[] cameraIDs = mCameraManager.getCameraIdList();

        for (String cameraId : cameraIDs) {
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            Integer cameraDirection = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if (cameraDirection != null && cameraDirection == lensFacing)
                return cameraId;
        }

        return null;
    }

    private Size getOptimalPreviewSize(Size[] sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizes) {
            double ratio = (double) size.getHeight() / size.getWidth();
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.getHeight() - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.getHeight() - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - h);
                }
            }
        }

        return optimalSize;
    }

}