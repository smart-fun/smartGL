package fr.arnaudguyon.smartgl.opengl;

import android.graphics.Bitmap;
import android.opengl.Matrix;
import android.util.SparseArray;

import java.util.ArrayList;

import fr.arnaudguyon.smartgl.touch.SpriteTouchListener;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

public class Sprite extends RenderObject {

	private float mPosX, mPosY;
    private float mPivotX, mPivotY;
	private float mRotAngle;
	protected float mWidth, mHeight;
    private float mScaleX = 1;
    private float mScaleY = 1;
	private SpriteTouchListener mTouchListener;
    private SparseArray<ArrayList<Collision>> mCollisions;  // Type of Collision -> list of Collision
    private int mDisplayPriority = 1;   // used to sort sprites, 1 is higher priority than 2

	public boolean handlesInput() {
		return (mTouchListener != null);
	}

	public boolean onTouch(TouchHelperEvent event, float frameDuration, Sprite currentTouchedSprite) {

		TouchHelperEvent.TouchEventType action = event.getType();
		float x = event.getX(0);
		float y = event.getY(0);
		if (action == TouchHelperEvent.TouchEventType.SINGLETOUCH) {
			if (currentTouchedSprite == null) {
				if (touchedBy(x, y)) {
					if (mTouchListener != null) {
						return mTouchListener.onTouch(this, frameDuration, event);
					} else {
						return true;
					}
				}
			}
		} else if ((action == TouchHelperEvent.TouchEventType.SINGLEMOVE) || (action == TouchHelperEvent.TouchEventType.LONGPRESS) || (action == TouchHelperEvent.TouchEventType.TAPPING)) {
			if (currentTouchedSprite == this) {
				if (mTouchListener != null) {
					return mTouchListener.onTouch(this, frameDuration, event);
				} else {
					return true;
				}
			}
		} else if (action == TouchHelperEvent.TouchEventType.SINGLEUNTOUCH) {
			if (currentTouchedSprite == this) {
				if (mTouchListener != null) {
					return mTouchListener.onTouch(this, frameDuration, event);
				} else {
					return false;
				}
			}
		}
		return false;
	}

	final public boolean touchedBy(float x, float y) {
		return (x >= getPosX() && x < getPosX() + getWidth() && y >= getPosY() && y < getPosY() + getHeight());
	}

	private Sprite() {
		super(false);
	}

	public Sprite( int width, int height) {
		this();
		mWidth = width;
		mHeight = height;
		mPosX = mPosY = 0;
		mRotAngle = 0;
		createFace(width, height);
	}
	
	public Sprite( int width, int height, Bitmap bitmap) {
		this(width, height);
		Texture texture = new Texture(bitmap);
		setTexture(texture);
	}
	
	public void registerTouchListener(SpriteTouchListener listener) {
		mTouchListener = listener;
	}
	public void unregisterTouchListener() {
		mTouchListener = null;
	}
	
	public void setTexture(Texture texture) {
		getFace().setTexture(texture);
	}

    public void setScale(float scaleX, float scaleY) {
        mScaleX = scaleX;
        mScaleY = scaleY;
		invalidMatrix();
    }
    public float getScaleX() {
        return mScaleX;
    }
    public float getScaleY() {
        return mScaleY;
    }
	protected void createFace(/*Context context,*/ int width, int height) {

		VertexList vertexList = createDefaultVertexList(width, height);
		UVList uvList = createDefaultUVList();

		Face3D face = new Face3D();
		face.setVertexList(vertexList);
		face.setUVList(uvList);
		face.setTexture(null);
		addFace(face);
	}
	
	protected VertexList createDefaultVertexList(int width, int height) {
		VertexList vertexList = new VertexList();
		vertexList.init(4);
		vertexList.add(0, 0, 0);
		vertexList.add(width, 0, 0);
		vertexList.add(0, height, 0);
		vertexList.add(width, height, 0);
		vertexList.finalizeBuffer();
		return vertexList;
	}
	
	protected UVList createDefaultUVList() {
		UVList uvList = new UVList();
		uvList.init(4);
		uvList.add(0, 0);
		uvList.add(1, 0);
		uvList.add(0, 1);
		uvList.add(1, 1);
		uvList.finalizeBuffer();
		return uvList;
	}

    // [0;1]
    final public void setPivot(float xPercent, float yPercent) {
        mPivotX = Math.max(0, xPercent);
        mPivotX = Math.min(mPivotX, 1);

        mPivotY = Math.max(0, yPercent);
        mPivotY = Math.min(mPivotY, 1);
        invalidMatrix();
    }
    public final float getPivotX() {
        return mPivotX;
    }
    public final float getPivotY() {
        return mPivotY;
    }

	final public void setPos(float x, float y) {
        if ((x != mPosX) || (y != mPosY)) {
            mPosX = x;
            mPosY = y;
            invalidMatrix();
		}
	}

    final public void setDisplayPriority(int distanceZ) {
        mDisplayPriority = distanceZ;
    }
    final public int getDisplayPriority() {
        return mDisplayPriority;
    }
	
	final public float moveX(float dx) {
        if (dx != 0) {
            mPosX += dx;
            invalidMatrix();
        }
		return mPosX;
	}
	final public float moveY(float dy) {
        if (dy != 0) {
            mPosY += dy;
            invalidMatrix();
        }
		return mPosY;
	}

