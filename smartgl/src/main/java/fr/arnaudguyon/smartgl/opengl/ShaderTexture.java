package fr.arnaudguyon.smartgl.opengl;

public class ShaderTexture extends Shader {

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
		"void main() {\n" +
		"  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
		"}\n";

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