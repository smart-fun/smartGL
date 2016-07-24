package fr.arnaudguyon.smartgl.opengl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.util.Log;

public class ConfigGLSelector implements EGLConfigChooser {

	private static final String TAG = "ConfigGLSelector";
	private final static int EGL_OPENGL_ES2_BIT = 4; // WTF, defined nowhere

	private final static int[][] CONFIGS_PARAMS = {
			{ EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8, EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE },

			{ EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8, EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_DEPTH_SIZE, 8, EGL10.EGL_NONE } };

	@SuppressWarnings("unused")
	private ConfigGLSelector() {
	}

	public ConfigGLSelector(GLSurfaceView view) {
		view.getHolder().setFormat(PixelFormat.RGBA_8888); // Avoid DeadLock & Reboot on HTC Desire...
	}

	@Override
	public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

		boolean initSuccess = egl.eglInitialize(display, new int[] { 2, 0 });
		if (!initSuccess) {
			Log.e(TAG, "Can't initialize EGL");
			return null;
		}

		final int NB_CONFIGS_TO_FIND = 1;
		EGLConfig[] resultConfigs = new EGLConfig[NB_CONFIGS_TO_FIND];
		int[] numConfigsFound = new int[NB_CONFIGS_TO_FIND];

		for (int tryIndex = 0; tryIndex < CONFIGS_PARAMS.length; ++tryIndex) {
			int[] params = CONFIGS_PARAMS[tryIndex];
			boolean success = egl.eglChooseConfig(display, params, resultConfigs, NB_CONFIGS_TO_FIND, numConfigsFound);
			if (success) {
				return resultConfigs[tryIndex];
			}
		}

		Log.e(TAG, "Can't find any GL config");
		return null;
	}

}
