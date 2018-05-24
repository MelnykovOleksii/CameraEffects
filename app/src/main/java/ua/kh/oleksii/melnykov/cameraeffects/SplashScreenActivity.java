package ua.kh.oleksii.melnykov.cameraeffects;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ua.kh.oleksii.melnykov.cameraeffects.camera.CameraActivity;

/**
 * <p> Created by Melnykov Oleksii on 17.05.2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects </p>
 * Сплэшскрин - экран, который показывается первый пользователю, служит для превью логотипа.
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // запуск CameraActivity
        startActivity(CameraActivity.createIntent(this));

        finish();
    }

}
