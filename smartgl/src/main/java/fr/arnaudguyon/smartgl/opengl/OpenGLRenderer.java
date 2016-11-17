package fr.arnaudguyon.smartgl.opengl;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.SparseArray;

import junit.framework.Assert;

import fr.arnaudguyon.smartgl.R;
import fr.arnaudguyon.smartgl.math.Vector2D;

public abstract class OpenGLRenderer implements GLSurfaceView.Renderer {

	private WeakReference<OpenGLView> mOpenGLView;
	private int mWidth, mHeight;

	private long mPreviousTime = 0;
	private long mFrameDuration = 0;

	private float[] mProj3DMatrix = new float[16];
	private float[] mProj2DMatrix = new float[16];
	private float[] mTmpMatrix = new float[16];

	private Vector<RenderPass> mRenderPasses;
	private boolean mInitDone;
	private float[] mClearColor = {0.2f, 0.5f, 0.7f, 1};	// RGBA
	
	private Shader mPreviousShader = null;
	private boolean mUseTexture = false;
	private boolean mUseColor = false;
	private int mVertexAttribId = -1;
	private int mUvAttribId = -1;
	private int mColorAttribId = -1;
	private int mProjMatrixId = -1;

    // DEBUG
    private boolean mDebugMode = false;
    private Sprite mColCircleSprite;
    private Sprite mColSquareSprite;
	private Sprite mColSegmentSprite;

	public OpenGLRenderer(Context context) {
		super();
		mRenderPasses = new Vector<>();
//		mTouchHelper = new TouchHelper();
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}
	
	public float getFrameDuration() {
		return (mFrameDuration / 1000.f);
	}

	public float[] getProjection3DMatrix() {
		return mProj3DMatrix;
	}

	public float[] getProjection2DMatrix() {
		return mProj2DMatrix;
	}
	
	public void setClearColor(float r, float g, float b, float a) {
		mClearColor[0] = r;
		mClearColor[1] = g;
		mClearColor[2] = b;
		mClearColor[3] = a;
	}
	
	final void setListener(OpenGLView listener) {
		mOpenGLView = new WeakReference<>(listener);
	}
	final OpenGLView getListener() {
        if (mOpenGLView != null) {
            return mOpenGLView.get();
        } else {
            return null;
        }
    }

    public void setDebugMode(Context context) {
        loadDebugData(context);
        mDebugMode = true;
    }

	public void setRenderPasses(Vector<RenderPass> renderPasses) {
		synchronized (this) {
			mRenderPasses = renderPasses;
		}
	} // TODO: release lists inside programs when setting null
	
	public void addRenderPass(RenderPass renderPass) {
		synchronized (this) {
			mRenderPasses.add(renderPass);
		}
	}
	
	public void removeRenderPass(RenderPass renderPass) {
		synchronized (this) {
			mRenderPasses.remove(renderPass);
		}
	}

	protected void onPreRender(GL10 gl) {
	}

	protected void onPreRenderPass(GL10 gl, int passNumber) {
	}

	protected void onPostRenderPass(GL10 gl, int passNumber) {
	}

	protected void onPostRender(GL10 gl) {
	}

	@Override
	public void onDrawFrame(GL10 gl10) {

		computeFps();

		GLES20.glClearColor(mClearColor[0], mClearColor[1], mClearColor[2], mClearColor[3]);	// RGBA
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		synchronized (this) {
			
			if (!mInitDone) {
				return;
			}
			
			if (mOpenGLView != null) {
				OpenGLView view = mOpenGLView.get();
				if (view != null) {
					view.onPreRender(this);
				}
			}
			
			onPreRender(gl10);

			if (mRenderPasses != null) {

				int passNumber = 0;
				final int prgSize = mRenderPasses.size();
				for (int prgIt = 0; prgIt < prgSize; ++prgIt) {
					final RenderPass renderPass = mRenderPasses.get(prgIt);
					if (!renderPass.isLoaded()) {
						renderPass.load();
					}

					++passNumber;
					onPreRenderPass(gl10, passNumber);

					Vector<RenderObject> mainObjectList = renderPass.getRenderObjects();
					//Vector<RenderObject> objectList = buildObjectList(mainObjectList);	// recursive with Container Objects

					if (renderPass.useZBuffer()) {
						gl10.glEnable(GL10.GL_DEPTH_TEST);
						if (renderPass.clearZBuffer()) {
							GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
						}
					} else {
						gl10.glDisable(GL10.GL_DEPTH_TEST);
					}

					mPreviousShader = null;
					mUseTexture = false;
					mUseColor = false;
					mVertexAttribId = -1;
					mUvAttribId = -1;
					mColorAttribId = -1;
					mProjMatrixId = -1;
					
					// OBJECTS
					for (int renderObjIt = 0; renderObjIt < mainObjectList.size(); ++renderObjIt) {
						RenderObject object = mainObjectList.get(renderObjIt);
						boolean render = object.shouldDisplay(this);
						if (object.isContainer()) {
							renderContainer(renderPass, (RenderObjectContainer) object, render);
						} else {
							renderObject(renderPass, object, render);
						}
						
					}

					onPostRenderPass(gl10, passNumber);
				}
			}
			//handleTouchEvents();
			onPostRender(gl10);
		}
	}
	
