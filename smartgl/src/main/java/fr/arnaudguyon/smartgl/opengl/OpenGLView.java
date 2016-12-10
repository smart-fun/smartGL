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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/* package */ abstract class OpenGLView extends GLSurfaceView {

    private OpenGLRenderer mOpenGLRenderer;

    public OpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OpenGLView(Context context) {
        super(context);
    }

    @Override
    public void setRenderer(Renderer renderer) throws RuntimeException {

        if (renderer instanceof OpenGLRenderer) {
            mOpenGLRenderer = (OpenGLRenderer) renderer;
            mOpenGLRenderer.setListener(this);
            setEGLContextClientVersion(2);
            ConfigGLSelector selector = new ConfigGLSelector(this);
            if (selector != null) {
                setEGLConfigChooser(selector);
                //setPreserveEGLContextOnPause(true);
                super.setRenderer(mOpenGLRenderer);
            }
        } else {
            throw new RuntimeException("renderer must be a OpenGLRenderer");
        }
    }

    public OpenGLRenderer getOpenGLRenderer() {
        return mOpenGLRenderer;
    }

    public void onPreRender(OpenGLRenderer renderer) {
    }

    @Override
    public void onPause() {
        if (mOpenGLRenderer != null) {
            mOpenGLRenderer.onPause();
        }
        releaseResources();
        if (mOpenGLRenderer != null) {
            mOpenGLRenderer.addRenderPass(null);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOpenGLRenderer != null) {
            mOpenGLRenderer.onResume();
        }
    }

    protected void acquireResources() {
    }

    protected void releaseResources() {
    }

    protected abstract void onViewResized(int width, int height);
}
