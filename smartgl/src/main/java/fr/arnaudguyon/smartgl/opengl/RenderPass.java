package fr.arnaudguyon.smartgl.opengl;

import java.util.Vector;

import junit.framework.Assert;
import android.opengl.GLES20;

public class RenderPass {
	private final static int NOPROGRAM = 0;

	private Vector<Integer> mProgramId;
	private Vector<Shader> mShaders;
	private Vector<RenderObject> mRenderObjects;
	private boolean mUseZBuffer;
	private boolean mClearZBuffer;
	
	public boolean isLoaded() {
		return ((mProgramId.size() > 0) && (mProgramId.firstElement() != NOPROGRAM));
	}
	public void load() {
		Assert.assertTrue(!isLoaded());
		for(int i=0; i<mShaders.size(); ++i) {
			final int programId = GLES20.glCreateProgram();
			
			Assert.assertTrue(programId != 0);
			final Shader shader = mShaders.get(i);
			if (!shader.isLoaded()) {
				shader.loadShader(programId);
			}
			GLES20.glAttachShader(programId, shader.getVertexScriptId());
			GLES20.glAttachShader(programId, shader.getPixelScriptId());
			GLES20.glLinkProgram(programId);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
			Assert.assertTrue(linkStatus[0] == GLES20.GL_TRUE);
			shader.init(programId);

			mProgramId.add(programId);
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
		mUseZBuffer = false;
		mClearZBuffer = false;
		mProgramId = new Vector<Integer>();
		mRenderObjects = new Vector<RenderObject>();
		mShaders = new Vector<Shader>();
	}

	public RenderPass(boolean useZBuffer, boolean clearZBuffer) {
		this();
		mUseZBuffer = useZBuffer;
		mClearZBuffer = clearZBuffer;
	}

	public void addShader(Shader shader) {
		mShaders.add(shader);
	}

	public int getProgramId(int progNumber) {
		Assert.assertTrue(progNumber < mShaders.size());
		return mProgramId.get(progNumber);
	}

	public Shader getShader(int progNumber) {
		Assert.assertTrue(progNumber < mShaders.size());
		return mShaders.get(progNumber);
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
