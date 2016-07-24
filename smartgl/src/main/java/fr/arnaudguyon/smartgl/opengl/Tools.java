package fr.arnaudguyon.smartgl.opengl;

import android.opengl.Matrix;

public class Tools {
	
	private static float[] sTmpV4 = new float[4];
	
	public static boolean worldToScreen(final float[] vertexPos, final float projMatrix[], int width, int height, final float[] windowPos) {
		Matrix.multiplyMV(sTmpV4, 0, projMatrix, 0, vertexPos, 0);

		if (sTmpV4[3] == 0) {
			sTmpV4[3] = 1;
			//windowPos[0] = windowPos[1] = windowPos[2] = 0;
			//return false;
		}
		// Map x, y and z to range 0-1
		windowPos[0] = ((sTmpV4[0] / sTmpV4[3]) * 0.5f + 0.5f) * width;
		windowPos[1] = (0.5f - (sTmpV4[1] / sTmpV4[3]) * 0.5f) * height;
		windowPos[2] = (sTmpV4[2] / sTmpV4[3]) * 0.5f + 0.5f;
		return true;
	}

	public static boolean worldToScreen(OpenGLRenderer renderer, final float[] vertexPos, final float[] windowPos) {
		if (renderer != null) {
			float[] projMatrix = renderer.getProjection3DMatrix();
			final int width = renderer.getWidth();
			final int height = renderer.getHeight();
			worldToScreen(vertexPos, projMatrix, width, height, windowPos);
			return true;
		}
		return false;
	}
}
