package fr.arnaudguyon.smartgl.opengl;

import java.util.Vector;

import android.opengl.Matrix;

public abstract class RenderObject implements IShaderTextureFade {

	private boolean mIs3D;
	private boolean mVisible;
	private Vector<Face3D> mFaces;
	private float[] mMatrix;
    private float mAlpha = 1;
    private boolean mInvalidMatrix = true;

	public RenderObject(boolean is3D) {
		mIs3D = is3D;
		mFaces = new Vector<Face3D>();
		mMatrix = new float[16];
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

	public final Vector<Face3D> getFaces() {
		return mFaces;
	}

	public final void setFaces(Vector<Face3D> faces) {
		mFaces = faces;
	}
	public final void addFace(Face3D face) {
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
		final int faceSize = mFaces.size();
		for (int faceIt = 0; faceIt < faceSize; ++faceIt) {
			Face3D face = mFaces.get(faceIt);
			Texture texture = face.getTexture();
			if (texture != null) {
				texture.unbindTexture();
			}
		}
		// then normal release resources
		releaseResources();
	}

	protected void releaseResources() {
		final int faceSize = mFaces.size();
		for (int faceIt = 0; faceIt < faceSize; ++faceIt) {
			Face3D face = mFaces.get(faceIt);
			face.releaseResources();
		}
		mFaces.clear();
	}

	public int getProgramIndex(Face3D face) {
		return 0;
	}

	public void releaseTextures() {
		Vector<Face3D> faces = getFaces();
		final int faceSize = faces.size();
		for (int faceIt = 0; faceIt < faceSize; ++faceIt) {
			Face3D face = faces.get(faceIt);
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
