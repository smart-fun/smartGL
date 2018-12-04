/*
    Copyright 2016 Arnaud Guyon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package fr.arnaudguyon.smartgl.opengl;

import android.util.Log;

import fr.arnaudguyon.smartgl.tools.Assert;

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
            float circlePosX = circle.getPosX();
            float circlePosY = circle.getPosY();
            float radius = circle.getRadius();

            // Vertical collision in "left to right" segment
            if (circlePosX >= mLeftPos && circlePosX <= mRightPos) {
                if ((circlePosY + radius >= getTop()) && (circlePosY - radius <= getBottom())) {
                    return  true;
                }
            }
            // Horizontal collision in "top to bottom" segment
            if (circlePosY >= mBottomPos && circlePosY <= getTop()) {
                if ((circlePosX + radius >= mLeftPos) && (circlePosX - radius <= mRightPos)) {
                    return  true;
                }
            }
            // Corners
            float leftDist2 = (mLeftPos - circlePosX) * (mLeftPos - circlePosX);
            float topDist2 = (mTopPos - circlePosY) * (mTopPos - circlePosY);
            float radius2 = radius * radius;
            // Top-Left corner
            if (leftDist2 + topDist2 < radius2) {
                return true;
            }
            float rightDist2 = (mRightPos - circlePosX) * (mRightPos - circlePosX);
            // Top-Right corner
            if (rightDist2 + topDist2 < radius2) {
                return true;
            }
            float bottomDist2 = (mBottomPos - circlePosY) * (mBottomPos - circlePosY);
            // Bottom-Left corner
            if (leftDist2 + bottomDist2 < radius2) {
                return true;
            }
            // Bottom-Right corner
            if (rightDist2 + bottomDist2 < radius2) {
                return true;
            }
            return false;
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
