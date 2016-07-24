package fr.arnaudguyon.smartgl.math;

import android.util.Log;

public class Point2D {
	public float mX;
	public float mY;
	public Point2D(float x, float y) {
		mX = x;
		mY = y;
	}
	public Point2D(Point2D other) {
		mX = other.mX;
		mY = other.mY;
	}
	void scale(float factor) {
		mX *= factor;
		mY *= factor;
	}
	void move(Vector2D vector) {
		mX += vector.getDX();
		mY += vector.getDY();
	}
	void move(float dx, float dy) {
		mX += dx;
		mY += dy;
	}
	void log(String name) {
		Log.i("Pingoo", "Point2D " + name + " " + mX + ";" + mY + " ");
	}
}
