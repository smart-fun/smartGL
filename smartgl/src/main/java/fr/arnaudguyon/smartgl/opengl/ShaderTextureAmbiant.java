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

/**
 * Created by aguyon on 18.01.17.
 */

public class ShaderTextureAmbiant extends Shader {

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
                    "uniform vec4 mAmbiantColor;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord) * mAmbiantColor;" +
                    "}";

    private int mLightAmbiantId;

    public ShaderTextureAmbiant() {
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

        mLightAmbiantId = GLES20.glGetUniformLocation(programId, "mAmbiantColor");
        Assert.assertTrue(mLightAmbiantId >= 0);
    }

    @Override
    public void onPreRender(OpenGLRenderer renderer, RenderObject object, Face3D face) {
        float[] ambiant = renderer.getLightAmbiant();
//        GLES20.glUniform4f(mLightAmbiantId, 1, 0, 0, 0.3f); // r,v,b,a
        GLES20.glUniform4fv(mLightAmbiantId, 1, ambiant, 0);
    }
}
