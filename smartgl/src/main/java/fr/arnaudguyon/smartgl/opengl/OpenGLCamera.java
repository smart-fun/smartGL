package fr.arnaudguyon.smartgl.opengl;

/**
 * Created by arnaud on 20/11/2016.
 */

public class OpenGLCamera {

    private boolean mDirty = true;

    private float mFOV = 56;
    private float mNear = 0.01f;
    private float mFar = 10000;

    private float mPosX;
    private float mPosY;
    private float mPosZ;

    private float mRotX;
    private float mRotY;
    private float mRotZ;

    public void setPosition(float x, float y, float z) {
        mPosX = x;
        mPosY = y;
        mPosZ = z;
        mDirty = true;
    }
    public void setRotation(float x, float y, float z) {
        mRotX = x;
        mRotY = y;
        mRotZ = z;
        mDirty = true;
    }

    /**
     * set the Field Of View (FOV)
     * @param fov in degrees
     */
    public void setFOV(float fov) {
        mFOV = fov;
        mDirty = true;
    }
    public float getFOV() {
        return mFOV;
    }

    void setDirty(boolean dirty) {
        mDirty = dirty;
    }
    public boolean isDirty() {
        return mDirty;
    }

    public void setNear(float near) {
        mNear = near;
        mDirty = true;
    }
    public float getNear() {
        return mNear;
    }
    public void setFar(float far) {
        mFar = far;
        mDirty = true;
    }
    public float getFar() {
        return mFar;
    }


    public float getPosX() {
        return mPosX;
    }

    public float getPosY() {
        return mPosY;
    }

    public float getPosZ() {
        return mPosZ;
    }

    public float getRotX() {
        return mRotX;
    }

    public float getRotY() {
        return mRotY;
    }

    public float getRotZ() {
        return mRotZ;
    }


}
