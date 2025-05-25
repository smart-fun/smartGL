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

import android.opengl.Matrix;

import androidx.annotation.NonNull;

public abstract class RenderObject implements IShaderTextureFade {

	private boolean mIs3D;
	private boolean mVisible;
	private @NonNull Vector<Face3D> mFaces = new Vector<>();
	private final @NonNull float[] mMatrix = new float[16];
    private float mAlpha = 1;
    private boolean mInvalidMatrix = true;

	public RenderObject(boolean is3D) {
		mIs3D = is3D;
		mVisible = true;
		Matrix.setIdentityM(mMatrix, 0);
	}
	
	public final boolean is3D() {
		return mIs3D;
	}

	public final void setVisible(boolean visible) {
		mVisible = visible;
	}

	public final boolean isVisible() {
		return mVisible;
	}

	public final boolean isHidden() {
		return !mVisible;
	}

	public final @NonNull Vector<Face3D> getFaces() {
		return mFaces;
	}

	public final void setFaces(@NonNull Vector<Face3D> faces) {
		mFaces = faces;
	}
	public final void addFace(@NonNull Face3D face) {
		mFaces.add(face);
	}

//	protected final float[] getMatrixArray() {
//		return mMatrix;
//	}
	
	protected boolean isContainer() {
		return false;
	}

	abstract protected void computeMatrix(float[] matrix);

    public float[] getMatrix() {
        if (mInvalidMatrix) {
            computeMatrix(mMatrix);
            mInvalidMatrix = false;
        }
        return mMatrix;
    }

    protected void invalidMatrix() {
        mInvalidMatrix = true;
    }

	public void forceReleaseAll() {
		// force deleteOpenGLResource
		for(Face3D face : mFaces) {
			Texture texture = face.getTexture();
			if (texture != null) {
				texture.unbindTexture();
			}
		}
		// then normal release resources
		releaseResources();
	}

	protected void releaseResources() {
		for(Face3D face : mFaces) {
			face.releaseResources();
		}
		mFaces.clear();
	}

	public void releaseTextures() {
		for(Face3D face : mFaces) {
			Texture texture = face.getTexture();
			if (texture != null) {
				texture.unbindTexture();
			}
		}
	}
	
	// always called, even if object is hidden
	public void tick(OpenGLRenderer renderer) {}

	public boolean shouldDisplay(OpenGLRenderer renderer) {
		return mVisible;
	}
	// called only if shouldDisplay return true
	public void onPreRenderObject(OpenGLRenderer renderer) {}
	// called for each face, typically used to add specific shader links
	public void onPreRenderFace(OpenGLRenderer renderer, Shader shader, Face3D face) {}

    @Override
    public float getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public void localToGlobalPosition(float localX, float localY, float localZ, float[] result4f) {
        float[] rectMatrix = getMatrix();
        result4f[0] = localX;
        result4f[1] = localY;
        result4f[2] = localZ;
        result4f[3] = 1;
        Matrix.multiplyMV(result4f, 0, rectMatrix, 0, result4f, 0);
    }
}
