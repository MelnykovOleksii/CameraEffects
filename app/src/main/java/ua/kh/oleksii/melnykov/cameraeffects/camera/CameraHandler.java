package ua.kh.oleksii.melnykov.cameraeffects.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class CameraHandler extends Handler {

    static final int MSG_SET_SURFACE_TEXTURE = 0;
    private WeakReference<Activity> mWeakActivity;
    private CameraInterface mCameraInterface;
    private Activity mContext;
    private Boolean isWeakReferenceHandler;

    public CameraHandler(Activity context, CameraInterface cameraInterface) {
        mContext = context;
        mCameraInterface = cameraInterface;
        isWeakReferenceHandler = false;
    }

    public void invalidateHandler() {
        mWeakActivity.clear();
    }

    public void weakReferenceHandler() {
        isWeakReferenceHandler = true;
        mWeakActivity = new WeakReference<>(mContext);
    }

    @Override
    public void handleMessage(Message inputMessage) {
        int what = inputMessage.what;
        if (isWeakReferenceHandler) {

            Activity activity = mWeakActivity.get();
            if (activity == null) {
                return;
            }

            switch (what) {
                case MSG_SET_SURFACE_TEXTURE:
                    mCameraInterface.handleSetSurfaceTexture((SurfaceTexture) inputMessage.obj);
                    break;
                default:
                    throw new RuntimeException("unknown msg " + what);
            }
        }
    }
}