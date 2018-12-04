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

import android.opengl.GLES20;
import android.util.Log;

import fr.arnaudguyon.smartgl.tools.Assert;

abstract public class Shader {
	
	private static final String TAG = "Shader";
	private final static int NOSHADER = 0;

	private String mVertexScript;
	private String mPixelScript;
	
	private int mVertexScriptId;
	private int mPixelScriptId;
	private int mVertexAttribId;
	private int mUVAttribId;
	private int mColorAttribId;
	private int mProjMatrixId;

	private Shader() {
	}

	public Shader(String vertexScript, String pixelScript) {
		this();
		mVertexScript = vertexScript;
		mPixelScript = pixelScript;
		
		mVertexScriptId = NOSHADER;
		mPixelScriptId = NOSHADER;
	}
	
	public boolean isLoaded() {
		return (mVertexScriptId != NOSHADER);
	}
	
	public void loadShader(final int programId) {
		mVertexScriptId = loadShader(GLES20.GL_VERTEX_SHADER, mVertexScript);
		mPixelScriptId = loadShader(GLES20.GL_FRAGMENT_SHADER, mPixelScript);
	}
	
	public void unloadShader() {
		if (isLoaded()) {
			GLES20.glDeleteShader(mVertexScriptId);
			GLES20.glDeleteShader(mPixelScriptId);
			mVertexScriptId = NOSHADER;
			mPixelScriptId = NOSHADER;
		}
	}
	
	public int getVertexScriptId() {
		return mVertexScriptId;
	}

	public int getPixelScriptId() {
		return mPixelScriptId;
	}

	public abstract boolean useTexture();
	public abstract boolean useColor();
	protected abstract String getVertexAttribName();
	protected abstract String getUVAttribName();
	protected abstract String getColorAttribName();
	protected abstract String getProjMatrixAttribName();

	public int getVertexAttribId() {
		return mVertexAttribId;
	}

	public int getUVAttribId() {
		return mUVAttribId;
	}

	public int getColorAttribId() {
		return mColorAttribId;
	}

	public int getProjMatrixId() {
		return mProjMatrixId;
	}

	private int loadShader(int shaderType, String script) {

		int shader = GLES20.glCreateShader(shaderType);
		Assert.assertTrue(shader != 0);

		GLES20.glShaderSource(shader, script);
		GLES20.glCompileShader(shader);
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			Log.e(TAG, "Could not compile shader " + shaderType + " : " + GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			shader = 0;
			Assert.assertTrue(false);
		}
		return shader;
	}

	protected void init(final int programId) {
		
		final String vertexAttribName = getVertexAttribName();
		final String uvAttribName = getUVAttribName();
		final String colorAttribName = getColorAttribName();
		final String projMatrixAttribName = getProjMatrixAttribName();

		mVertexAttribId = (vertexAttribName != null) ? GLES20.glGetAttribLocation(programId, vertexAttribName) : -1;
		mUVAttribId = (uvAttribName != null) ? GLES20.glGetAttribLocation(programId, uvAttribName) : -1;
		mColorAttribId = (colorAttribName != null) ? GLES20.glGetAttribLocation(programId, colorAttribName) : -1;
		mProjMatrixId = (projMatrixAttribName != null) ? GLES20.glGetUniformLocation(programId, projMatrixAttribName) : -1;
		Assert.assertTrue(mVertexAttribId >= 0);
		Assert.assertTrue((mUVAttribId >= 0) || !useTexture());
		Assert.assertTrue((mColorAttribId >= 0) || !useColor());
		Assert.assertTrue(mProjMatrixId >= 0);
	}
	
	public void onPreRender(OpenGLRenderer renderer, RenderObject object, Face3D face) {
	}

}
