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

import android.opengl.Matrix;

public class Object3D extends RenderObject {

	private float mPosX, mPosY, mPosZ;
	private float mRotX, mRotY, mRotZ;
	private float mScaleX, mScaleY, mScaleZ;

	public Object3D() {
		super(true);
		mPosX = mPosY = mPosZ = 0;
		mScaleX = mScaleY = mScaleZ = 1;
		mRotX = mRotY = mRotZ = 0;
	}

	final public void setPos(float x, float y, float z) {
		mPosX = x;
		mPosY = y;
		mPosZ = z;
        invalidMatrix();
	}

	final public float getPosX() {
		return mPosX;
	}

	final public float getPosY() {
		return mPosY;
	}

	final public float getPosZ() {
		return mPosZ;
	}

	final public void setScale(float x, float y, float z) {
		mScaleX = x;
		mScaleY = y;
		mScaleZ = z;
        invalidMatrix();
	}

	final public float getScaleX() {
		return mScaleX;
	}

	final public float getScaleY() {
		return mScaleY;
	}

	final public float getScaleZ() {
		return mScaleZ;
	}

	final public void setRotation(float x, float y, float z) {
		mRotX = (x % 360f);
		mRotY = (y % 360f);
		mRotZ = (z % 360f);
        invalidMatrix();
	}

	final public float getRotX() {
		return mRotX;
	}

	final public float getRotY() {
		return mRotY;
	}

	final public float getRotZ() {
		return mRotZ;
	}
	
	final public void addRotX(float dx) {
		mRotX += dx;
        invalidMatrix();
	}
	
	final public void addRotY(float dy) {
		mRotY += dy;
        invalidMatrix();
	}

	final public void addRotZ(float dz) {
		mRotZ += dz;
        invalidMatrix();
	}

	@Override
	final public void computeMatrix(float[] matrix) {
		Matrix.setIdentityM(matrix, 0);
		Matrix.translateM(matrix, 0, mPosX, mPosY, mPosZ);

		if (mRotX != 0) {
			Matrix.rotateM(matrix, 0, mRotX, 1, 0, 0);
		}

		if (mRotY != 0) {
			Matrix.rotateM(matrix, 0, mRotY, 0, 1, 0);
		}

		if (mRotZ != 0) {
			Matrix.rotateM(matrix, 0, mRotZ, 0, 0, 1);
		}

		if ((mScaleX != 1) || (mScaleY != 1) || (mScaleZ != 1)) {
			Matrix.scaleM(matrix, 0, mScaleX, mScaleY, mScaleZ);
		}
	}

	@Override
	public void releaseResources() {
		super.releaseResources();
	}

}
