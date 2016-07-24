package fr.arnaudguyon.smartgl.touch;

import java.util.Vector;

class TouchFingerInfo {

	//private static final String TAG = "TouchFingerInfo";

	private final static float MOVE_TOLERENCE_DISTANCE = 30; // pixels TODO: depending on device resolution
	private final static float TAP_DURATION = 500; // ms

	private final static float LONGPRESS_TOLERENCE_DISTANCE = 10; // pixels TODO: depending on device resolution
	private final static float LONGPRESS_DURATION = 700; // ms

	private class TapInfo {
		private float mX, mY;
		private long mDate;

		TapInfo() {
			mDate = System.currentTimeMillis();
		}

		private TapInfo(float x, float y) {
			this();
			mX = x;
			mY = y;
		}
	}

	private float mXTouch, mYTouch;
	private long mTouchDate;
	private float mXUnTouch, mYUnTouch;
	private long mUnTouchDate;
	private float mXMove, mYMove;
	private int mFinger;
	private boolean mOn;
	private Vector<TapInfo> mTaps;
	private long mLongPressDate;

	final float getXTouch() {
		return mXTouch;
	}

	final float getYTouch() {
		return mYTouch;
	}

	//	final float getTouchDate() {
	//		return mTouchDate;
	//	}

	//	final float getXUnTouch() {
	//		return mXUnTouch;
	//	}

	//	final float getYUnTouch() {
	//		return mYUnTouch;
	//	}

	//	final float getUnTouchDate() {
	//		return mUnTouchDate;
	//	}

	//	final float getXMove() {
	//		return mXMove;
	//	}

	//	final float getYMove() {
	//		return mYMove;
	//	}

	final int getFinger() {
		return mFinger;
	}

	final boolean isOn() {
		return mOn;
	}

	TouchFingerInfo(float x, float y, int finger) {
		mFinger = finger;
		touch(x, y);
		mTaps = new Vector<TapInfo>();
	}

	final void touch(float x, float y) {
		mXTouch = mXMove = mXUnTouch = x;
		mYTouch = mYMove = mYUnTouch = y;
		mTouchDate = System.currentTimeMillis();
		mLongPressDate = mTouchDate;
		mOn = true;
	}

	final void unTouch(TouchHelper touchHelper, float x, float y) {
		mXUnTouch = x;
		mYUnTouch = y;
		mUnTouchDate = System.currentTimeMillis();
		mLongPressDate = 0;
		mOn = false;
		handleTap(touchHelper);
	}

	final void move(float x, float y) {
		mXMove = x;
		mYMove = y;
	}

	private final void handleTap(TouchHelper touchHelper) {
		long dt = mUnTouchDate - mTouchDate;
		if (dt < TAP_DURATION) {
			float dx = mXUnTouch - mXTouch;
			float dy = mYUnTouch - mYTouch;
			dx = (dx >= 0) ? dx : -dx;
			dy = (dy >= 0) ? dy : -dy;
			if ((dx < MOVE_TOLERENCE_DISTANCE) && (dy < MOVE_TOLERENCE_DISTANCE)) {
				TapInfo tapInfo = new TapInfo(mXUnTouch, mYUnTouch);
				mTaps.add(tapInfo);
				handleMultiTap(touchHelper);
			}
		}
	}

	private final void handleMultiTap(TouchHelper touchHelper) {
		int nbTaps = 1;
		TapInfo refTap = mTaps.get(mTaps.size() - 1);
		long refDate = refTap.mDate;
		float refX = refTap.mX;
		float refY = refTap.mY;
		for (int index = mTaps.size() - 2; index > 0; --index) {

			TapInfo otherTap = mTaps.get(index);

			float dx = otherTap.mX - refX;
			float dy = otherTap.mY - refY;
			dx = (dx >= 0) ? dx : -dx;
			dy = (dy >= 0) ? dy : -dy;
			boolean timeOk = (refDate - otherTap.mDate < TAP_DURATION);

			if (timeOk && (dx < MOVE_TOLERENCE_DISTANCE) && (dy < MOVE_TOLERENCE_DISTANCE)) {
				nbTaps++;
				refDate = otherTap.mDate;
				refX = otherTap.mX;
				refY = otherTap.mY;
			} else {
				mTaps.remove(index);
			}
		}
		//Log.i(TAG, "MULTITAP n=" + nbTaps);
		touchHelper.tap(mFinger, mXUnTouch, mYUnTouch, nbTaps);
	}

	final void cancelLongPressDate() {
		mLongPressDate = 0;
	}

	final void checkLongPress(TouchHelper touchHelper) {
		if (mLongPressDate > 0) {

			float dx = mXMove - mXTouch;
			float dy = mYMove - mYTouch;
			dx = (dx >= 0) ? dx : -dx;
			dy = (dy >= 0) ? dy : -dy;
			if ((dx < LONGPRESS_TOLERENCE_DISTANCE) && (dy < LONGPRESS_TOLERENCE_DISTANCE)) {
				if (System.currentTimeMillis() - mLongPressDate > LONGPRESS_DURATION) {
					mLongPressDate = 0;
					touchHelper.longPress(mFinger, mXMove, mYMove);
				}
			} else { // Moved too much
				mLongPressDate = 0;
			}
		}
	}

}
