package fr.arnaudguyon.smartgl.math;

import android.util.Log;

public class Circle {

	private Point2D mCenter;
	private float mRadius;
	
	public Point2D getCenter() {
		return mCenter;
	}

	public float getRadius() {
		return mRadius;
	}
	
	public Circle(Point2D center, float radius) {
		mCenter = new Point2D(center);
		mRadius = radius;
	}
	
	public Circle(Circle other) {
		mCenter = new Point2D(other.mCenter);
		mRadius = other.mRadius;
	}
	
	public void scale(float factor) {
		mRadius *= factor;
	}
	
	public void move(float dx, float dy) {
		mCenter.mX += dx;
		mCenter.mY += dy;
	}
	
	public void setPos(float x, float y) {
		mCenter.mX = x;
		mCenter.mY = y;
	}

	public float collideWithVectorSlideH(Vector2D seg) {

		// too much on the left
		float minX = seg.getMinX();
		float maxCircle = mCenter.mX + mRadius;
		if (maxCircle <= minX) {
			return 0;
		}

		// too much on the right
		float maxX = seg.getMaxX();
		float minCircle = mCenter.mX - mRadius;
		if (minCircle >= maxX) {
			return 0;
		}

		// before segment [AB]
		Vector2D CA = new Vector2D(mCenter, seg.getStart());
		if ((CA.getSize() >= mRadius) && (CA.dot(seg) >= 0)) {
			return 0;
		}
		// after segment [AB]
		Vector2D CB = new Vector2D(mCenter, seg.getStop());
		if ((CB.getSize() >= mRadius) && (CB.dot(seg) <= 0)) {
			return 0;
		}

		// too far from line
		Vector2D normal = seg.normale();
		float dist = CA.dot(normal);
		float absDist = Math.abs(dist);
		if (absDist >= mRadius) {
			return 0;
		}

		float slideX = 0;
		if (seg.getDX() == 0) {	// Vertical segment
			float sign = (seg.getDY() < 0) ? 1 : -1;
			slideX = sign * (mRadius - absDist);
		} else {
			float D = mRadius - absDist;
			float sign = (dist > 0) ? 1 : -1;
			slideX = sign * D * seg.getDX() / seg.getDY();
		}

		mCenter.move(slideX, 0);

		return slideX;
	}

	public void log(String name) {
		Log.i("Pingoo", "Circle " + name + " " + mCenter.mX + ";" + mCenter.mY + " R=" + mRadius);
	}

}
