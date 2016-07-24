package fr.arnaudguyon.smartgl.opengl;

import android.app.Fragment;

/**
 * Created by arnaud on 21/03/2015.
 */
public abstract class OpenGLFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        OpenGLView view = (OpenGLView) getView();	// TODO: find a way to force user to create a OpenGLView
        if (view != null) {
            view.onResume();
        }
    }

    @Override
    public void onPause() {
        SmartGLView view = (SmartGLView) getView();
        if (view != null) {
            view.onPause();
        }
        onReleaseResourcesInternal();
        super.onPause();
    }

    void onReleaseResourcesInternal() {
        OpenGLView view = (OpenGLView) getView();
        if (view != null) {
            view.onReleaseResourcesInternal();
            view.onReleaseResources();
        }
    }

    // From SmartGLFragment -> OpenGLView -> OpenGLRenderer
    protected abstract void onReleaseResources();

    // From OpenGLRenderer -> OpenGLView -> SmartGLFragment
    void onAcquireResourcesInternal() {}
    protected abstract void onAcquireResources();
}
