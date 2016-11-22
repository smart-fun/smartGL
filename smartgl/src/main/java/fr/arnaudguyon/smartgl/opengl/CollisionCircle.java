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

import junit.framework.Assert;

/**
 * Created by Arnaud Guyon on 07/05/2015.
 */
public class CollisionCircle extends Collision {

    //private Sprite mOwner;
    private float mXPercent;
    private float mYPercent;
    private float mRadiusPercent;

    private float[] mGlobalPos = new float[] {0,0,0,1};
    private float mGlobalRadius = 1;

    public CollisionCircle(Sprite owner, float xPercentCenter, float yPercentCenter, float radiusPercent) {
        super(owner);
        //mOwner = owner;
        mXPercent = xPercentCenter;
        mYPercent = yPercentCenter;
        mRadiusPercent = radiusPercent;
    }

    @Override
    public void computePositionAndSize() {
        float rotation = mOwner.getRotation();
        if (rotation == 0) {
            final float scale = mOwner.getScaleX();
            mGlobalPos[0] = mOwner.getPosX() + ((mXPercent - mOwner.getPivotX()) * mOwner.getWidth() * scale);
            mGlobalPos[1] = mOwner.getPosY() + ((mYPercent - mOwner.getPivotY()) * mOwner.getHeight() * scale);
            mGlobalPos[2] = 0;
            mGlobalPos[3] = 1;
        } else {
            mOwner.localToGlobalPosition(mXPercent * mOwner.getWidth(), mYPercent * mOwner.getHeight(), 0, mGlobalPos);
        }
        mGlobalRadius = mRadiusPercent * mOwner.getWidth();
    }

    //@Override
//    float[] getPosition() {
//        return mGlobalPos;
//    }

    //@Override
    float getWidth() {
        return getRadius()*2;
    }

    //@Override
    float getHeight() {
        return getRadius()*2;
    }

    public float getRadius() {
        return mGlobalRadius * mOwner.getScaleX();
    }

    @Override
    public boolean collide(Collision otherCol) {
        if (! (otherCol instanceof CollisionCircle)) {
            return otherCol.collide(this);
        }
        final CollisionCircle other = (CollisionCircle) otherCol;
        final float squaredDistance = getSquaredDistanceToCenter(other);
        final float radiusSum = getRadius() + other.getRadius();
        return (squaredDistance < (radiusSum * radiusSum));
    }

   // @Override
    public boolean isInside(Collision otherCol) {
        if (! (otherCol instanceof CollisionCircle)) {
            Assert.assertTrue(false);   // other kind of collision not handled yet
            return false;
        }
        final CollisionCircle other = (CollisionCircle) otherCol;
        final float squaredDistance = getSquaredDistanceToCenter(other);
        final float radiusDiff = getRadius() - other.getRadius();
        return (squaredDistance < (radiusDiff * radiusDiff));
    }

    //@Override
    public boolean isCenterInside(Collision otherCol) {
        if (! (otherCol instanceof CollisionCircle)) {
            Assert.assertTrue(false);   // other kind of collision not handled yet
            return false;
        }
        final CollisionCircle other = (CollisionCircle) otherCol;
        final float squaredDistance = getSquaredDistanceToCenter(other);
        final float otherRadius = other.getRadius();
        return (squaredDistance < (otherRadius * otherRadius));
    }

    public float getSquaredDistanceToCenter(Collision otherCol) {
        if (otherCol instanceof CollisionCircle) {
            CollisionCircle other = (CollisionCircle) otherCol;
            computePositionAndSize();
            other.computePositionAndSize();
            final float xDiff = (other.getPosX() - mGlobalPos[0]);
            final float yDiff = (other.getPosY() - mGlobalPos[1]);
            final float dist2 = (xDiff * xDiff) + (yDiff * yDiff);
            return dist2;
        } else {
            Assert.assertTrue(false);   // not handlded yet
            return 0;
        }
    }

    //@Override
    public float getPosX() {
        return mGlobalPos[0];
    }
    //@Override
    public float getPosY() {
        return mGlobalPos[1];
    }

    // TODO: if collide, can move hero to the same direction of the collision (if is sticked to the obstacle)
    // TODO: when collide, set speed to 0 or go nowhere when collision stops

}
