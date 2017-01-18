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

public class ShaderTextureFade extends Shader {
	
	private int mFadeAttribId;

	// @formatter:off

	private final static String VERTEX_SHADER_TEXTURE_SCRIPT =
		"uniform mat4 m_ProjectionMatrix;\n" +
		"attribute vec4 m_Position;\n" +
		"attribute vec2 m_UV;\n" +
		"varying vec2 vTextureCoord;\n" +
		"void main() {\n" +
		"  gl_Position = m_ProjectionMatrix * m_Position;\n" +
		"  vTextureCoord = m_UV;\n" +
		"}\n";
	private final static String PIXEL_SHADER_TEXTURE_SCRIPT =
		"precision mediump float;\n" +
		"varying vec2 vTextureCoord;\n" +
		"uniform sampler2D sTexture;\n" +
		"uniform float m_FadeValue;\n" +
		"void main() {\n" +
		"  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
		"  gl_FragColor.a *= m_FadeValue;\n" +
		"}\n";

	public ShaderTextureFade() {
		super(VERTEX_SHADER_TEXTURE_SCRIPT, PIXEL_SHADER_TEXTURE_SCRIPT);
	}

	@Override public boolean useTexture()	{ return true; }
	@Override public boolean useColor()		{ return false; }

	@Override protected String getVertexAttribName()		{ return "m_Position"; }
	@Override protected String getUVAttribName()			{ return "m_UV"; }
	@Override protected String getColorAttribName()			{ return null; }
	@Override protected String getProjMatrixAttribName()	{ return "m_ProjectionMatrix"; }

	@Override
	protected void init(int programId) {
		super.init(programId);
		
		//mFadeAttribId = GLES20.glGetAttribLocation(programId, "m_FadeValue");
		mFadeAttribId = GLES20.glGetUniformLocation(programId, "m_FadeValue");
	}

	@Override
	public void onPreRender(OpenGLRenderer renderer, RenderObject object, Face3D face) {

		float fadeValue = 1;
		if (object instanceof IShaderTextureFade) {
			IShaderTextureFade user = object;
			fadeValue = user.getAlpha();
		}
		GLES20.glUniform1f(mFadeAttribId, fadeValue);
	}

	// @formatter:on

	
}
