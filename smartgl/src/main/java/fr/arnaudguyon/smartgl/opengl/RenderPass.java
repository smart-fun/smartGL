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

import java.util.Vector;

import android.opengl.GLES20;

import androidx.annotation.NonNull;

import fr.arnaudguyon.smartgl.tools.Assert;

public class RenderPass {
    private final static int NOPROGRAM = 0;

    private int mProgramId = NOPROGRAM;
    private Shader mShaders;

    private final @NonNull
    Vector<RenderObject> mRenderObjects = new Vector<>();

    private boolean mUseZBuffer = false;
    private boolean mClearZBuffer = false;

    /* package */ boolean isLoaded() {
        return (mProgramId != NOPROGRAM);
    }

    /* package */ void load() {
        Assert.assertTrue(!isLoaded());
        if (mShaders != null) {
            mProgramId = GLES20.glCreateProgram();

            Assert.assertTrue(mProgramId != NOPROGRAM);
            if (!mShaders.isLoaded()) {
                mShaders.loadShader(mProgramId);
            }
            GLES20.glAttachShader(mProgramId, mShaders.getVertexScriptId());
            GLES20.glAttachShader(mProgramId, mShaders.getPixelScriptId());
            GLES20.glLinkProgram(mProgramId);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgramId, GLES20.GL_LINK_STATUS, linkStatus, 0);
            Assert.assertTrue(linkStatus[0] == GLES20.GL_TRUE);
            mShaders.init(mProgramId);
        }
    }

    public @NonNull
    Vector<RenderObject> getRenderObjects() {
        return mRenderObjects;
    }

    public boolean removeObject(@NonNull RenderObject renderObject) {
        return mRenderObjects.remove(renderObject);
    }

    public boolean useZBuffer() {
        return mUseZBuffer;
    }

    public boolean clearZBuffer() {
        return mClearZBuffer;
    }

    public void clearObjects() {
        mRenderObjects.clear();
    }

    public RenderPass() {
    }

    public RenderPass(boolean useZBuffer, boolean clearZBuffer) {
        this();
        mUseZBuffer = useZBuffer;
        mClearZBuffer = clearZBuffer;
    }

    public void setShader(Shader shader) {
        mShaders = shader;
    }

    public int getProgramId() {
        return mProgramId;
    }

    public Shader getShader() {
        return mShaders;
    }

    public void releaseResources() {
        final int renderSize = mRenderObjects.size();
        for (int renderIt = 0; renderIt < renderSize; ++renderIt) {
            RenderObject renderObject = mRenderObjects.get(renderIt);
            renderObject.releaseResources();
        }
        mRenderObjects.clear();
    }

    /* package */ void sortObjects() {
    }

}
