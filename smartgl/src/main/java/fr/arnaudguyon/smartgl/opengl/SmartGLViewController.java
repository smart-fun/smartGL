package fr.arnaudguyon.smartgl.opengl;

import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

/**
 * Created by arnaud on 19/11/2016.
 */

public interface SmartGLViewController {
    void onPrepareView(SmartGLView smartGLView);
    void onReleaseView(SmartGLView smartGLView);
    void onResizeView(SmartGLView smartGLView);
    void onTick(SmartGLView smartGLView);
    void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent event);
}
