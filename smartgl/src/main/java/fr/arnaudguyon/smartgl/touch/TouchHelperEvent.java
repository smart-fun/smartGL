package fr.arnaudguyon.smartgl.touch;

import android.os.SystemClock;

public class TouchHelperEvent {

	public enum TouchEventType {
		SINGLETOUCH, SINGLEMOVE, SINGLEUNTOUCH, MULTITOUCH, MULTIMOVE, MULTIUNTOUCH, TAPPING, LONGPRESS, FLING
	}

    private TouchEventType mType;
	private int mNbFingers;
	private long mTime;
	private float mRefPosX[], mRefPosY[], mPosX[], mPosY[], mVelocityY[], mVelocityX[];
	private int mFinger[];
	private int mNbTaps;

	public TouchEventType getType() {
		return mType;
	}

	TouchHelperEvent(int nbFingers, TouchEventType type) {
		mNbFingers = nbFingers;
		mType = type;
		mRefPosX = new float[nbFingers];
		mRefPosY = new float[nbFingers];
		mPosX = new float[nbFingers];
		mPosY = new float[nbFingers];
		mVelocityX = new float[nbFingers];
		mVelocityY = new float[nbFingers];
		mFinger = new int[nbFingers];
		mTime = SystemClock.uptimeMillis();
		mNbTaps = 0;
	}

	TouchHelperEvent(TouchEventType type, float xRef, float yRef, float newX, float newY, int finger) {
		this(1, type);
		mRefPosX[0] = xRef;
		mRefPosY[0] = yRef;
		mPosX[0] = newX;
		mPosY[0] = newY;
		mFinger[0] = finger;
	}

	TouchHelperEvent(TouchEventType type, float xRef, float yRef, float newX, float newY, float veloX, float veloY, int finger) {
		this(1, type);
		mRefPosX[0] = xRef;
		mRefPosY[0] = yRef;
		mPosX[0] = newX;
		mPosY[0] = newY;
		mVelocityX[0] = veloX;
		mVelocityY[0] = veloY;
		mFinger[0] = finger;
	}

	TouchHelperEvent(int finger, float x, float y, int nbTaps) {
		this(1, TouchEventType.TAPPING);
		mRefPosX[0] = mPosX[0] = x;
		mRefPosY[0] = mPosY[0] = y;
		mFinger[0] = finger;
		mNbTaps = nbTaps;
	}

	void setValues(int index, int finger, float xRef, float yRef, float newX, float newY) {
		mRefPosX[index] = xRef;
		mRefPosY[index] = yRef;
		mPosX[index] = newX;
		mPosY[index] = newY;
		mFinger[index] = finger;
	}

	public int getNbFingers() {
		return mNbFingers;
	}

	//	public int getFinger(int index) {
	//		return mFinger[index];
	//	}

	public long getTime() {
		return mTime;
	}

	public float getX(int index) {
		return mPosX[index];
	}

	public float getY(int index) {
		return mPosY[index];
	}

	public float getRefX(int index) {
		return mRefPosX[index];
	}

	public float getRefY(int index) {
		return mRefPosY[index];
	}

	public float getVelocityX(int index) {
		return mVelocityX[index];
	}

	public float getVelocityY(int index) {
		return mVelocityY[index];
	}

	public int getNbTaps() {
		return mNbTaps;
	}

	//	public float getDeltaMoveX(int index) {
	//		return mPosX[index] - mRefPosX[index];
	//	}

	//	public float getDeltaMoveY(int index) {
	//		return mPosY[index] - mRefPosY[index];
	//	}

	//	public float getDeltaSize(int index) {
	//		double deltaX = mPosX[index] - mRefPosX[index];
	//		double deltaY = mPosY[index] - mRefPosY[index];
	//		float size = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	//		return size;
	//	}

}
