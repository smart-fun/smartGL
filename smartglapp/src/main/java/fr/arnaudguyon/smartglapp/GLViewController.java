package fr.arnaudguyon.smartglapp;

import android.content.Context;

import java.util.ArrayList;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassObject3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassSprite;
import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.SmartGLViewController;
import fr.arnaudguyon.smartgl.opengl.Sprite;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;
import fr.arnaudguyon.smartgl.tools.ObjectReader;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

/**
 * Created by arnaud on 19/11/2016.
 */

public class GLViewController implements SmartGLViewController {

    private Sprite mSprite;
    private Object3D mObject3D;
    private Object3D mLastLoadedObject;
    private float mRandomSpeed;
    private float mSpeedX = 200;
    private float mSpeedY = 200;

    private Texture mSpriteTexture;

    public GLViewController() {
        mRandomSpeed = (float) ((Math.random() * 50) + 100);
        if (Math.random() > 0.5f) {
            mRandomSpeed *= -1;
        }
    }

    @Override
    public void onPrepareView(SmartGLView smartGLView) {

        Context context = smartGLView.getContext();

        // Add RenderPass for Sprites & Object3D
        SmartGLRenderer renderer = smartGLView.getSmartGLRenderer();
        RenderPassObject3D renderPassObject3D = new RenderPassObject3D();
        RenderPassSprite renderPassSprite = new RenderPassSprite();
        renderer.addRenderPass(renderPassObject3D);
        renderer.addRenderPass(renderPassSprite);

        mSprite = new Sprite(120, 120);
        mSprite.setPivot(0.5f, 0.5f);
        mSprite.setPos(60, 60);
        mSpriteTexture = new Texture(context, R.drawable.planet);
        mSprite.setTexture(mSpriteTexture);
        mSprite.setDisplayPriority(20);
        renderPassSprite.addSprite(mSprite);

//        Sprite sprite2= new Sprite(200, 200);
//        sprite2.setPos(200, 200);
//        sprite2.setDisplayPriority(19);
//        sprite2.setTexture(new Texture(context, R.drawable.planet));
//        renderPassSprite.addSprite(sprite2);

        mObject3D = new Object3D();
        Face3D face = new Face3D();
        face.setTexture(new Texture(context, R.drawable.door));
        UVList uvList = new UVList();
        uvList.init(4);
        uvList.add(0,1);
        uvList.add(1,1);
        uvList.add(0,0);
        uvList.add(1,0);
        uvList.finalizeBuffer();
        face.setUVList(uvList);   // TODO: create SquareUV for simple mapping (or load object)

        VertexList vertexList = new VertexList();
        vertexList.init(4);
        float z = 0;
        vertexList.add(-2,0,z);
        vertexList.add(+2,0,z);
        vertexList.add(-2,8,z);
        vertexList.add(+2,8,z);
        vertexList.finalizeBuffer();
        face.setVertexList(vertexList);   // TODO: load object or provide list

        mObject3D.addFace(face);
        mObject3D.setPos(0, -4, -14);   // ! clip after Z = -100

        renderPassObject3D.addObject(mObject3D);

        ObjectReader reader = new ObjectReader();
        ArrayList<Object3D> loadedObjects = reader.readRawResource(context, R.raw.bus, mSpriteTexture);
        for(Object3D object3D : loadedObjects) {
            object3D.setPos(0, 0, -50);
            object3D.setScale(0.1f, 0.1f, 0.1f);
            renderPassObject3D.addObject(object3D);
            mLastLoadedObject = object3D;
        }

    }

    @Override
    public void onReleaseView(SmartGLView smartGLView) {
        if (mSpriteTexture != null) {
            mSpriteTexture.release();
        }
    }

    @Override
    public void onResizeView(SmartGLView smartGLView) {
//        onReleaseView(smartGLView);
//        onPrepareView(smartGLView);
    }

    @Override
    public void onTick(SmartGLView smartGLView) {
        SmartGLRenderer renderer = smartGLView.getSmartGLRenderer();
        float frameDuration = renderer.getFrameDuration();
        if (mSprite != null) {

//            float newX = mSprite.getPosX() + (frameDuration * 100);
//            float newY = mSprite.getPosY();
//            if (newX > 600) {
//                newX = 0;
//            }
//            mSprite.setPos(newX, newY);
//
//            float newRot = mSprite.getRotation() + (frameDuration * 100);
//            mSprite.setRotation(newRot);
//
            float angle = mSprite.getRotation() + frameDuration*mRandomSpeed;
            mSprite.setRotation(angle);

            float x = mSprite.getPosX() + frameDuration*mSpeedX;
            float y = mSprite.getPosY() + frameDuration*mSpeedY;
            if (x < mSprite.getWidth()/2) {
                x = mSprite.getWidth()/2;
                mSpeedX = -mSpeedX;
                if (mSpeedY > 0) {
                    mRandomSpeed = Math.abs(mRandomSpeed);
                } else {
                    mRandomSpeed = -Math.abs(mRandomSpeed);
                }
            } else if (x + mSprite.getWidth()/2 >= smartGLView.getWidth()) {
                x = smartGLView.getWidth() - mSprite.getWidth()/2;
                mSpeedX = -mSpeedX;
                if (mSpeedY > 0) {
                    mRandomSpeed = -Math.abs(mRandomSpeed);
                } else {
                    mRandomSpeed = Math.abs(mRandomSpeed);
                }
            }
            if (y < mSprite.getHeight()/2) {
                y = mSprite.getHeight()/2;
                mSpeedY = -mSpeedY;
                if (mSpeedX > 0) {
                    mRandomSpeed = -Math.abs(mRandomSpeed);
                } else {
                    mRandomSpeed = Math.abs(mRandomSpeed);
                }
            } else if (y + mSprite.getHeight()/2 >= smartGLView.getHeight()) {
                y = smartGLView.getHeight() - mSprite.getHeight()/2;
                mSpeedY = -mSpeedY;
                if (mSpeedX > 0) {
                    mRandomSpeed = Math.abs(mRandomSpeed);
                } else {
                    mRandomSpeed = -Math.abs(mRandomSpeed);
                }
            }
            mSprite.setPos(x, y);
        }

        if (mObject3D != null) {
            float angle = mObject3D.getRotY() + frameDuration*mRandomSpeed;
            mObject3D.setRotation(0, angle, 0);

//            float[] windowPos = new float[3];
//            float[] vertexPos = mObject3D.getFaces().get(0).getVertexList().getInternalBuffer();
//            boolean success = Tools.worldToScreen(renderer, vertexPos, windowPos);
//            if (success) {
//                // check position
//            }

        }

        if (mLastLoadedObject != null) {
            float rx = mLastLoadedObject.getRotX() + 100*frameDuration;
            float ry = mLastLoadedObject.getRotY() + 77*frameDuration;
            float rz = mLastLoadedObject.getRotZ() + 56*frameDuration;
            mLastLoadedObject.setRotation(rx, ry, rz);
        }

    }

    @Override
    public void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent event) {
    }
}
