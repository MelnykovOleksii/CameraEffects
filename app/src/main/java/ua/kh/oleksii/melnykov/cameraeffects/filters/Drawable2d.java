package ua.kh.oleksii.melnykov.cameraeffects.filters;

import java.nio.FloatBuffer;

class Drawable2d {
    private static final int SIZEOF_FLOAT = 4;

    private FloatBuffer mVertexArray;

    private FloatBuffer mTexCoordArray = GlUtil.createFloatBuffer(new float[]{
            0.0f, 0.0f,     // 0 bottom left
            1.0f, 0.0f,     // 1 bottom right
            0.0f, 1.0f,     // 2 top left
            1.0f, 1.0f      // 3 top right
    });

    private int mVertexCount;
    private int mCoordsPerVertex;
    private int mVertexStride;
    private int mTexCoordStride;
    private float ratio;


    Drawable2d() {
        ratio = 1f;
        mCoordsPerVertex = 2;
        mVertexCount = 4;
        initVertexArray();
        mVertexStride = mCoordsPerVertex * SIZEOF_FLOAT;


        mTexCoordStride = 2 * SIZEOF_FLOAT;
    }

    FloatBuffer getVertexArray() {
        return mVertexArray;
    }

    FloatBuffer getTexCoordArray() {
        return mTexCoordArray;
    }

    int getVertexCount() {
        return mVertexCount;
    }

    int getVertexStride() {
        return mVertexStride;
    }

    int getTexCoordStride() {
        return mTexCoordStride;
    }

    int getCoordsPerVertex() {
        return mCoordsPerVertex;
    }

    void setRatio(float pRatio) {
        ratio = pRatio;
        initVertexArray();
    }

    private void initVertexArray() {
        mVertexArray = GlUtil.createFloatBuffer(new float[]{
                -1.0f, -ratio,   // 0 bottom left
                1.0f, -ratio,   // 1 bottom right
                -1.0f, ratio,   // 2 top left
                1.0f, ratio,   // 3 top right
        });
    }
}
