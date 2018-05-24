package ua.kh.oleksii.melnykov.cameraeffects.utils;

import android.widget.SeekBar;

/**
 * <p> Created by Melnykov Oleksii on 5/22/2018. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.utils </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public abstract class SeekBarProgressChangeListener implements SeekBar.OnSeekBarChangeListener {

    @Override
    public abstract void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
