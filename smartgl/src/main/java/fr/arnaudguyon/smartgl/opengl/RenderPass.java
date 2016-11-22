package fr.arnaudguyon.smartgl.opengl;

import java.util.Vector;

import junit.framework.Assert;
import android.opengl.GLES20;

public class RenderPass {
	private final static int NOPROGRAM = 0;

	private int mProgramId = NOPROGRAM;
	private Shader mShaders;
	private Vector<RenderObject> mRenderObjects = new Vector<>();
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

	public Vector<RenderObject> getRenderObjects() {
		return mRenderObjects;
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

    /* package */ void sortObjects() {}

}
