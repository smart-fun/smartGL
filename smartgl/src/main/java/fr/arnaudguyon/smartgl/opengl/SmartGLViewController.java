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

import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

/**
 * Created by arnaud on 19/11/2016.
 */

/**
 * Interface which interacts with the SmartGLView
 */
public interface SmartGLViewController {
    /**
     * called when the SmartGLView is ready for display
     * @param smartGLView
     */
    void onPrepareView(SmartGLView smartGLView);

    /**
     * called with the SmartGLView is about to dismiss
     * @param smartGLView
     */
    void onReleaseView(SmartGLView smartGLView);

    /**
     * called when the view is resized
     * @param smartGLView
     */
    void onResizeView(SmartGLView smartGLView);

    /**
     * called at every OpenGL frame
     * @param smartGLView
     */
    void onTick(SmartGLView smartGLView);

    /**
     * called after a user touch interaction
     * @param smartGLView
     * @param event
     */
    void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent event);
}
