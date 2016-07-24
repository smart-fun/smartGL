package fr.arnaudguyon.smartgl.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.lang.ref.WeakReference;

public abstract class OpenGLView extends GLSurfaceView {

	private OpenGLRenderer	mOpenGLRenderer;
    WeakReference<OpenGLFragment> mFragment;

	public OpenGLView(Context context, OpenGLRenderer renderer, OpenGLFragment fragment) {
		super(context);
		
		mOpenGLRenderer = renderer;
        mFragment = new WeakReference<OpenGLFragment>(fragment);
		renderer.setListener(this);
		
		setEGLContextClientVersion(2);
		ConfigGLSelector selector = new ConfigGLSelector(this);
		if (selector != null) {
			setEGLConfigChooser(selector);
			//setPreserveEGLContextOnPause(true);
			setRenderer(mOpenGLRenderer);
		}
	}

    public OpenGLFragment getFragment() {
        if (mFragment != null) {
            return mFragment.get();
        } else {
            return null;
        }
    }
	
	public OpenGLRenderer getOpenGLRenderer() {
		return mOpenGLRenderer;
	}

	public void onPreRender(OpenGLRenderer renderer) {
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	void onReleaseResourcesInternal() {
		if (mOpenGLRenderer != null) {
			mOpenGLRenderer.onReleaseResourcesInternal();
            mOpenGLRenderer.onReleaseResources();
		}
	}
    protected abstract void onReleaseResources();

    void onAcquireResourcesInternal() {
        OpenGLFragment fragment = getFragment();
        if (fragment != null) {
            fragment.onAcquireResourcesInternal();
            fragment.onAcquireResources();
        }
    }
    protected abstract void onAcquireResources();
	
}