	private void renderContainer(final RenderPass renderPass, RenderObjectContainer container, boolean render) {
		container.tick(this);
		if (render) {
			container.onPreRenderObject(this);
		}
		Vector<RenderObject> objects = container.getRenderObjects();
		if (objects != null) {
			for(int i=0; i<objects.size(); ++i) {
				RenderObject object = objects.get(i);
				if (object.isContainer()) {
					renderContainer(renderPass, (RenderObjectContainer) object, render && object.shouldDisplay(this));
				} else {
					renderObject(renderPass, object, render && object.shouldDisplay(this));
				}
			}
		}
	}
	
	private void renderObject(final RenderPass renderPass, RenderObject object, boolean render) {
		
		object.tick(this);
		if (!render) {
			return;
		}
		object.onPreRenderObject(this);
		
		float[] modelViewMatrix = object.getMatrix(); // get transformation matrix from object
		Vector<Face3D> faces = object.getFaces();

		final int faceSize = faces.size();
		for (int faceIt = 0; faceIt < faceSize; ++faceIt) {
			Face3D face = faces.get(faceIt);
			if (!face.shouldDisplay(this)) {
				continue;
			}
			int programIndex = face.getProgramIndex(object);
			GLES20.glUseProgram(renderPass.getProgramId(programIndex));

			// Check for new Shader
			Shader shader = renderPass.getShader(programIndex);
			if (shader != mPreviousShader) {
				mPreviousShader = shader;
				mUseTexture = shader.useTexture();
				mUseColor = shader.useColor();
				mVertexAttribId = shader.getVertexAttribId();
				mUvAttribId = shader.getUVAttribId();
				mColorAttribId = shader.getColorAttribId();
				mProjMatrixId = shader.getProjMatrixId();

				GLES20.glEnableVertexAttribArray(mVertexAttribId);
				if (mUseTexture) {
					GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
					GLES20.glEnableVertexAttribArray(mUvAttribId);

					GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
					//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);	// premultiply alpha
					GLES20.glEnable(GLES20.GL_BLEND);
				}
				if (mUseColor) {
					GLES20.glEnableVertexAttribArray(mColorAttribId);
				}
			}
			shader.onPreRender(object);

			// Vertex
			VertexList vertexList = face.getVertexList();
			FloatBuffer vertexBuffer = vertexList.getFloatBuffer();
			vertexBuffer.position(0);
			GLES20.glVertexAttribPointer(mVertexAttribId, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

			// Texture
			if (mUseTexture) {
				Texture tex = face.getTexture();
				if (tex == null) { // not ready yet
					continue;
				}
				if (!tex.isBinded()) {
					tex.bindTexture();
				}
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getId());
				// UVs
				UVList uvList = face.getUVList();
				FloatBuffer uvBuffer = (uvList != null) ? uvList.getFloatBuffer() : null;
				if (uvBuffer == null) {
					continue;
				}
				uvBuffer.position(0);
				GLES20.glVertexAttribPointer(mUvAttribId, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);
			}

			// Colors
			if (mUseColor) {
				ColorList colorList = face.getColorList();
				FloatBuffer colorBuffer = (colorList != null) ? colorList.getFloatBuffer() : null;
				if (colorBuffer == null) {
					continue;
				}
				colorBuffer.position(0);
				GLES20.glVertexAttribPointer(mColorAttribId, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
			}
			
			object.onPreRenderFace(this, shader, face);
			face.onPreRenderFace(this, object, shader);

			// Transformations
			if (object.is3D()) {
				Matrix.multiplyMM(mTmpMatrix, 0, mProj3DMatrix, 0, modelViewMatrix, 0);
			} else {
				Matrix.multiplyMM(mTmpMatrix, 0, mProj2DMatrix, 0, modelViewMatrix, 0);
			}
			GLES20.glUniformMatrix4fv(mProjMatrixId, 1, false, mTmpMatrix, 0);

			// Render
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexList.getNbElements());
		}

        if (mDebugMode) {
            if (object instanceof Sprite) {
                Sprite sprite = (Sprite) object;
                SparseArray<ArrayList<Collision>> collisions = sprite.getAllCollisions();
                if (collisions != null) {
                    for(int i=0; i<collisions.size(); ++i) {
                        ArrayList<Collision> colList = collisions.valueAt(i);
                        if (colList != null) {
                            for(int iCol=0; iCol<colList.size(); ++iCol) {
                                Collision collision = colList.get(iCol);
                                if (!collision.displayAtScreen()) {
                                    continue;
                                }
								collision.computePositionAndSize();
                                if (collision instanceof CollisionCircle) {
                                    CollisionCircle circle = (CollisionCircle) collision;
                                    float posX = circle.getPosX();
                                    float posY = circle.getPosY();
                                    int circleSize = (int) circle.getRadius() * 2;
                                    mColCircleSprite.setPos(posX, posY);
                                    mColCircleSprite.resize(circleSize, circleSize);
                                    renderObject(renderPass, mColCircleSprite, true);
                                } else if (collision instanceof CollisionRectangle) {
									CollisionRectangle rectangleCol = (CollisionRectangle) collision;
									float posX = rectangleCol.getLeft();
									float posY = rectangleCol.getTop();
									mColSquareSprite.setPos(posX, posY);
									mColSquareSprite.resize((int) (rectangleCol.getRight() - posX), (int) (rectangleCol.getBottom() - posY));
									renderObject(renderPass, mColSquareSprite, true);
								} else if (collision instanceof CollisionSegments) {
									CollisionSegments colSeg = (CollisionSegments) collision;
									ArrayList<Vector2D> segments = colSeg.getSegments();
									for(Vector2D vector : segments) {
										float middleX = (vector.getXStart() + vector.getXStop()) / 2;
										float middleY = (vector.getYStart() + vector.getYStop()) / 2;
										mColSegmentSprite.setPos(middleX, middleY);
										float width = vector.getSize();
										float height = width / 32f;
										if (width < 1) {
											width = 1;
										}
										if (height < 1) {
											height = 1;
										}
										mColSegmentSprite.resize((int)width, (int)height);
										float angle = vector.computeAngle() * 180 / 3.14159f;
										mColSegmentSprite.setRotation(angle);
										renderObject(renderPass, mColSegmentSprite, true);
									}

                                } else {
                                    Assert.assertTrue(false);   // not handled yet (and probably never)
                                }
                            }
                        }
                    }
                }
            }
        }

	}

