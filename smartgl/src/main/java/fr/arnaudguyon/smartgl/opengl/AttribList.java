package fr.arnaudguyon.smartgl.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// parent class for VertexList, UVList, ColorList
public class AttribList {

	private int mFloatsPerElement;
	private FloatBuffer mFloatBuffer;
	private float[] mInternalBuffer;
	private int mCurrentIndex;

	private AttribList() {
		mFloatBuffer = null;
		mInternalBuffer = null;
		mCurrentIndex = 0;
	}

	public AttribList(int floatsPerElement) {
		this();
		mFloatsPerElement = floatsPerElement;
	}

	protected AttribList(AttribList other) {
		mFloatsPerElement = other.getNbFloatsPerElement();
		init(other.mInternalBuffer);
		finalizeBuffer();
	}
	
	protected AttribList(float[] other, int otherElementsPerVector, int floatsPerElement) {
		mFloatsPerElement = floatsPerElement;
		if (otherElementsPerVector == floatsPerElement) {
			init(other);
		} else {
			init(other, otherElementsPerVector);
		}
		finalizeBuffer();
	}

	public final int getNbFloatsPerElement() {
		return mFloatsPerElement;
	}

	public final FloatBuffer getFloatBuffer() {
		return mFloatBuffer;
	}

	public final float[] getInternalBuffer() {
		return mInternalBuffer;
	}

	public final void init(int nbElements) {
		assert (mInternalBuffer == null);

		mInternalBuffer = new float[nbElements * mFloatsPerElement];
		mCurrentIndex = 0;
	}

	private final void init(float[] source) {
		assert ((mInternalBuffer == null) && (source != null));

		mInternalBuffer = new float[source.length];
		System.arraycopy(source, 0, mInternalBuffer, 0, source.length);
		mCurrentIndex = source.length; // EOF
	}
	private final void init(float[] source, int otherElementsPerVector) {
		assert ((mInternalBuffer == null) && (source != null));
		
		final int nbLines = source.length / otherElementsPerVector;
		mInternalBuffer = new float[nbLines * mFloatsPerElement];
		int srcPtr = 0;
		int dstPtr = 0;
		for(int i=0; i<nbLines; ++i) {
			System.arraycopy(source, srcPtr, mInternalBuffer, dstPtr, mFloatsPerElement);
			srcPtr += otherElementsPerVector;
			dstPtr += mFloatsPerElement;
		}
		mCurrentIndex = dstPtr; // EOF
	}
	
	public final void add(float a) {
		mInternalBuffer[mCurrentIndex++] = a;
	}

	public final void add(float a, float b) {
		mInternalBuffer[mCurrentIndex++] = a;
		mInternalBuffer[mCurrentIndex++] = b;
	}

	public final void add(float a, float b, float c) {
		mInternalBuffer[mCurrentIndex++] = a;
		mInternalBuffer[mCurrentIndex++] = b;
		mInternalBuffer[mCurrentIndex++] = c;
	}

	public final void add(float a, float b, float c, float d) {
		mInternalBuffer[mCurrentIndex++] = a;
		mInternalBuffer[mCurrentIndex++] = b;
		mInternalBuffer[mCurrentIndex++] = c;
		mInternalBuffer[mCurrentIndex++] = d;
	}

	public final void finalizeBuffer() {
		mFloatBuffer = ByteBuffer.allocateDirect(mCurrentIndex * Display.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFloatBuffer.put(mInternalBuffer).position(0);
	}

	public final int getNbElements() {
		return mCurrentIndex / mFloatsPerElement;
	}

	public final void destroyFloatBuffer() {
		mFloatBuffer.clear();
	}

}
