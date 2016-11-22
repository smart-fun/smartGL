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