	// Called from OpenGL Thread by Android
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {

        mPreviousTime = 0;
        // Prevent from calling onAcquireResources twice, because onSurfaceChanged is often called twice
        if (mInitDone && (mWidth==width) && (mHeight==height)) {
            return;
        }

        final boolean justResizing = mInitDone;

		GLES20.glViewport(0, 0, width, height);

		mWidth = width;
		mHeight = height;

		computeProjMatrix2D(mProj2DMatrix);
		computeProjMatrix3D(mProj3DMatrix);

        OpenGLView view = getListener();
        // Viewport has changed its size, release resources before acquiring
        if (justResizing) {
            if (view != null) {
				view.onViewResized(width, height);
            }
        }
		mInitDone = true;
	}
	
	protected void computeProjMatrix2D(float [] matrix2D) {
		Matrix.orthoM(matrix2D, 0, 0f, mWidth, mHeight, 0, -1f, 1f);
	}

	protected void computeProjMatrix3D(float [] matrix3D) {
		float ratio = (float) mWidth / (float) mHeight;
		float near = 0.1f;
		Matrix.frustumM(matrix3D, 0, -near, near, -near / ratio, near / ratio, near, 100);	// TODO: Far 100 is hardcoded
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mPreviousTime = 0;

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		//TODO: set it as option of Renderer 
		//gl.glEnable(GL10.GL_CULL_FACE);
		//gl.glCullFace(GL10.GL_FRONT);

	}

