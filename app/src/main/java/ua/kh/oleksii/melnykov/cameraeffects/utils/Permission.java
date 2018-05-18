package ua.kh.oleksii.melnykov.cameraeffects.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;


public class Permission {
    private static final String TAG = "CameraPermission";

    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private Activity mActivity;

    public Permission(Activity pActivity) {
        this.mActivity = pActivity;
    }

    public void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.CAMERA)) {

            Snackbar.make(mActivity.findViewById(android.R.id.content), "permission CAMERA",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    public void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Snackbar.make(mActivity.findViewById(android.R.id.content),
                    "permission READ_EXTERNAL_STORAGE",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void requestWriteExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Snackbar.make(mActivity.findViewById(android.R.id.content),
                    "permission WRITE_EXTERNAL_STORAGE",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public boolean onRequestPermissionsResult(int[] pGrantResults) {
        return pGrantResults.length == 1 && pGrantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
