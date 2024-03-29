package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import ua.kh.oleksii.melnykov.cameraeffects.R;
import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraHandler;
import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraInterface;
import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraModule;
import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraRenderer;
import ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraType;
import ua.kh.oleksii.melnykov.cameraeffects.filters.listAdapter.FilterAdapter;
import ua.kh.oleksii.melnykov.cameraeffects.gallery.GalleryActivity;
import ua.kh.oleksii.melnykov.cameraeffects.utils.RecyclerSectionItemDecoration;
import ua.kh.oleksii.melnykov.cameraeffects.utils.SeekBarProgressChangeListener;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Активити служит для работы с камерой и наложения различных фильтров на изображения с камеры.
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class CameraActivity extends AppCompatActivity {

    //region статические поля
    private static final int CAMERA_PERMISSION = 147;
    //endregion

    private int mScreenWidth;
    private int mScreenHeight;

    //region view поля
    private TextView mErrorText;
    private ImageButton mSwitchCamera;
    private GLSurfaceView mGLSurfaceView;
    private RecyclerView mFilterList;
    private ConstraintLayout mListLayout;
    private ConstraintLayout mFilterSettingsLayout;
    private ConstraintLayout mFilterSetting1Layout;
    private ConstraintLayout mFilterSetting2Layout;
    private ConstraintLayout mFilterSetting3Layout;
    private SeekBar mFilterSetting1SeekBar;
    private SeekBar mFilterSetting2SeekBar;
    private SeekBar mFilterSetting3SeekBar;
    private Button mTakePicture;
    //endregion

    private FilterAdapter mFilterAdapter;
    private FilterAdapter.OnItemClickCallback mOnFilterItemClickCallback;

    //region поля для камеры
    private CameraType mCameraType = CameraType.NONE;
    private CameraRenderer mCameraRenderer;
    @Nullable
    private CameraInterface mCameraInterface;
    @Nullable
    private CameraHandler mCameraHandler;
    private ProgressBar mSaveImageProgress;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        //region инициализация view
        mTakePicture = findViewById(R.id.camera_take_picture);
        mErrorText = findViewById(R.id.camera_error_text);
        mSwitchCamera = findViewById(R.id.camera_switch_camera);
        mFilterList = findViewById(R.id.include_filters_list);
        mGLSurfaceView = findViewById(R.id.camera_surface_view);
        mListLayout = findViewById(R.id.include_filters_list_layout);
        ImageView toGallery = findViewById(R.id.main_gallery);
        mFilterSettingsLayout = findViewById(R.id.include_filter_settings_layout);
        mFilterSetting1Layout = findViewById(R.id.include_filter_setting1);
        mFilterSetting2Layout = findViewById(R.id.include_filter_setting2);
        mFilterSetting3Layout = findViewById(R.id.include_filter_setting3);
        mFilterSetting1SeekBar = findViewById(R.id.include_filter_setting1_seek_bar);
        mFilterSetting2SeekBar = findViewById(R.id.include_filter_setting2_seek_bar);
        mFilterSetting3SeekBar = findViewById(R.id.include_filter_setting3_seek_bar);
        mSaveImageProgress = findViewById(R.id.camera_save_progress);
        //endregion

        //region подключение слушателей для view
        mTakePicture.setOnClickListener(view -> onTakePhotoClick());
        mSwitchCamera.setOnClickListener(view -> onSwitchCamera());
        mOnFilterItemClickCallback = (position, isSecondClick) -> {
            if (mCameraRenderer == null) return;
            if (!isSecondClick) mCameraRenderer.changeFilter(position);
            else initFilterSettings();
        };
        toGallery.setOnClickListener(v -> startActivity(GalleryActivity.createIntent(this)));
        //endregion

        //region настройка GLSurfaceView и установка рендера
        mCameraRenderer = new CameraRenderer(null);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8,
                8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLSurfaceView.setOnTouchListener((v, event) -> {
            if (mCameraRenderer.getProgram() != null &&
                    mCameraRenderer.getProgram().isTouchListenerEnable())
                mCameraRenderer.getProgram().setTouchCoordinate(event.getX(), event.getY(),
                        mScreenWidth, mScreenHeight, mCameraType);
            return true;
        });
        //endregion

        //region настройка RecyclerView
        mFilterList.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        ViewTreeObserver observer = mFilterList.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> {
            if (mFilterAdapter == null) {
                mFilterAdapter = new FilterAdapter(mFilterList.getHeight(), mOnFilterItemClickCallback);
                mFilterList.setAdapter(mFilterAdapter);
            }
        });
        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                        true,
                        new RecyclerSectionItemDecoration.SectionCallback() {
                            @Override
                            public boolean isSection(int position) {
                                return position == 1 || position == 9;
                            }

                            @Override
                            public CharSequence getSectionHeader(int position) {
                                if (position == 0) return null;
                                else return position < 9 ? "Коррекция" : "Фильтры";
                            }
                        });
        mFilterList.addItemDecoration(sectionItemDecoration);
        //endregion

        //region начальное состояние для view
        mSwitchCamera.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
        mListLayout.setVisibility(View.INVISIBLE);
        mFilterSettingsLayout.setVisibility(View.GONE);
        mTakePicture.setVisibility(View.GONE);
        mSaveImageProgress.setVisibility(View.GONE);
        //endregion

        if (isSupportsOpenGLES3() && getCameraPermission())
            initCamera();
    }

    private void initFilterSettings() {
        mFilterSettingsLayout.setVisibility(View.VISIBLE);
        boolean isWithFilterEdit = false;

        if (mCameraRenderer.getProgram().isNeedFirstSettingParameter()) {
            isWithFilterEdit = true;
            mFilterSetting1Layout.setVisibility(View.VISIBLE);
            mFilterSetting1SeekBar.setProgress(mCameraRenderer.getProgram().getFirstSettingsValue());
            mFilterSetting1SeekBar.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mCameraRenderer.getProgram().setFirstSettingsValue(progress);
                }
            });
        } else mFilterSetting1Layout.setVisibility(View.GONE);

        if (mCameraRenderer.getProgram().isNeedSecondSettingParameters()) {
            isWithFilterEdit = true;
            mFilterSetting2Layout.setVisibility(View.VISIBLE);
            mFilterSetting2SeekBar.setProgress(mCameraRenderer.getProgram().getSecondSettingsValue());
            mFilterSetting2SeekBar.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mCameraRenderer.getProgram().setSecondSettingsValue(progress);
                }
            });
        } else mFilterSetting2Layout.setVisibility(View.GONE);

        if (mCameraRenderer.getProgram().isNeedThirdSettingParameters()) {
            isWithFilterEdit = true;
            mFilterSetting3Layout.setVisibility(View.VISIBLE);
            mFilterSetting3SeekBar.setProgress(mCameraRenderer.getProgram().getThirdSettingsValue());
            mFilterSetting3SeekBar.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mCameraRenderer.getProgram().setThirdSettingsValue(progress);
                }
            });
        } else mFilterSetting3Layout.setVisibility(View.GONE);

        if (!isWithFilterEdit)
            mFilterSettingsLayout.setVisibility(View.GONE);
    }

    private void onTakePhotoClick() {
        mSaveImageProgress.setVisibility(View.VISIBLE);
        mCameraRenderer.setCallbackTakeBitmap(image -> {
            image = addWaterMark(image);
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, "Camera Effects" + "/" +
                    "IMG " + Calendar.getInstance().getTime().toString() + ".jpg");
            try {
                file.getParentFile().mkdirs();
                OutputStream output = null;
                try {
                    output = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, output);
                } finally {
                    if (null != output) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                MediaScannerConnection.scanFile(CameraActivity.this, new String[]{
                                file.toString()}, null,
                        (s, uri) -> {
                            mSaveImageProgress.setVisibility(View.GONE);
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CameraActivity.this,
                        R.string.save_image_error, Toast.LENGTH_SHORT).show();
            }

        });

        mGLSurfaceView.requestRender();

    }

    private Bitmap addWaterMark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        String gText = getString(R.string.app_name);

        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Typeface regular = Typeface.createFromAsset(getAssets(),
                "SlimTony.otf");

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(regular);
        paint.setColor(getResources().getColor(R.color.colorWhite));
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
        float scale = (float) h / 360;

        paint.setTextSize((int) (16 * scale));
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);

        int x = (w - bounds.width() - 16);
        int y = (h - (int) (bounds.height() * 0.5));

        canvas.drawText(gText, x, y, paint);

        return result;
    }

    private void onSwitchCamera() {
        if (mCameraType == CameraType.FRONT) mCameraType = CameraType.BACK;
        else if (mCameraType == CameraType.BACK) mCameraType = CameraType.FRONT;

        if (mCameraInterface != null) {
            closeCamera();
            mCameraInterface.openCameraByType(mCameraType);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraInterface != null && mCameraType != CameraType.NONE)
            mCameraInterface.openCameraByType(mCameraType);
        if (mGLSurfaceView != null && mCameraRenderer != null)
            mGLSurfaceView.requestRender();
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
                Boolean isGranted = null;
                for (int result : grantResults) {
                    isGranted = result == PackageManager.PERMISSION_GRANTED && isGranted == null
                            || result == PackageManager.PERMISSION_GRANTED && isGranted;
                }
                if (isGranted != null && isGranted) initCamera();
                else showError(R.string.camera_permission_denied);
                break;
        }
    }

    /**
     * Инициализация камеры и её открытие
     */
    private void initCamera() {
        mErrorText.setVisibility(View.GONE);

        mCameraInterface = new CameraModule(this).provideSupportCamera();
        mCameraHandler = new CameraHandler(this, mCameraInterface);
        mCameraRenderer.setCameraHandler(mCameraHandler);
        mGLSurfaceView.requestRender();
        mCameraInterface.setOnFrameAvailableCallback(surfaceTexture -> mGLSurfaceView.requestRender());
        mCameraInterface.setCameraReadyCallback((previewWidth, previewHeight) -> {
            mListLayout.setVisibility(View.VISIBLE);
            mTakePicture.setVisibility(View.VISIBLE);
            mCameraHandler.weakReferenceHandler();
            mGLSurfaceView.onResume();
            mGLSurfaceView.queueEvent(() ->
                    mCameraRenderer.setCameraPreviewSize(previewWidth, previewHeight,
                            mGLSurfaceView.getWidth(),
                            mGLSurfaceView.getHeight()));
        });

        mCameraType = getCameraTypeForOpen(mCameraInterface);
        mCameraInterface.openCameraByType(mCameraType);
        if (mCameraType == CameraType.BACK || mCameraType == CameraType.FRONT)
            mSwitchCamera.setVisibility(View.VISIBLE);
    }

    /**
     * Метод возвращает тип поддерживаемой камеры {@link CameraType}
     *
     * @param cameraInterface модель работы с камерой,
     *                        {@link ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraNew} или
     *                        {@link ua.kh.oleksii.melnykov.cameraeffects.camera.bind.CameraOld}
     * @return возвращает тип поддерживаемой камеры {@link CameraType}, если поддерживается
     * {@link CameraType#FRONT} и {@link CameraType#BACK}, то возвращается {@link CameraType#BACK}
     * для того, чтобы изначально открыть именно эту камеру
     */
    private CameraType getCameraTypeForOpen(CameraInterface cameraInterface) {
        boolean hasFrontCamera = cameraInterface.hasFrontCamera();
        boolean hasBackCamera = cameraInterface.hasBackCamera();

        if (hasFrontCamera && hasBackCamera) return CameraType.FRONT;
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
    public void onBackPressed() {
        if (mFilterSettingsLayout.getVisibility() == View.VISIBLE)
            mFilterSettingsLayout.setVisibility(View.GONE);
        else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    private void closeCamera() {
        if (mCameraInterface != null) mCameraInterface.closeCamera();
        if (mCameraRenderer != null && mGLSurfaceView != null) {
            mGLSurfaceView.queueEvent(() -> mCameraRenderer.notifyPausing());
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraHandler != null) mCameraHandler.invalidateHandler();
    }

}