	private void computeFps() {
		long newTime = SystemClock.uptimeMillis();
		if (mPreviousTime == 0) {
			mFrameDuration = 20;	// consider 50 fps at start
			mPreviousTime = newTime - mFrameDuration;
		} else {
			mFrameDuration = newTime - mPreviousTime;
			mPreviousTime = newTime;
		}
	}
	
	public Vector<Sprite> getToucheableSprites() {
		Vector<Sprite> result = null;
		if (mRenderPasses != null) {
			result = new Vector<>();
			for(RenderPass renderPass : mRenderPasses) {
				Vector<RenderObject> objects = renderPass.getRenderObjects();
				if (objects != null) {
					for(RenderObject object : objects) {
						if (object.isContainer()) {
							RenderObjectContainer container = (RenderObjectContainer) object;
							addToucheableSprites(container, result);
						} else if (object instanceof Sprite) {
							Sprite sprite = (Sprite) object;
							if (sprite.handlesInput()) {
								result.add(sprite);
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private void addToucheableSprites(RenderObjectContainer container, Vector<Sprite> result) {
		Vector<RenderObject> objects = container.getRenderObjects();
		if ((objects != null) && (!objects.isEmpty())) {
			for(RenderObject object : objects) {
				if (object.isContainer()) {
					RenderObjectContainer subContainer = (RenderObjectContainer) object;
					addToucheableSprites(subContainer, result);
				} else if (object instanceof Sprite) {
					Sprite sprite = (Sprite) object;
					if (sprite.handlesInput()) {
						result.add(sprite);
					}
				}
			}
		}
	}
	
//	public void onTouchEvent(View view, MotionEvent event) {
//		mTouchHelper.onTouchEvent(view, event);
//	}
//	
//	private void handleTouchEvents() {
//
//		TouchHelperEvent event = null;
//
//		while ((event = mTouchHelper.getNextEvent()) != null) {
//			if (mRenderPasses != null) {
//				final int prgSize = mRenderPasses.size();
//				for (int prgIt = prgSize - 1; prgIt >= 0; --prgIt) { // backward
//					final RenderPass renderPass = mRenderPasses.get(prgIt);
//					Vector<RenderObject> mainObjectList = renderPass.getRenderObjects();
//					Vector<RenderObject> objectList = buildObjectList(mainObjectList);
//					for (int renderObjIt = objectList.size() - 1; renderObjIt >= 0; --renderObjIt) { // backward
//						RenderObject object = objectList.get(renderObjIt);
//						if (!object.is3D()) {
//							Sprite sprite = (Sprite) object;
//							if (sprite.handlesInput()) {
//								boolean handled = sprite.onTouch(event, mCurrentTouchedSprite);
//								if (handled) {
//									mCurrentTouchedSprite = sprite; // get or continue to use
//								} else if (mCurrentTouchedSprite == sprite) {
//									mCurrentTouchedSprite = null; // stopped to use
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}


    void onPause() {
        synchronized (this) {
            mInitDone = false;
            mRenderPasses.clear();
        }
    }
	void onResume() {

	}

    private void loadDebugData(Context context) {
        Texture colCircleTexture = new Texture(context, R.drawable.col_circle);
        mColCircleSprite = new Sprite(32,32);
        mColCircleSprite.setPivot(0.5f, 0.5f);
        mColCircleSprite.setTexture(colCircleTexture);

		Texture colSquareTexture = new Texture(context, R.drawable.col_square);
        mColSquareSprite = new Sprite(32,32);
        mColSquareSprite.setPivot(0, 0);
        mColSquareSprite.setTexture(colSquareTexture);


		Texture colSegmentTexture = new Texture(context, R.drawable.col_segment);
		mColSegmentSprite = new Sprite(32,32);
		mColSegmentSprite.setPivot(0.5f, 0.5f);
		mColSegmentSprite.setTexture(colSegmentTexture);
    }

}
