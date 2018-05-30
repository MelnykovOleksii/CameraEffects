package ua.kh.oleksii.melnykov.cameraeffects.gallery.bind;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.InputStream;

/**
 * <p> Created by Melnykov Oleksii on 24-May-18. <br>
 * Copyright (c) 2018 LineUp. <br>
 * Project: CameraEffects, ua.kh.oleksii.melnykov.cameraeffects.gallery.bind </p>
 *
 * @author Melnykov Oleksii
 * @version 1.0
 */
public class LoadImageUriTask extends AsyncTask<Void, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    private ImageRenderer mRenderer;
    private Callback mCallback;
    private int mOutputWidth;
    private int mOutputHeight;
    private Uri mUri;

    public LoadImageUriTask(Uri uri, Context context, ImageRenderer renderer, Callback callback) {
        mCallback = callback;
        mUri = uri;
        mContext = context;
        mRenderer = renderer;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            synchronized (mRenderer.mSurfaceChangedWaiter) {
                mRenderer.mSurfaceChangedWaiter.wait(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mOutputWidth = mRenderer.getFrameWidth();
        mOutputHeight = mRenderer.getFrameHeight();
        return loadResizedImage();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mCallback.onBitmapReady(bitmap);
    }

    private Bitmap decode(BitmapFactory.Options options) {
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(mUri);
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap loadResizedImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decode(options);
        int scale = 1;
        while (checkSize(options.outWidth / scale > mOutputWidth,
                options.outHeight / scale > mOutputHeight)) {
            scale++;
        }

        scale--;
        if (scale < 1) {
            scale = 1;
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = decode(options);
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    private boolean checkSize(boolean widthBigger, boolean heightBigger) {
        return widthBigger || heightBigger;
    }

    public interface Callback {
        void onBitmapReady(Bitmap bitmap);
    }

}

