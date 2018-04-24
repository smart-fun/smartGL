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

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import fr.arnaudguyon.smartgl.touch.TouchHelper;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

/**
 * View where the OpenGL scene is displayed.
 * Is a OpenGLView that handles the Touch and the callbacks with the SmartGLViewController
 */

public class SmartGLView extends OpenGLView {

    private TouchHelper mTouchHelper;
    private Sprite mInputSprite;
    private SmartGLViewController mListener;

    public SmartGLView(Context context) {
        super(context);
    }

    public SmartGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * set the SmartGLRenderer as scene renderer
     *
     * @param context
     */
    public void setDefaultRenderer(@NonNull Context context) {
        SmartGLRenderer renderer = new SmartGLRenderer(context);
        setRenderer(renderer);
    }

    /**
     * returns the current SmartGLRenderer
     *
     * @return the current SmartGLRenderer or null if none
     */
    public SmartGLRenderer getSmartGLRenderer() {
        OpenGLRenderer renderer = getOpenGLRenderer();
        if (renderer instanceof SmartGLRenderer) {
            return (SmartGLRenderer) renderer;
        }
        return null;
    }

    /**
     * set the SmartGLViewController
     *
     * @param controller
     */
    public void setController(SmartGLViewController controller) {
        mListener = controller;
    }

    /**
     * gets the SmartGLViewController
     *
     * @return
     */
    public SmartGLViewController getController() {
        return mListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isClickable()) {
            if (mTouchHelper == null) {
                mTouchHelper = new TouchHelper();
            }
            mTouchHelper.onTouchEvent(this, event, false);
            return true;
        } else {
            mTouchHelper = null;
            return false;
        }
    }

    /**
     * to call if there is an Android view on top of the SmartGLView and there is a need that the SmartGL view handles the touch.
     * It propagates the touch to the SmartGLView.
     *
     * @param fromView an Android View
     * @param event    the touch event received in this view
     */
    public void onTouchEventFromOtherView(View fromView, MotionEvent event) {
        if (isClickable()) {
            if (mTouchHelper == null) {
                mTouchHelper = new TouchHelper();
            }
            mTouchHelper.onTouchEvent(fromView, event, true);
        } else {
            mTouchHelper = null;
        }
    }

    /**
     * called at every frame of OpenGL. Handles the Touch Events
     *
     * @param renderer
     */
    @Override
    public void onPreRender(OpenGLRenderer renderer) {
        super.onPreRender(renderer);

        if (mListener != null) {
            mListener.onTick(this);
        }

        // Handle Touch Events: skip several moves in a row and send OnTouchEvent (on OpenGLThread)
        if (mTouchHelper != null) {
            TouchHelperEvent event = mTouchHelper.getNextEvent();
            if (event != null) {
                Vector<TouchHelperEvent> touchEvents = new Vector<TouchHelperEvent>();
                while (event != null) {
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
        if (mListener != null) {
            mListener.onTouchEvent(this, event);
        }
    }

    @Override
    protected void acquireResources() {
        super.acquireResources();
        if (mListener != null) {
            mListener.onPrepareView(this);
        }
    }

    @Override
    protected void releaseResources() {
        super.releaseResources();
        mTouchHelper = null;
        if (mListener != null) {
            mListener.onReleaseView(this);
        }
    }

    @Override
    protected void onViewResized(int width, int height) {
        if (mListener != null) {
            mListener.onResizeView(this);
        }
    }
}