	final public float getPosX() {
		return mPosX;
	}

	final public float getPosY() {
		return mPosY;
	}

	final public Face3D getFace() {
		return getFaces().firstElement();
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}
	
	final public void setRotation(float angle) {
        float newValue = (angle % 360f);
        if (newValue != mRotAngle) {
            mRotAngle = newValue;
            invalidMatrix();
        }
	}
	final public void rotate(float angle) {
        float newValue = (mRotAngle + angle) % 360f;
        if (newValue != mRotAngle) {
            mRotAngle = (mRotAngle + angle) % 360f;
            invalidMatrix();
        }
	}
	final public float getRotation() {
		return mRotAngle;
	}

	@Override
	final public void computeMatrix(float[] matrix) {
		Matrix.setIdentityM(matrix, 0);
        final float shiftX = mPivotX * mWidth * mScaleX;
        final float shiftY = mPivotY * mHeight * mScaleY;
		if (mRotAngle != 0) {
            Matrix.translateM(matrix, 0, mPosX, mPosY, 0);
			Matrix.rotateM(matrix, 0, mRotAngle, 0, 0, 1);
			Matrix.translateM(matrix, 0, -shiftX, -shiftY, 0);
		} else {
			Matrix.translateM(matrix, 0, mPosX - shiftX, mPosY - shiftY, 0);
		}
        if ((mScaleX != 1) || (mScaleY != 1)) {
            Matrix.scaleM(matrix, 0, mScaleX, mScaleY, 1);
        }
	}

	public void resetUVs() {
		rebindUVs(0, 1, 0, 1, 1, 1);
	}
	
	public void flipMappingH() {
		int w = (int) getWidth();
		int h = (int) getHeight();
		rebindUVs(w, 0, 0, h, w, h);
	}
    public void unflipMappingH() {
        int w = (int) getWidth();
        int h = (int) getHeight();
        rebindUVs(0, w, 0, h, w, h);
    }

    public void flipVertexH() {
        VertexList vertexList = getFace().getVertexList();
        float[] internalBuffer = vertexList.getInternalBuffer();

        internalBuffer[0] = mWidth - internalBuffer[0];
        internalBuffer[3] = mWidth - internalBuffer[3];
        internalBuffer[6] = mWidth - internalBuffer[6];
        internalBuffer[9] = mWidth - internalBuffer[9];

        vertexList.getFloatBuffer().put(internalBuffer).position(0);
    }

	public void rebindUVs(int xMin, int xMax, int yMin, int yMax, float texWidth, float texHeight) {

		Face3D face = getFace();
		if (face != null) {
			float uMin = xMin / texWidth;
			float uMax = xMax / texWidth;
			float vMin = yMin / texHeight;
			float vMax = yMax / texHeight;

			UVList uvList = face.getUVList();
			float[] internal = uvList.getInternalBuffer();
			int index = 0;

			internal[index++] = uMin;
			internal[index++] = vMin;

			internal[index++] = uMax;
			internal[index++] = vMin;

			internal[index++] = uMin;
			internal[index++] = vMax;

			internal[index++] = uMax;
			internal[index++] = vMax;

			uvList.getFloatBuffer().put(internal).position(0);
		}
	}

	public void resize(int width, int height) {
		if ((width == mWidth) && (height == mHeight)) {
			return;
		}
		mWidth = width;
		mHeight = height;
		VertexList vertexList = getFace().getVertexList();

		float[] internalBuffer = vertexList.getInternalBuffer();
		//internalBuffer[0] = 0;
		//internalBuffer[1] = 0;
		//internalBuffer[2] = 0;

		internalBuffer[3] = width;
		//internalBuffer[4] = 0;
		//internalBuffer[5] = 0;

		//internalBuffer[6] = 0;
		internalBuffer[7] = height;
		//internalBuffer[8] = 0;

		internalBuffer[9] = width;
		internalBuffer[10] = height;
		//internalBuffer[11] = 0;

		vertexList.getFloatBuffer().put(internalBuffer).position(0);
        invalidMatrix();
	}

	@Override
	public void releaseResources() {
		super.releaseResources();
        mCollisions = null;
        unregisterTouchListener();
	}

    // TODO: later use localToGlobal to detect with rotation / scale
	public boolean intersectWith(Sprite other) {
		// too much on the left of other
		if (mPosX + mWidth < other.mPosX) {
			return false;
		}
		// too much on the right of other
		if (mPosX >= other.mPosX + other.mWidth) {
			return false;
		}
		// higher
		if (mPosY + mHeight < other.mPosY) {
			return false;
		}
		// lower
        return mPosY < other.mPosY + other.mHeight;
    }

    public void addCollision(int type, Collision collision) {
        if (mCollisions == null) {
            mCollisions = new SparseArray<>();
        }
        ArrayList<Collision> collisionsForType = mCollisions.get(type);
        if (collisionsForType == null) {
            collisionsForType = new ArrayList<>();
            mCollisions.put(type, collisionsForType);
        }
        collisionsForType.add(collision);
    }
    public ArrayList<Collision> getCollisions(int type) {
        if (mCollisions != null) {
            return mCollisions.get(type);
        } else {
            return null;
        }
    }
    public SparseArray<ArrayList<Collision>> getAllCollisions() {
        return mCollisions;
    }

}
