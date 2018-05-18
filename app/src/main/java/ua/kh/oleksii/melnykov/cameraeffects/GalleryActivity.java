package ua.kh.oleksii.melnykov.cameraeffects;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getCameraPermission()) init();
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

    private void init() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

    }

}
