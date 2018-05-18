package ua.kh.oleksii.melnykov.cameraeffects;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ua.kh.oleksii.melnykov.cameraeffects.camera.CameraHandler;
import ua.kh.oleksii.melnykov.cameraeffects.camera.CameraInterface;
import ua.kh.oleksii.melnykov.cameraeffects.camera.CameraModule;
import ua.kh.oleksii.melnykov.cameraeffects.camera.CameraRender;
import ua.kh.oleksii.melnykov.cameraeffects.camera.CameraType;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Активити служит для работы с камерой и наложения различных фильтров на изображения с камеры.
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {

    //region статические поля
    private static final int CAMERA_PERMISSION = 147;
    //endregion

    //region view поля
    private ConstraintLayout mBottomLayout;
    private Button mTakePicture;
    private TextView mErrorText;
    private ImageButton mSwitchCamera;
    private GLSurfaceView mGLSurfaceView;
    //endregion

    //region поля для камеры
    @Nullable
    private CameraInterface mCameraInterface;
    private CameraType mCameraType = CameraType.NONE;
    @Nullable
    private CameraRender mCameraRender;
    @Nullable
    private CameraHandler mCameraHandler;
    //endregion

    /**
     * Статическая функция для создания Intent для запуска активити {@link CameraActivity}
     *
     * @param context контекст создания intent
     * @return intent создания активити {@link CameraActivity}
     */
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, CameraActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region инициализация view
        mBottomLayout = findViewById(R.id.main_bottom_bar_layout);
        mTakePicture = findViewById(R.id.main_take_picture);
        mErrorText = findViewById(R.id.main_error_text);
        mSwitchCamera = findViewById(R.id.main_switch_camera);
        mGLSurfaceView = findViewById(R.id.main_camera_surface_view);
        //endregion

        //region начальное состояние для view
        mSwitchCamera.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
        //endregion

        //region подключение слушателей для view
        mTakePicture.setOnClickListener(view -> {
            // если у нас нет разрешения на использование камеры и сохранение снимка, то
            // запрашиваю их снова
            if (getCameraPermission())
                Toast.makeText(this, "Сфоторгафировать", Toast.LENGTH_SHORT).show();
        });
        mSwitchCamera.setOnClickListener(view -> Toast.makeText(this,
                "Переключить камеру", Toast.LENGTH_SHORT).show());
        //endregion

        if (isSupportsOpenGLES3() && getCameraPermission())
            initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraInterface != null && mCameraType != CameraType.NONE)
            mCameraInterface.openCameraByType(mCameraType);
    }

    /**
     * Проверка, поддерживает ли устройство OpenGL ES 3.0,
     * если нет, то выводится сообщение об ошибке
     *
     * @return возвращает true, если OpenGL ES 3.0 поддерживает и false если нет
     */
    private boolean isSupportsOpenGLES3() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean isSupported = configurationInfo.reqGlEsVersion >= 0x00030000;

        if (!isSupported) showError(R.string.opengles3_not_supported);
        return isSupported;
    }

    /**
     * Запрос у системы на разрешение использования камеры, если это разрешение не предоставлено,
     * то запрашивается разрешение на использование камеры и записи во внешнюю память для
     * сохранения снимков.
     *
     * @return возвращает true, если разрешение предоставлено и false, если нет.
     */
    private boolean getCameraPermission() {
        boolean isGranted = PermissionChecker.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!isGranted) {
            showError(R.string.camera_permission_denied);
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION);
        }

        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            default:
                // если был другой запрос на разрешение, то выполняем
                // стандартное действие из суперкласса
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

            case CAMERA_PERMISSION:
                // если пользователем подтверждено рарешение на использование камеры,
                // то выполняем инициализацию камеры
                if (grantResults.length != 1 || grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) initCamera();
                else showError(R.string.camera_permission_denied);
                break;
        }
    }

    /**
     * Инициализация камеры и её открытие
     */
    private void initCamera() {
        mErrorText.setVisibility(View.GONE);

        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8,
                8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);

        mCameraInterface = new CameraModule(this).provideSupportCamera();
        mCameraHandler = new CameraHandler(this, mCameraInterface);
        mCameraRender = new CameraRender(mCameraHandler);

        mGLSurfaceView.setRenderer(mCameraRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLSurfaceView.requestRender();

        mCameraType = getCameraTypeForOpen(mCameraInterface);
        mCameraInterface.openCameraByType(mCameraType);
        if(mCameraType == CameraType.BACK || mCameraType == CameraType.FRONT)
            mSwitchCamera.setVisibility(View.VISIBLE);

        mCameraInterface.setCameraReadyCallback((previewWidth, previewHeight) -> {
            mCameraHandler.weakReferenceHandler();
            mGLSurfaceView.onResume();
            mGLSurfaceView.queueEvent(() ->
                    mCameraRender.setCameraPreviewSize(previewWidth, previewHeight,
                            mGLSurfaceView.getWidth(),
                            mGLSurfaceView.getHeight()));
        });
    }

    /**
     * Метод возвращает тип поддерживаемой камеры {@link CameraType}
     *
     * @param cameraInterface модель работы с камерой,
     *                        {@link ua.kh.oleksii.melnykov.cameraeffects.camera.CameraNew} или
     *                        {@link ua.kh.oleksii.melnykov.cameraeffects.camera.CameraOld}
     * @return возвращает тип поддерживаемой камеры {@link CameraType}, если поддерживается
     * {@link CameraType#FRONT} и {@link CameraType#BACK}, то возвращается {@link CameraType#BACK}
     * для того, чтобы изначально открыть именно эту камеру
     */
    private CameraType getCameraTypeForOpen(CameraInterface cameraInterface) {
        boolean hasFrontCamera = cameraInterface.hasFrontCamera();
        boolean hasBackCamera = cameraInterface.hasBackCamera();

        if (hasFrontCamera && hasBackCamera) return CameraType.BACK;
        else if (hasBackCamera) return CameraType.ONLY_BACK;
        else if (hasFrontCamera) return CameraType.ONLY_FRONT;
        else return CameraType.NONE;
    }

    /**
     * Выводит ошибку в специальный TextView и при этом скрывает view камеры и работы с ней
     *
     * @param resId id сторокового ресурса
     */
    private void showError(@StringRes int resId) {
        mErrorText.setVisibility(View.VISIBLE);
        mSwitchCamera.setVisibility(View.GONE);

        mErrorText.setText(resId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraInterface != null) mCameraInterface.closeCamera();
        if (mCameraRender != null)
            mGLSurfaceView.queueEvent(() -> mCameraRender.notifyPausing());
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraHandler != null) mCameraHandler.invalidateHandler();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLSurfaceView.requestRender();
    }
}
