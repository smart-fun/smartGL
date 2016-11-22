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
package fr.arnaudguyon.smartgl.math;

import android.util.Log;

public class Vector2D {

	private Point2D mStart, mStop;
	private float mDX, mDY, mSize;
	
	public Vector2D(Vector2D other) {
		this(new Point2D(other.mStart), new Point2D(other.mStop));
	}

	public Vector2D(float startX, float startY, float stopX, float stopY) {
		mStart = new Point2D(startX, startY);
		mStop = new Point2D(stopX, stopY);
		initDeltaAndSize();
	}

	public Vector2D(Point2D start, Point2D stop) {
		mStart = new Point2D(start);
		mStop = new Point2D(stop);
		initDeltaAndSize();
	}
	
	private void initDeltaAndSize() {
		mDX = mStop.mX - mStart.mX;
		mDY = mStop.mY - mStart.mY;
		if ((mDX != 0) || (mDY != 0)) {
			mSize = (float) Math.sqrt((mDX*mDX) + (mDY*mDY));
		} else {
			mSize = 0;
		}
		
	}

	public Point2D getStart() {
		return mStart;
	}
	
	public Point2D getStop() {
		return mStop;
	}
	
	public float getXStart() {
		return mStart.mX;
	}

	public float getYStart() {
		return mStart.mY;
	}

	public float getXStop() {
		return mStop.mX;
	}

	public float getYStop() {
		return mStop.mY;
	}
	
	public float getSize() {
		return mSize;
	}
	
	public float getDX() {
		return mDX;
	}
	
	public float getDY() {
		return mDY;
	}
	
	public void move(float dx, float dy) {
		mStart.move(dx, dy);
		mStop.move(dx, dy);
	}
	
	public float getMinX() {
		return (mStart.mX <= mStop.mX) ? mStart.mX : mStop.mX;
	}
	public float getMaxX() {
		return (mStart.mX >= mStop.mX) ? mStart.mX : mStop.mX;
	}
	
	void normalize() {
		if (mSize > 0) {
			mDX /= mSize;
			mDY /= mSize;
			mStop.mX = mStart.mX + mDX;
			mStop.mY = mStart.mY + mDY;
		} else {
			mStop.mX = mStart.mX + 1;
			mStop.mY = mStart.mY;
		}
		mSize = 1;
	}
	
	Vector2D invert() {
		float x1 = mStart.mX * -1;
		float y1 = mStart.mY * -1;
		float x2 = mStop.mX * -1;
		float y2 = mStop.mY * -1;
		Vector2D result = new Vector2D(x1, y1, x2, y2);
		return result;
	}
	
	Vector2D normale() {
		Point2D start = new Point2D(0,0);
		Point2D stop = new Point2D(-mDY, mDX);
		Vector2D result = new Vector2D(start, stop);
		result.normalize();
		return result;
	}
	
	void scale(float factor) {
		mStart.scale(factor);
		mStop.scale(factor);
		mDX *= factor;
		mDY *= factor;
		mSize *= factor;
	}
	
	float dot(Vector2D other) {
		return (mDX * other.mDX) + (mDY * other.mDY);
	}

	public float computeAngle() {
		Vector2D other = new Vector2D(this);
		other.normalize();
		float cos = other.mDX;
		float sin = other.mDY;
		if (cos != 0) {
			float acos = (float) Math.acos(cos);
			return (sin > 0) ? acos : -acos;
		} else {
			float asin = (float) Math.asin(sin);
			return  (cos > 0) ? asin : -asin;
		}
	}

	public void log(String name) {
		Log.i("Pingoo", "Vector2D " + name + " (" + mDX + ":" + mDY + ") = "+ mStart.mX + ";" + mStart.mY + " -> " + mStop.mX + ";" + mStop.mY + " (size=" + mSize + ")");
	}

}
