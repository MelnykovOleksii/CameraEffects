package ua.kh.oleksii.melnykov.cameraeffects.filters;


public class FullFrameRect {
    private final Drawable2d mRectDrawable = new Drawable2d();
    private DistortionFilterProgram mProgram;

    public FullFrameRect(DistortionFilterProgram program) {
        mProgram = program;
    }

    public void release() {
        if (mProgram != null) {
            mProgram = null;
        }
    }

    public DistortionFilterProgram getProgram() {
        return mProgram;
    }

    public void changeProgram(DistortionFilterProgram program) {
        mProgram.release();
        mProgram = program;
    }

    public int createTextureObject() {
        return mProgram.createTextureObject();
    }

    public void drawFrame(int textureId, float[] texMatrix) {
        mProgram.draw(
                mRectDrawable.getVertexArray(),
                mRectDrawable.getVertexCount(),
                mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(),
                texMatrix,
                mRectDrawable.getTexCoordArray(),
                textureId,
                mRectDrawable.getTexCoordStride());
    }

    public void setRatio(float ratio) {
        mRectDrawable.setRatio(ratio);
    }
}
