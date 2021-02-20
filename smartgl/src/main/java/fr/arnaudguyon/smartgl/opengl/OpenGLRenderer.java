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

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import fr.arnaudguyon.smartgl.R;
import fr.arnaudguyon.smartgl.math.Vector2D;
import fr.arnaudguyon.smartgl.tools.Assert;

/**
 * Base class for the Renderer. Handles the list of the RenderObject to display, the camera.
 * Do the render.
 */

public abstract class OpenGLRenderer implements GLSurfaceView.Renderer {

    private WeakReference<OpenGLView> mOpenGLView;
    private int mWidth, mHeight;

    private long mPreviousTime = 0;
    private long mFrameDurationRaw = 0;
    private float mFrameDurationSmoothed = 0.02f;

    private float[] mProj3DMatrix = new float[16];
    private float[] mProj2DMatrix = new float[16];
    private float[] mTmpMatrix = new float[16];

    private Vector<RenderPass> mRenderPasses;
    private boolean mInitDone;
    private float[] mClearColor = {0.2f, 0.5f, 0.7f, 1};    // RGBA
    private OpenGLCamera mCamera;

    private Shader mPreviousShader = null;
    private boolean mUseTexture = false;
    private boolean mUseColor = false;
    private int mVertexAttribId = -1;
    private int mUvAttribId = -1;
    private int mColorAttribId = -1;
    private int mProjMatrixId = -1;

    private Boolean mDoubleSided = true;

