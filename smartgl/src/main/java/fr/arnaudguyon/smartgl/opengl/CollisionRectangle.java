package fr.arnaudguyon.smartgl.opengl;

import android.util.Log;

import junit.framework.Assert;

/**
 * Created by Arnaud Guyon on 09/05/2015.
 */
public class CollisionRectangle extends Collision {

    private static final String TAG = "CollisionRectangle";

    //private Sprite mOwner;
    float mLeftPercent;
    float mTopPercent;
    float mRightPercent;
    float mBottomPercent;

    float mLeftPos;
    float mTopPos;
    float mRightPos;
    float mBottomPos;

    public CollisionRectangle(Sprite owner, float leftPercent, float topPercent, float rightPercent, float bottomPercent) {
        super(owner);
        //mOwner = owner;
        mLeftPercent = leftPercent;
        mTopPercent = topPercent;
        mRightPercent = rightPercent;
        mBottomPercent = bottomPercent;
    }

    private static boolean sWarned = false;
    @Override
    public void computePositionAndSize() {
        //Assert.assertTrue(mOwner.getRotation() == 0);   // rotation not handled
        if (mOwner.getRotation() != 0) {
            if (!sWarned) {
                Log.e(TAG, "CollisionRectangle cannot be rotated yet! " + mOwner);
                sWarned = true;
            }
        }

        mLeftPos = mOwner.getPosX() + ((mLeftPercent - mOwner.getPivotX()) * mOwner.getWidth());
        mTopPos = mOwner.getPosY() + ((mTopPercent - mOwner.getPivotY()) * mOwner.getHeight());

        mRightPos = mOwner.getPosX() + ((mRightPercent - mOwner.getPivotX()) * mOwner.getWidth());
        mBottomPos = mOwner.getPosY() + ((mBottomPercent - mOwner.getPivotY()) * mOwner.getHeight());
    }

    @Override
    public boolean collide(Collision otherCol) {
        if (otherCol instanceof CollisionCircle) {
            return collideWithCircle((CollisionCircle) otherCol);
        } else {
            Assert.assertTrue(false);   // not handled yet
            return false;
        }
    }

    public boolean circleIsInside(CollisionCircle circle) {
        computePositionAndSize();
        circle.computePositionAndSize();
        float circlePosX = circle.getPosX();
        float circlePosY = circle.getPosY();
        float radius = circle.getRadius();

        if (circlePosY - radius > mTopPos) {
            if (circlePosY + radius < mBottomPos) {
                if (circlePosX - radius > mLeftPos) {
                    if (circlePosX + radius < mRightPos) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean circleIsCenterInside(CollisionCircle circle) {
        computePositionAndSize();
        circle.computePositionAndSize();
        float circlePosX = circle.getPosX();
        float circlePosY = circle.getPosY();

        if (circlePosY > mTopPos) {
            if (circlePosY < mBottomPos) {
                if (circlePosX > mLeftPos) {
                    if (circlePosX < mRightPos) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean collideWithCircle(CollisionCircle circle) {
        if (circleIsInside(circle)) { // already computes position & size for both
            return true;
        } else {
//            computePositionAndSize();
//            circle.computePositionAndSize();
            float circlePosX = circle.getPosX();
            float circlePosY = circle.getPosY();
            float radius = circle.getRadius();

            if (circlePosY - radius > mBottomPos) {
                return false;
            }
            if (circlePosY + radius < mTopPos) {
                return false;
            }
            if (circlePosX - radius > mRightPos) {
                return false;
            }
            if (circlePosX + radius < mLeftPos) {
                return false;
            }
            // "squared circle" collides square
            // TODO: how to be more precise in angles ?
            return true;
        }
    }

    public float getLeft() {
        return mLeftPos;
    }
    public float getTop() {
        return mTopPos;
    }
    public float getRight() {
        return mRightPos;
    }
    public float getBottom() {
        return mBottomPos;
    }

    public void flipH() {
        float left = mLeftPercent;
        mLeftPercent = 1 - mRightPercent;
        mRightPercent = 1 - left;
    }
}
