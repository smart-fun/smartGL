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

public class ShaderColor extends Shader {

	// @formatter:off

	private final static String VERTEX_SHADER_COLOR_SCRIPT =
		"uniform mat4 m_ProjectionMatrix;\n" +
		"attribute vec4 m_Position;\n" +
		"attribute vec4 m_Color;\n" +
		"varying vec4 vColor;\n" +
		"void main() {\n" +
		"  gl_Position = m_ProjectionMatrix * m_Position;\n" +
		"  vColor = m_Color;\n" +
		"}\n";
	private final static String PIXEL_SHADER_COLOR_SCRIPT =
		"precision mediump float;\n" +
		"varying vec4 vColor;\n" +
		"void main() {\n" +
		"  gl_FragColor = vColor;\n" +
		"}\n";


	public ShaderColor() {
		super(VERTEX_SHADER_COLOR_SCRIPT, PIXEL_SHADER_COLOR_SCRIPT);
	}

	@Override public boolean useTexture()	{ return false; }
	@Override public boolean useColor()		{ return true; }
	
	@Override protected String getVertexAttribName()		{ return "m_Position"; }
	@Override protected String getUVAttribName()			{ return null; }
	@Override protected String getColorAttribName()			{ return "m_Color"; }
	@Override protected String getProjMatrixAttribName()	{ return "m_ProjectionMatrix"; }
	
	// @formatter:on

}
