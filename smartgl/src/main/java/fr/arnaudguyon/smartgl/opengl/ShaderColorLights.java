package fr.arnaudguyon.smartgl.opengl;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import fr.arnaudguyon.smartgl.tools.Assert;

public class ShaderColorLights extends Shader {

    private final static String VERTEX_SHADER =
            "uniform mat4 m_ProjectionMatrix;" +
                    "uniform mat4 mModelMatrix;" +
                    "uniform vec3 mLightDirection;" +
                    "uniform vec4 mLightColor;" +
                    "uniform vec4 mAmbiantColor;" +
                    "attribute vec4 m_Position;" +
                    "attribute vec3 mNormals;" +
                    "varying vec4 vLightColor;" +
                    "attribute vec4 m_Color;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {" +
                    "  gl_Position = m_ProjectionMatrix * m_Position;" +
                    "  vec3 modelViewNormal = vec3(mModelMatrix * vec4(mNormals, 0.0));" +
                    "  float diffuse = max(dot(modelViewNormal, -mLightDirection), 0.0);" +
                    "  vLightColor = mAmbiantColor + (mLightColor * diffuse);" +
                    "  vColor = m_Color;\n" +
                    "}";

    private final static String PIXEL_SHADER =
            "precision mediump float;" +
                    "varying vec4 vLightColor;" +
                    "uniform sampler2D sTexture;" +
                    "varying vec4 vColor;\n" +
                    "void main() {" +
                    "  gl_FragColor = vColor * vLightColor;" +
                    "}";

    private int mModelMatrixId;
    private int mLightDirectionId;
    private int mLightColorId;
    private int mLightAmbiantId;
    private int mNormalsId;

    public ShaderColorLights() {
        super(VERTEX_SHADER, PIXEL_SHADER);
    }

    @Override
    public boolean useTexture() {
        return false;
    }

    @Override
    public boolean useColor() {
        return true;
    }

    @Override
    protected String getVertexAttribName() {
        return "m_Position";
    }

    @Override
    protected String getUVAttribName() {
        return null;
    }

    @Override
    protected String getColorAttribName() {
        return "m_Color";
    }

    @Override
    protected String getProjMatrixAttribName() {
        return "m_ProjectionMatrix";
    }

    @Override
    protected void init(int programId) {
        super.init(programId);

        mModelMatrixId = GLES20.glGetUniformLocation(programId, "mModelMatrix");
        mLightDirectionId = GLES20.glGetUniformLocation(programId, "mLightDirection");
        mLightColorId = GLES20.glGetUniformLocation(programId, "mLightColor");
        mLightAmbiantId = GLES20.glGetUniformLocation(programId, "mAmbiantColor");
        mNormalsId = GLES20.glGetAttribLocation(programId, "mNormals");
        Assert.assertTrue(mModelMatrixId >= 0);
        Assert.assertTrue(mLightDirectionId >= 0);
        Assert.assertTrue(mLightColorId >= 0);
        Assert.assertTrue(mLightAmbiantId >= 0);
        Assert.assertTrue(mNormalsId >= 0);
    }

    @Override
    public void onPreRender(OpenGLRenderer renderer, RenderObject object, Face3D face3D) {

        float[] modelMatrix = object.getMatrix();
        GLES20.glUniformMatrix4fv(mModelMatrixId, 1, false, modelMatrix, 0);

        float[] lightDirection = renderer.getLightDirection();
        GLES20.glUniform3fv(mLightDirectionId, 1, lightDirection, 0);

        float[] lightColor = renderer.getLightColor();
        GLES20.glUniform4fv(mLightColorId, 1, lightColor, 0);

        float[] ambiant = renderer.getLightAmbiant();
        GLES20.glUniform4fv(mLightAmbiantId, 1, ambiant, 0);

        GLES20.glEnableVertexAttribArray(mNormalsId);
        NormalList normalList = face3D.getNormalList();
        FloatBuffer vertexBuffer = normalList.getFloatBuffer();
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalsId, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
    }

}
