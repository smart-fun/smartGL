package fr.arnaudguyon.smartgl.opengl;

// TODO for library
// abstract all smartGL stuff, and make onAcquire/onRelease abstract
// set default Matrix, and add function set3DMatrix or setCameraPosition/Orientation(2d / 3d pos)
// draw glview layout in edit mode (just color background ? or display fake stuff ?)
// put all in same package, so that package functions are not accessible from outside, only public

import android.app.Fragment;

public abstract class SmartGLFragment extends OpenGLFragment {

//    @Override
//	public void onResume() {
//		super.onResume();
//		SmartGLView view = (SmartGLView) getView();	// TODO: find a way to force user to create a SmartGLView
//		if (view != null) {
//			view.onResume();
//		}
//	}
//
//	@Override
//	public void onPause() {
//		SmartGLView view = (SmartGLView) getView();
//		if (view != null) {
//			view.onPause();
//		}
//		onReleaseResourcesInternal();
//		super.onPause();
//	}
//
//    void onReleaseResourcesInternal() {
//        SmartGLView view = (SmartGLView) getView();
//        if (view != null) {
//            view.onReleaseResourcesInternal();
//            view.onReleaseResources();
//        }
//    }
//
//	// From SmartGLFragment -> OpenGLView -> OpenGLRenderer
//	protected abstract void onReleaseResources();
//
//	// From OpenGLRenderer -> OpenGLView -> SmartGLFragment
//	void onAcquireResourcesInternal() {}
//    protected abstract void onAcquireResources();

}
