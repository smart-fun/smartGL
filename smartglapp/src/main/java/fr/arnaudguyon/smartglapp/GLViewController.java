package fr.arnaudguyon.smartglapp;

import android.content.Context;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassObject3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassSprite;
import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.SmartGLViewController;
import fr.arnaudguyon.smartgl.opengl.Sprite;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.tools.WavefrontModel;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

/**
 * Created by arnaud on 19/11/2016.
 */

public class GLViewController implements SmartGLViewController {

    private Sprite mSprite;
    private Object3D mObject3D;
    private float mRandomSpeed;
    private float mSpeedX = 200;
    private float mSpeedY = 200;

    private Texture mSpriteTexture;
    private Texture mObjectTexture;

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

        mObjectTexture = new Texture(context, R.drawable.coloredbg);

//        ObjectReader reader = new ObjectReader();
//        ArrayList<Object3D> loadedObjects = reader.readRawResource(context, R.raw.bus, mSpriteTexture);
//        for(Object3D object3D : loadedObjects) {
//            object3D.setPos(0, 0, -50);
//            object3D.setScale(0.1f, 0.1f, 0.1f);
//            renderPassObject3D.addObject(object3D);
//            mLastLoadedObject = object3D;
//        }


//        // CUBE (6 faces before triangle strip optim. 4 optimized)
//        loader.loadObject(context, R.raw.crate);
//        mObject3D = loader.toObject3D();
//        for(Face3D face3D : mObject3D.getFaces()) {
//            face3D.setTexture(mObjectTexture);
//        }
//        mObject3D.setPos(0, 0, -5);
//        renderPassObject3D.addObject(mObject3D);

        // SPACESHIP (116 faces before triangle strip optim. 65 optimized)
        WavefrontModel model = new WavefrontModel.Builder(context, R.raw.spaceship)
                .optimize(true)
                .addTexture("Base", mObjectTexture)
                .addTexture("Black", mSpriteTexture)
                .create();
        mObject3D = model.toObject3D();
        mObject3D.setPos(0, 0, -8);// TODO: put in Builder + rotation and scale?
        renderPassObject3D.addObject(mObject3D);

//        // CAR (? faces before triangle strip optim. ? optimized)
//        Texture carTexture = new Texture(context, R.drawable.car);
//        WavefrontModel model = new WavefrontModel.Builder(context, R.raw.car)
//                .optimize(true)
//                .addTexture("3d.svg_auv", carTexture)
//                .create();
//        mObject3D = model.toObject3D();
//        mObject3D.setPos(0, 0, -8);// TODO: put in Builder + rotation and scale?
//        renderPassObject3D.addObject(mObject3D);

//        // BUS (2794 faces before triangle strip optim. 2632 optimized)
//        loader.loadObject(context, R.raw.bus);
//        mObject3D = loader.toObject3D();
//        for(Face3D face3D : mObject3D.getFaces()) {
//            face3D.setTexture(mObjectTexture);
//        }
//        mObject3D.setScale(0.1f, 0.1f, 0.1f);
//        mObject3D.setPos(0, 0, -50);
//        renderPassObject3D.addObject(mObject3D);
    }

    @Override
    public void onReleaseView(SmartGLView smartGLView) {
        if (mSpriteTexture != null) {
            mSpriteTexture.release();
        }
        if (mObjectTexture != null) {
            mObjectTexture.release();
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
            float rx = mObject3D.getRotX() + 50*frameDuration;
            float ry = mObject3D.getRotY() + 37*frameDuration;
            float rz = mObject3D.getRotZ() + 26*frameDuration;
            mObject3D.setRotation(rx, ry, rz);
        }

    }

    @Override
    public void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent event) {
    }
}
