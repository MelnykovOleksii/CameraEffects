package ua.kh.oleksii.melnykov.cameraeffects.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ua.kh.oleksii.melnykov.cameraeffects.R;

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

    private TextView mErrorText;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, GalleryActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mErrorText = findViewById(R.id.gallery_error_text);

        if (isSupportsOpenGLES3() && getCameraPermission()) initGallery();
    }

    private boolean isSupportsOpenGLES3() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean isSupported = configurationInfo.reqGlEsVersion >= 0x00030000;

        if (!isSupported) showError(R.string.opengles3_not_supported);
        return isSupported;
    }

    private boolean getCameraPermission() {
        boolean isDenied = PermissionChecker.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;

        if (isDenied) ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION);

        return !isDenied;
    }

    private void initGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_IMAGE_FROM_GALLERY);

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
                if (grantResults.length != 1 || grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) initGallery();
                else showError(R.string.gallery_permission_denied);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Toast.makeText(this, "Выбрано: " + uri.getPath(), Toast.LENGTH_SHORT).show();
        } else {
            onBackPressed();
        }
    }

    private void showError(@StringRes int resId) {
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(resId);
    }

}
