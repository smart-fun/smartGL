package fr.arnaudguyon.smartgl.opengl;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import fr.arnaudguyon.smartgl.touch.TouchHelper;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

public abstract class SmartGLView extends OpenGLView {
	
	//private static final String TAG = "SmartGLView";

	private TouchHelper mTouchHelper;
	private Sprite mInputSprite;

	public SmartGLView(Context context, OpenGLRenderer renderer, SmartGLFragment fragment) {
		super(context, renderer, fragment);
	}
	
	public SmartGLFragment getFragment() {
        OpenGLFragment fragment = super.getFragment();
        if ((fragment != null) && (fragment instanceof SmartGLFragment)) {
            return (SmartGLFragment) fragment;
        } else {
            return null;
        }
	}
	
	public SmartGLRenderer getSmartGLRenderer() {
		return (SmartGLRenderer) getOpenGLRenderer();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTouchHelper != null) {
			mTouchHelper.onTouchEvent(this, event, false);
		}
		return true;
	}

    public void onTouchEventFromOtherView(View fromView, MotionEvent event) {
        if (mTouchHelper != null) {
            mTouchHelper.onTouchEvent(fromView, event, true);
        }
    }

	@Override
	public void onPreRender(OpenGLRenderer renderer) {
		super.onPreRender(renderer);
		
		// Handle Touch Events: skip several moves in a row and send OnTouchEvent (on OpenGLThread)
		if (mTouchHelper != null) {
			TouchHelperEvent event = mTouchHelper.getNextEvent();
			if (event != null) {
				Vector<TouchHelperEvent> touchEvents = new Vector<TouchHelperEvent>();
				while(event != null) {
					//Log.i(TAG, "Touch " + event.getX(0) + " ; " + event.getY(0));
					int tabSize = touchEvents.size();
					TouchHelperEvent.TouchEventType newType = event.getType();
					if ((tabSize > 0) && (newType == TouchHelperEvent.TouchEventType.SINGLEMOVE || newType == TouchHelperEvent.TouchEventType.MULTIMOVE) && (touchEvents.get(tabSize - 1).getType() == newType)) {
						touchEvents.remove(tabSize - 1);
					}
					touchEvents.add(event);
					event = mTouchHelper.getNextEvent();
				}
				
				float frameDuration = renderer.getFrameDuration();
				
				for (int eventIndex = 0; eventIndex < touchEvents.size(); ++eventIndex) {
					event = touchEvents.get(eventIndex);
					
					boolean handledBySprites = touchEventOnSprites(event, frameDuration);
					if (handledBySprites) {
						continue;
					}
					
					onTouchEvent(event);
				}
				
			}
		}
	}
	
	private boolean touchEventOnSprites(TouchHelperEvent event, float frameDuration) {

		boolean spriteDisappeared = ((mInputSprite != null) && (mInputSprite.isHidden() || !mInputSprite.handlesInput()));

		if (spriteDisappeared || (event.getType() == TouchHelperEvent.TouchEventType.SINGLETOUCH)) {
			final float x = event.getX(0);
			final float y = event.getY(0);

			OpenGLRenderer renderer = getOpenGLRenderer();
			
			Vector<Sprite> hudSprites = renderer.getToucheableSprites();
			final int touchSize = hudSprites.size();
			for (int touchIt = touchSize - 1; touchIt >= 0; --touchIt) {
				RenderObject renderObject = hudSprites.get(touchIt);
				Sprite sprite = (Sprite) renderObject;
				if (sprite != null && sprite.handlesInput() && sprite.isVisible() && sprite.touchedBy(x, y)) {
					if (sprite.onTouch(event, frameDuration, mInputSprite)) {
						mInputSprite = sprite;
						return true;
					}
				}
			}
			mInputSprite = null;
			return false;
		} else if (mInputSprite == null) {
			return false;
		} else {
			mInputSprite.onTouch(event, frameDuration, mInputSprite);
			if (event.getType() == TouchHelperEvent.TouchEventType.SINGLEUNTOUCH) {
				mInputSprite = null;
			}
			return true;
		}
	}
	
	protected void onTouchEvent(TouchHelperEvent event) {
	}

    void onAcquireResourcesInternal() {
        super.onAcquireResourcesInternal();

        activateTouch(true);    // TODO: only if necessary


    }

    protected void onAcquireResources() {
    }

    public void activateTouch(boolean activate) {
        if (activate) {
            mTouchHelper = new TouchHelper();
        } else {
            mTouchHelper = null;
        }
    }

}
