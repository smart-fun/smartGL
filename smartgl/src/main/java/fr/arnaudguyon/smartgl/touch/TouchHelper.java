package fr.arnaudguyon.smartgl.touch;

import java.util.Vector;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class TouchHelper {
	
	private static final String TAG = "TouchHelper";

	private Vector<TouchHelperEvent> mEvents;
	private Vector<TouchFingerInfo> mFingerInfos;
	private int[] mViewOffset;
	private VelocityTracker mVelocityTracker;
	private float mMaximumFlingVelocity = Float.MAX_VALUE;
	private float mMinimumFlingVelocity = Float.MIN_VALUE;

	public TouchHelper() {
		mEvents = new Vector<>();
		mFingerInfos = new Vector<>();
		mViewOffset = new int[2];
	}

	//	public void releaseResources() {
	//		mEvents.clear();
	//		mFingerInfos.clear();
	//	}

	public TouchHelperEvent getNextEvent() {
		synchronized (this) {
			if (mEvents.size() > 0) {
				TouchHelperEvent result = mEvents.get(0);
				mEvents.remove(0);
				return result;
			} else {
				checkLongPress();
			}
		}
		return null;
	}

	private void addEvent(TouchHelperEvent event) {
		synchronized (this) {
			mEvents.add(event);
		}
	}

	private void checkLongPress() {

		TouchFingerInfo finger = null;

		int nbFingersOn = 0;
		final int fingerInfosSize = mFingerInfos.size();
		for (int fingerInfoIt = 0; fingerInfoIt < fingerInfosSize; ++fingerInfoIt) {
			TouchFingerInfo touchFingerInfo = mFingerInfos.get(fingerInfoIt);
			if (touchFingerInfo.isOn()) {
				++nbFingersOn;
				if (nbFingersOn > 1) {
					cancelLongPressDates();
					return;
				}
				finger = touchFingerInfo;
			}
		}

		if (finger != null) {
			finger.checkLongPress(this);
		}
	}

	private void cancelLongPressDates() {
		final int fingerInfosSize = mFingerInfos.size();
		for (int fingerInfoIt = 0; fingerInfoIt < fingerInfosSize; ++fingerInfoIt) {
			TouchFingerInfo touchFingerInfo = mFingerInfos.get(fingerInfoIt);
			touchFingerInfo.cancelLongPressDate();
		}
	}

	void longPress(int finger, float x, float y) {
		TouchHelperEvent myEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.LONGPRESS, x, y, x, y, finger);
		addEvent(myEvent);
	}

    private float xFromView(View view, float x) {
        view.getLocationOnScreen(mViewOffset);
        x += mViewOffset[0];
        return x;
    }
    private float yFromView(View view, float y) {
        view.getLocationOnScreen(mViewOffset);
        y += mViewOffset[1];
        return y;
    }

	private float normalizedX(View view, float x) {
		if (view != null) {
			view.getLocationOnScreen(mViewOffset);
			//view.getLocationInWindow(mViewOffset);
			x -= mViewOffset[0];
		}
		/*
		 * if (x < 0) { x = 0; } else if (x > DisplayHelper.sScreenWidth - 1) {
		 * x = DisplayHelper.sScreenWidth - 1; }
		 */
		return x;
	}

	private float normalizedY(View view, float y) {
		if (view != null) {
			view.getLocationOnScreen(mViewOffset);
			//view.getLocationInWindow(mViewOffset);
			y -= mViewOffset[1];
		}
//		if (y < 0) {
//			y = 0;
//		} else if (y > DisplayHelper.sScreenHeight - 1) {
//			y = DisplayHelper.sScreenHeight - 1;
//		}
		return y;
	}

	public void onTouchEvent(View view, MotionEvent event, boolean fromOtherView) {
		synchronized (this) {

			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(event);

			int action = event.getAction();
			final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//			float x = normalizeResult ? normalizedX(view, event.getX(pointerIndex)) : event.getX(pointerIndex);
//			float y = normalizeResult ? normalizedY(view, event.getY(pointerIndex)) : event.getY(pointerIndex);
            float x = fromOtherView ? xFromView(view, event.getX(pointerIndex)) : event.getX(pointerIndex);
            float y = fromOtherView ? yFromView(view, event.getY(pointerIndex)) : event.getY(pointerIndex);

			final int finger = event.getPointerId(pointerIndex);

			if (finger > 1) {
				Log.i(TAG, "only 2 fingers handled");
				return;
			}

			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: // put 1st finger
			{
				//Log.i(TAG, "ACTION DOWN");
				mFingerInfos.clear();
				TouchFingerInfo fingerInfo = new TouchFingerInfo(x, y, finger);
				addFingerInfo(fingerInfo);
				TouchHelperEvent myEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.SINGLETOUCH, x, y, x, y, finger);
				addEvent(myEvent);
			}
				break;
			// Uncomment to make fling/swipe work
			// case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: // remove last finger
			{
				//Log.i(TAG, "ACTION UP OR CANCEL");
				TouchFingerInfo fingerInfo = getFingerInfo(finger);
				if (fingerInfo == null) {
					TouchHelperEvent myEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.SINGLEUNTOUCH, x, y, x, y, finger);
					addEvent(myEvent);
				} else {
					// fling event
					final VelocityTracker velocityTracker = mVelocityTracker;
					final int pointerId = event.getPointerId(0);
					velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
					final float velocityY = velocityTracker.getYVelocity(pointerId);
					final float velocityX = velocityTracker.getXVelocity(pointerId);

					if ((Math.abs(velocityY) > mMinimumFlingVelocity) || (Math.abs(velocityX) > mMinimumFlingVelocity)) {
						TouchHelperEvent myFlingEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.FLING, fingerInfo.getXTouch(), fingerInfo.getYTouch(), x, y, velocityX, velocityY, finger);
						addEvent(myFlingEvent);
					}
					mVelocityTracker.recycle();
					mVelocityTracker = null;

					fingerInfo.unTouch(this, x, y);
					TouchHelperEvent myEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.SINGLEUNTOUCH, fingerInfo.getXTouch(), fingerInfo.getYTouch(), x, y, finger);
					addEvent(myEvent);
				}
				mFingerInfos.clear();
			}
				break;
			case MotionEvent.ACTION_POINTER_DOWN: // put other finger
			{
				//Log.i(TAG, "ACTION POINTER DOWN");
				TouchFingerInfo fingerInfo = getFingerInfo(finger);
				if (fingerInfo == null) {
					fingerInfo = new TouchFingerInfo(x, y, finger);
					addFingerInfo(fingerInfo);
				} else {
					fingerInfo.touch(x, y);
				}

				TouchHelperEvent myEvent = new TouchHelperEvent(2, TouchHelperEvent.TouchEventType.MULTITOUCH);
				int index = 0;

				final int fingerInfosSize = mFingerInfos.size();
				for (int fingerInfoIt = 0; fingerInfoIt < fingerInfosSize; ++fingerInfoIt) {
					TouchFingerInfo info = mFingerInfos.get(fingerInfoIt);
					if (info.isOn()) {
						int infoFinger = info.getFinger();
						int infoIndex = event.findPointerIndex(infoFinger);
						if (infoIndex < 0) { // not found, can't do anything
							return;
						}

//						float newX = normalizeResult ? normalizedX(view, event.getX(infoIndex)) : event.getX(infoIndex);
//						float newY = normalizeResult ? normalizedY(view, event.getY(infoIndex)) : event.getY(infoIndex);
                        float newX = fromOtherView ? xFromView(view, event.getX(infoIndex)) : event.getX(infoIndex);
                        float newY = fromOtherView ? yFromView(view, event.getY(infoIndex)) : event.getY(infoIndex);
						info.move(newX, newY);
						myEvent.setValues(index, infoFinger, info.getXTouch(), info.getYTouch(), newX, newY);
						++index;
						if (index >= 2) { // 2 fingers max
							break;
						}
					}
				}
				addEvent(myEvent);

			}
				break;
			case MotionEvent.ACTION_POINTER_UP: // remove other finger
			{
				//Log.i(TAG, "ACTION POINTER UP");
				{
					// Check the dot product of current velocities.
					// If the pointer that left was opposing another velocity vector, clear.
					mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
					final int upIndex = event.getActionIndex();
					final int id1 = event.getPointerId(upIndex);
					final float x1 = mVelocityTracker.getXVelocity(id1);
					final float y1 = mVelocityTracker.getYVelocity(id1);
					final int pointerCount = event.getPointerCount();
					for (int i = 0; i < pointerCount; i++) {
						if (i == upIndex)
							continue;

						final int id2 = event.getPointerId(i);
						final float x11 = x1 * mVelocityTracker.getXVelocity(id2);
						final float y11 = y1 * mVelocityTracker.getYVelocity(id2);

						final float dot = x11 + y11;
						if (dot < 0) {
							mVelocityTracker.clear();
							break;
						}
					}
				}
				TouchFingerInfo fingerInfo = getFingerInfo(finger);
				fingerInfo.unTouch(this, x, y);
				TouchHelperEvent myEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.MULTIUNTOUCH, fingerInfo.getXTouch(), fingerInfo.getYTouch(), x, y, finger);
				addEvent(myEvent);
			}
				break;
			case MotionEvent.ACTION_MOVE: {
				//Log.i(TAG, "ACTION MOVE");
				boolean isSingleMove = (getNbFingersOn() <= 1);
				if (isSingleMove) { // SINGLE MOVE
					TouchFingerInfo fingerInfo = getFingerInfo(finger);
					fingerInfo.move(x, y);
					TouchHelperEvent myEvent = new TouchHelperEvent(TouchHelperEvent.TouchEventType.SINGLEMOVE, fingerInfo.getXTouch(), fingerInfo.getYTouch(), x, y, finger);
					addEvent(myEvent);
				} else { // MULTIPLE MOVE

					// update all fingers and prepare event
					TouchHelperEvent myEvent = new TouchHelperEvent(2, TouchHelperEvent.TouchEventType.MULTIMOVE);
					int index = 0;
					final int fingerInfosSize = mFingerInfos.size();
					for (int fingerInfoIt = 0; fingerInfoIt < fingerInfosSize; ++fingerInfoIt) {
						TouchFingerInfo info = mFingerInfos.get(fingerInfoIt);
						if (info.isOn()) {
							int infoFinger = info.getFinger();
							int infoIndex = event.findPointerIndex(infoFinger);
							if (infoIndex < 0) { // not found, can't do anything
								return;
							}
//							float newX = normalizeResult ? normalizedX(view, event.getX(infoIndex)) : event.getX(infoIndex);
//							float newY = normalizeResult ? normalizedY(view, event.getY(infoIndex)) : event.getY(infoIndex);
                            float newX = fromOtherView ? xFromView(view, event.getX(infoIndex)) : event.getX(infoIndex);
                            float newY = fromOtherView ? yFromView(view, event.getY(infoIndex)) : event.getY(infoIndex);
							info.move(newX, newY);
							myEvent.setValues(index, infoFinger, info.getXTouch(), info.getYTouch(), newX, newY);
							++index;
							if (index >= 2) { // 2 fingers max
								break;
							}
						}
					}
					addEvent(myEvent);
				}

			}
				break;
			}
		}
	}

	private void addFingerInfo(TouchFingerInfo fingerInfo) {
		for (int index = 0; index < mFingerInfos.size(); ++index) {
			TouchFingerInfo info = mFingerInfos.get(index);
			if (info.getFinger() == fingerInfo.getFinger()) { // already present
				mFingerInfos.setElementAt(fingerInfo, index); // replace element
				return;
			}
		}
		mFingerInfos.add(fingerInfo);
	}

	private TouchFingerInfo getFingerInfo(int finger) {
		final int fingerInfosSize = mFingerInfos.size();
		for (int fingerInfoIt = 0; fingerInfoIt < fingerInfosSize; ++fingerInfoIt) {
			TouchFingerInfo ref = mFingerInfos.get(fingerInfoIt);
			if (ref.getFinger() == finger) {
				return ref;
			}
		}
		return null;
	}

	private int getNbFingersOn() {
		int nbOn = 0;
		final int fingerInfosSize = mFingerInfos.size();
		for (int fingerInfoIt = 0; fingerInfoIt < fingerInfosSize; ++fingerInfoIt) {
			TouchFingerInfo info = mFingerInfos.get(fingerInfoIt);
			if (info.isOn()) {
				++nbOn;
			}
		}
		return nbOn;
	}

	void tap(int finger, float x, float y, int nbTaps) {
		TouchHelperEvent event = new TouchHelperEvent(finger, x, y, nbTaps);
		addEvent(event);
	}

}
