package ua.kh.oleksii.melnykov.cameraeffects.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import ua.kh.oleksii.melnykov.cameraeffects.filters.listAdapter.FilterAdapter;
import ua.kh.oleksii.melnykov.cameraeffects.gallery.bind.ImageRenderer;
import ua.kh.oleksii.melnykov.cameraeffects.gallery.bind.LoadImageUriTask;
import ua.kh.oleksii.melnykov.cameraeffects.utils.RecyclerSectionItemDecoration;
import ua.kh.oleksii.melnykov.cameraeffects.utils.SeekBarProgressChangeListener;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class GalleryActivity extends AppCompatActivity {

    private static final int EXTERNAL_STORAGE_PERMISSION = 285;
    public static final int GET_IMAGE_FROM_GALLERY = 369;
    public static final String KEY_SAVE_INSTANSE_IMAGE_URI = "GalleryActivity.KEY_SAVE_INSTANSE_IMAGE_URI";

    private int mScreenWidth;
    private int mScreenHeight;

    //region view поля
    private TextView mErrorText;
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
    private ProgressBar mSaveImageProgress;
    //endregion

    private FilterAdapter mFilterAdapter;
    private FilterAdapter.OnItemClickCallback mOnFilterItemClickCallback;
    private ImageRenderer mRenderer;
    private ImageButton mSaveImage;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, GalleryActivity.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        //region инициализация view
        mSaveImage = findViewById(R.id.gallery_save_image);
        mErrorText = findViewById(R.id.gallery_error_text);
        mFilterList = findViewById(R.id.include_filters_list);
        mGLSurfaceView = findViewById(R.id.gallery_surface_view);
        mListLayout = findViewById(R.id.include_filters_list_layout);
        ImageView toCamera = findViewById(R.id.gallery_back_to_camera);
        mFilterSettingsLayout = findViewById(R.id.include_filter_settings_layout);
        mFilterSetting1Layout = findViewById(R.id.include_filter_setting1);
        mFilterSetting2Layout = findViewById(R.id.include_filter_setting2);
        mFilterSetting3Layout = findViewById(R.id.include_filter_setting3);
        mFilterSetting1SeekBar = findViewById(R.id.include_filter_setting1_seek_bar);
        mFilterSetting2SeekBar = findViewById(R.id.include_filter_setting2_seek_bar);
        mFilterSetting3SeekBar = findViewById(R.id.include_filter_setting3_seek_bar);
        mSaveImageProgress = findViewById(R.id.gallery_save_progress);
        //endregion

        //region подключение слушателей для view
        mSaveImage.setOnClickListener(view -> onSaveImage());
        mOnFilterItemClickCallback = (position, isSecondClick) -> {
            if (!isSecondClick) {
                mRenderer.changeFilter(position);
                mGLSurfaceView.requestRender();
            } else initFilterSettings();
        };
        toCamera.setOnClickListener(v -> onBackPressed());
        mGLSurfaceView.setOnTouchListener((v, event) -> {
            if (mRenderer.getProgram() != null &&
                    mRenderer.getProgram().isTouchListenerEnable()) {
                mRenderer.getProgram().setTouchCoordinate(event.getX(), event.getY(),
                        mScreenWidth, mScreenHeight, null);
                mGLSurfaceView.requestRender();
            }
            return true;
        });
        //endregion

        mRenderer = new ImageRenderer();
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGLSurfaceView.setRenderer(mRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
        mErrorText.setVisibility(View.GONE);
        mListLayout.setVisibility(View.INVISIBLE);
        mFilterSettingsLayout.setVisibility(View.GONE);
        mSaveImage.setVisibility(View.GONE);
        mSaveImageProgress.setVisibility(View.GONE);
        //endregion

        if (isSupportsOpenGLES3() && getExternalReadPermission())
            getImageFromUserGallery();

    }

    private void initFilterSettings() {
        mFilterSettingsLayout.setVisibility(View.VISIBLE);
        boolean isWithFilterEdit = false;

        if (mRenderer.getProgram().isNeedFirstSettingParameter()) {
            isWithFilterEdit = true;
            mFilterSetting1Layout.setVisibility(View.VISIBLE);
            mFilterSetting1SeekBar.setProgress(mRenderer.getProgram().getFirstSettingsValue());
            mFilterSetting1SeekBar.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mRenderer.getProgram().setFirstSettingsValue(progress);
                    mGLSurfaceView.requestRender();
                }
            });
        } else mFilterSetting1Layout.setVisibility(View.GONE);

        if (mRenderer.getProgram().isNeedSecondSettingParameters()) {
            isWithFilterEdit = true;
            mFilterSetting2Layout.setVisibility(View.VISIBLE);
            mFilterSetting2SeekBar.setProgress(mRenderer.getProgram().getSecondSettingsValue());
            mFilterSetting2SeekBar.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mRenderer.getProgram().setSecondSettingsValue(progress);
                    mGLSurfaceView.requestRender();
                }
            });
        } else mFilterSetting2Layout.setVisibility(View.GONE);

        if (mRenderer.getProgram().isNeedThirdSettingParameters()) {
            isWithFilterEdit = true;
            mFilterSetting3Layout.setVisibility(View.VISIBLE);
            mFilterSetting3SeekBar.setProgress(mRenderer.getProgram().getThirdSettingsValue());
            mFilterSetting3SeekBar.setOnSeekBarChangeListener(new SeekBarProgressChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mRenderer.getProgram().setThirdSettingsValue(progress);
                    mGLSurfaceView.requestRender();
                }
            });
        } else mFilterSetting3Layout.setVisibility(View.GONE);

        if (!isWithFilterEdit)
            mFilterSettingsLayout.setVisibility(View.GONE);
    }

    private void onSaveImage() {
        mSaveImageProgress.setVisibility(View.VISIBLE);
        mRenderer.setCallbackTakeBitmap(image -> {
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
                MediaScannerConnection.scanFile(GalleryActivity.this, new String[]{
                                file.toString()}, null,
                        (s, uri) -> {
                            mSaveImageProgress.setVisibility(View.GONE);
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(GalleryActivity.this,
                        R.string.save_image_error, Toast.LENGTH_SHORT).show();
            }

        });

        mGLSurfaceView.requestRender();
    }

    private boolean isSupportsOpenGLES3() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean isSupported = configurationInfo.reqGlEsVersion >= 0x00030000;

        if (!isSupported) showError(R.string.opengles3_not_supported);
        return isSupported;
    }

    private boolean getExternalReadPermission() {
        boolean isDenied = PermissionChecker.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;

        if (isDenied) ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION);

        return !isDenied;
    }

    private void getImageFromUserGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_IMAGE_FROM_GALLERY);
    }

    private void initGallery(Uri imageUri) {
        mErrorText.setVisibility(View.GONE);
        mListLayout.setVisibility(View.VISIBLE);
        mSaveImage.setVisibility(View.VISIBLE);

        new LoadImageUriTask(imageUri, this, mRenderer,
                bitmap -> {
                    mRenderer.setImageBitmap(bitmap);
                    mGLSurfaceView.requestRender();
                }).execute();

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

            case EXTERNAL_STORAGE_PERMISSION:
                // если пользователем подтверждено рарешение на использование камеры,
                // то выполняем инициализацию камеры
                Boolean isGranted = null;
                for (int result : grantResults) {
                    isGranted = result == PackageManager.PERMISSION_GRANTED && isGranted == null
                            || result == PackageManager.PERMISSION_GRANTED && isGranted;
                }
                if (isGranted != null && isGranted) getImageFromUserGallery();
                else showError(R.string.gallery_permission_denied);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            initGallery(data.getData());
        } else {
            onBackPressed();
        }
    }

    private void showError(@StringRes int resId) {
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(resId);
    }

    @Override
    public void onBackPressed() {
        if (mFilterSettingsLayout.getVisibility() == View.VISIBLE)
            mFilterSettingsLayout.setVisibility(View.GONE);
        else super.onBackPressed();
    }

}