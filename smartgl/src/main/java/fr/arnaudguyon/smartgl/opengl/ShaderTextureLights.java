/*
    Copyright 2017 Arnaud Guyon

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

import junit.framework.Assert;

import java.nio.FloatBuffer;

/**
 * Created by aguyon on 18.01.17.
 */

public class ShaderTextureLights extends Shader {

    private final static String VERTEX_SHADER =
            "uniform mat4 m_ProjectionMatrix;" +
            "uniform mat4 mModelMatrix;" +
                    "uniform vec3 mLightDirection;" +
                    "uniform vec4 mLightColor;" +
                    "uniform vec4 mAmbiantColor;" +
                    "attribute vec4 m_Position;" +
                    "attribute vec2 m_UV;" +
                    "attribute vec3 mNormals;" +
                    "varying vec2 vTextureCoord;" +
                    "varying vec4 vLightColor;" +
                    "void main() {" +
                    "  gl_Position = m_ProjectionMatrix * m_Position;" +
                    "  vTextureCoord = m_UV;" +
                    "  vec3 modelViewNormal = vec3(mModelMatrix * vec4(mNormals, 0.0));" +
                    "  float diffuse = max(dot(modelViewNormal, mLightDirection), 0.0);" +
                    "  vLightColor = mAmbiantColor + (mLightColor * diffuse);" +
                    "}";
    private final static String PIXEL_SHADER =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "varying vec4 vLightColor;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord) * vLightColor;" +
                    "}";

    private int mModelMatrixId;
    private int mLightDirectionId;
    private int mLightColorId;
    private int mLightAmbiantId;
    private int mNormalsId;

    public ShaderTextureLights() {
        super(VERTEX_SHADER, PIXEL_SHADER);
    }

    @Override
    public boolean useTexture() {
        return true;
    }

    @Override
    public boolean useColor() {
        return false;
    }

    @Override
    protected String getVertexAttribName() {
        return "m_Position";
    }

    @Override
    protected String getUVAttribName() {
        return "m_UV";
    }

    @Override
    protected String getColorAttribName() {
        return null;
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

        NormalList normalList = face3D.getNormalList();
        FloatBuffer vertexBuffer = normalList.getFloatBuffer();
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalsId, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
    }

}