    private static final float[] DEFAULT_AMBIANT_LIGHT = {1, 1, 1, 1};    // RVBA
    private LightAmbiant mLightAmbiant;
    private LightParallel mLightParallel;

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
        return mFrameDurationSmoothed;
    }

    public float getRawFrameDuration() {
        return mFrameDurationRaw;
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

    public void setCamera(OpenGLCamera camera) {
        mCamera = camera;
    }

    public OpenGLCamera getCamera() {
        return mCamera;
    }

    public void setDoubleSided(boolean doubleSided) {
        mDoubleSided = doubleSided;
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

    void clearRenderPasses() {
        synchronized (this) {
            mRenderPasses.clear();
        }
    }

    public void removeRenderPass(RenderPass renderPass) {
        synchronized (this) {
            mRenderPasses.remove(renderPass);
        }
    }

    public boolean removeObject(@NonNull RenderObject renderObject) {
        boolean removed = false;
        synchronized (this) {
            for(RenderPass renderPass: mRenderPasses) {
                removed |= renderPass.removeObject(renderObject);
            }
        }
        return removed;
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

        GLES20.glClearColor(mClearColor[0], mClearColor[1], mClearColor[2], mClearColor[3]);    // RGBA
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        checkDoubleSided(gl10);

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

            if ((mCamera != null) && mCamera.isDirty()) {
                computeProjMatrix3D(mProj3DMatrix);
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

                    renderPass.sortObjects();

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

            if (mScreenshotListener != null) {
                doTakeScreenshot(gl10);
            }
        }

    }

    private void renderContainer(final RenderPass renderPass, RenderObjectContainer container, boolean render) {
        container.tick(this);
        if (render) {
            container.onPreRenderObject(this);
        }
        Vector<RenderObject> objects = container.getRenderObjects();
        if (objects != null) {
            for (int i = 0; i < objects.size(); ++i) {
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
            GLES20.glUseProgram(renderPass.getProgramId());

            // Check for new Shader
            Shader shader = renderPass.getShader();
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
                    if (tex.bindTexture()) {
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getId());
                    }
                }
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
            shader.onPreRender(this, object, face);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexList.getNbElements());
        }

        if (mDebugMode) {
            if (object instanceof Sprite) {
                Sprite sprite = (Sprite) object;
                SparseArray<ArrayList<Collision>> collisions = sprite.getAllCollisions();
                if (collisions != null) {
                    for (int i = 0; i < collisions.size(); ++i) {
                        ArrayList<Collision> colList = collisions.valueAt(i);
                        if (colList != null) {
                            for (int iCol = 0; iCol < colList.size(); ++iCol) {
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
                                    for (Vector2D vector : segments) {
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
                                        mColSegmentSprite.resize((int) width, (int) height);
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
        // Prevent from calling acquireResources or onViewResized twice, because onSurfaceChanged is often called twice
        if (mInitDone && (mWidth == width) && (mHeight == height)) {
            return;
        }

        GLES20.glViewport(0, 0, width, height);

        mWidth = width;
        mHeight = height;

        computeProjMatrix2D(mProj2DMatrix);
        computeProjMatrix3D(mProj3DMatrix);

        OpenGLView view = getListener();
        if (view != null) {
            if (mInitDone) {
                view.onViewResized(width, height);
            } else {
                view.acquireResources();
            }
        }

        mInitDone = true;
    }

    private void computeProjMatrix2D(float[] matrix2D) {
        Matrix.orthoM(matrix2D, 0, 0f, mWidth, mHeight, 0, -1f, 1f);
    }

    private void computeProjMatrix3D(float[] matrix3D) {
//		float ratio = (float) mWidth / (float) mHeight;
//		float near = 0.1f;
//		Matrix.frustumM(matrix3D, 0, -near, near, -near / ratio, near / ratio, near, 100);

        if (mCamera == null) {
            return;
        }

        float FOV = mCamera.getFOV();
        final float near = mCamera.getNear();
        final float far = mCamera.getFar();
        float ratio = (float) getWidth() / (float) getHeight();
        Matrix.perspectiveM(matrix3D, 0, FOV, ratio, near, far);

        // Rotation
        float ox = -mCamera.getRotX();
        float oy = -mCamera.getRotY();
        float oz = -mCamera.getRotZ();
        if (ox != 0)
            Matrix.rotateM(matrix3D, 0, ox, 1, 0, 0);
        if (oy != 0)
            Matrix.rotateM(matrix3D, 0, oy, 0, 1, 0);
        if (oz != 0)
            Matrix.rotateM(matrix3D, 0, oz, 0, 0, 1);

        // Translation
        float x = -mCamera.getPosX();
        float y = -mCamera.getPosY();
        float z = -mCamera.getPosZ();
        Matrix.translateM(matrix3D, 0, x, y, z);

        mCamera.setDirty(false);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mPreviousTime = 0;

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        checkDoubleSided(gl);
    }

    private void checkDoubleSided(GL10 gl) {
        if (mDoubleSided != null) {
            if (mDoubleSided.booleanValue()) {
                gl.glDisable(GL10.GL_CULL_FACE);
            } else {
                gl.glEnable(GL10.GL_CULL_FACE);
                gl.glCullFace(GL10.GL_FRONT);
            }
            mDoubleSided = null;    // applied
        }
    }

    private void computeFps() {

        long newTime = SystemClock.uptimeMillis();
        if (mPreviousTime == 0) {    // First frame, set standard values at 50fps
            mFrameDurationRaw = 20;
            mPreviousTime = newTime - mFrameDurationRaw;
            mFrameDurationSmoothed = (mFrameDurationRaw / 1000.f);
        } else {    // Other frames, let's calculate the FPS
            mFrameDurationRaw = newTime - mPreviousTime;
            mPreviousTime = newTime;
            float instantFrameDurationSecond = (mFrameDurationRaw / 1000.f);
            // normal smooth for normal values
            if ((instantFrameDurationSecond >= 0.001f) && (instantFrameDurationSecond <= 0.5f)) {
                mFrameDurationSmoothed = (mFrameDurationSmoothed * 0.9f) + (instantFrameDurationSecond * 0.1f);
            } else {    // smooth more for extreme values (like starting freeze)
                mFrameDurationSmoothed = (mFrameDurationSmoothed * 0.95f) + (instantFrameDurationSecond * 0.05f);
            }
        }

    }

    public Vector<Sprite> getToucheableSprites() {
        Vector<Sprite> result = null;
        if (mRenderPasses != null) {
            result = new Vector<>();
            for (RenderPass renderPass : mRenderPasses) {
                Vector<RenderObject> objects = renderPass.getRenderObjects();
                if (objects != null) {
                    for (RenderObject object : objects) {
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
            for (RenderObject object : objects) {
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
//            mRenderPasses.clear();
        }
    }

    void onResume() {
    }

    public void setLightAmbiant(LightAmbiant lightAmbiant) {
        mLightAmbiant = lightAmbiant;
    }

    float[] getLightAmbiant() {
        LightAmbiant lightAmbiant = mLightAmbiant;    // avoid multithread issues
        if (lightAmbiant != null) {
            return lightAmbiant.getArray();
        } else {
            return DEFAULT_AMBIANT_LIGHT;
        }
    }

    public void setLightParallel(LightParallel lightParallel) {
        mLightParallel = lightParallel;
    }

    float[] getLightDirection() {
        if (mLightParallel != null) {
            return mLightParallel.getDirection().getArray();
        } else {
            Assert.assertTrue("getLightDirection: No Parallel Light defined", false);
            return null;
        }
    }

    float[] getLightColor() {
        if (mLightParallel != null) {
            return mLightParallel.getColor().getArray();
        } else {
            Assert.assertTrue("getLightColor: No Parallel Light defined", false);
            return null;
        }
    }

    // TODO: when create surface, unload/reload debug data (if debug mode)
    private void loadDebugData(Context context) {
        Texture colCircleTexture = new Texture(context, R.drawable.col_circle);
        mColCircleSprite = new Sprite(32, 32);
        mColCircleSprite.setPivot(0.5f, 0.5f);
        mColCircleSprite.setTexture(colCircleTexture);

        Texture colSquareTexture = new Texture(context, R.drawable.col_square);
        mColSquareSprite = new Sprite(32, 32);
        mColSquareSprite.setPivot(0, 0);
        mColSquareSprite.setTexture(colSquareTexture);


        Texture colSegmentTexture = new Texture(context, R.drawable.col_segment);
        mColSegmentSprite = new Sprite(32, 32);
        mColSegmentSprite.setPivot(0.5f, 0.5f);
        mColSegmentSprite.setTexture(colSegmentTexture);
    }

    // ********************** SCREENSHOT FEATURE **********************

    private OnTakeScreenshot mScreenshotListener;
    private Handler mScreenshotHandler;

    public void takeScreenshot(OnTakeScreenshot listener) {
        synchronized (this) {
            mScreenshotListener = listener;
            mScreenshotHandler = new Handler();
        }
    }

    public interface OnTakeScreenshot {
        void screenshotTaken(Bitmap bitmap);
    }

    private void doTakeScreenshot(GL10 gl) {

        if ((mScreenshotListener == null) || (mScreenshotHandler == null)) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int source[] = new int[width * height];
        int dest[] = new int[width * height];
        IntBuffer wrap = IntBuffer.wrap(source);
        wrap.position(0);
        gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, wrap);

        int raw;
        int color;
        int offsetSrc;
        int offsetDst;
        for (int y = 0; y < height; y++) {
            offsetSrc = y * width;
            offsetDst = (height - y - 1) * width;
            for (int x = 0; x < width; x++) {
                raw = source[offsetSrc++];  // ABGR to ARGB
                color = (raw & 0xFF00FF00) | ((raw & 0x00FF0000) >> 16) | ((raw & 0x000000FF) << 16);
                dest[offsetDst++] = color;
            }
        }

        final Bitmap bitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
        mScreenshotHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (mScreenshotListener != null) {
                        if (bitmap != null) {
                            mScreenshotListener.screenshotTaken(bitmap);
                        }
                        mScreenshotListener = null;
                    }
                }
            }
        });

    }
}
