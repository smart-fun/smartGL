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

public class ShaderTexture extends Shader {

	// @formatter:off

	private final static String VERTEX_SHADER_TEXTURE_SCRIPT =
		"uniform mat4 m_ProjectionMatrix;" +
		"attribute vec4 m_Position;" +
		"attribute vec2 m_UV;" +
		"varying vec2 vTextureCoord;" +
		"void main() {" +
		"  gl_Position = m_ProjectionMatrix * m_Position;" +
		"  vTextureCoord = m_UV;" +
		"}";
	private final static String PIXEL_SHADER_TEXTURE_SCRIPT =
		"precision mediump float;" +
		"varying vec2 vTextureCoord;" +
		"uniform sampler2D sTexture;" +
		"void main() {" +
		"  gl_FragColor = texture2D(sTexture, vTextureCoord);" +
		"}";

	public ShaderTexture() {
		super(VERTEX_SHADER_TEXTURE_SCRIPT, PIXEL_SHADER_TEXTURE_SCRIPT);
	}

	@Override public boolean useTexture()	{ return true; }
	@Override public boolean useColor()		{ return false; }

	@Override protected String getVertexAttribName()		{ return "m_Position"; }
	@Override protected String getUVAttribName()			{ return "m_UV"; }
	@Override protected String getColorAttribName()			{ return null; }
	@Override protected String getProjMatrixAttribName()	{ return "m_ProjectionMatrix"; }

	// @formatter:on
}