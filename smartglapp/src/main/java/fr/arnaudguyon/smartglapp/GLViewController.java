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
package fr.arnaudguyon.smartglapp;

import android.content.Context;

import androidx.annotation.NonNull;

import fr.arnaudguyon.smartgl.math.Vector3D;
import fr.arnaudguyon.smartgl.opengl.LightParallel;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassObject3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassSprite;
import fr.arnaudguyon.smartgl.opengl.SmartColor;
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
    private float mRandomRotationSpeed;
    private float mSpeedX = 200;
    private float mSpeedY = 200;

    private Texture mSpriteTexture;
    private Texture mObjectTexture;
    private Texture mSpaceFrigateTexture;
    private Texture mSpaceCruiserTexture;

    private RenderPassObject3D mRenderPassObject3D;
    private RenderPassObject3D mRenderPassObject3DColor;
    private RenderPassSprite mRenderPassSprite;

    private Object3D mBus;
    private Object3D mCube;
    private Object3D mFrigate;
    private Object3D mCruiser;
    private Object3D mEarth;

    private Object3D mNextObject = null;
    private Object3D mNextObjectColor = null;

    public GLViewController() {
        mRandomRotationSpeed = (float) ((Math.random() * 50) + 100);
        if (Math.random() > 0.5f) {
            mRandomRotationSpeed *= -1;
        }
    }

    @Override
    public void onPrepareView(SmartGLView smartGLView) {

        Context context = smartGLView.getContext();

        // Add RenderPass for Sprites & Object3D
        SmartGLRenderer renderer = smartGLView.getSmartGLRenderer();
        mRenderPassObject3D = new RenderPassObject3D(RenderPassObject3D.ShaderType.SHADER_TEXTURE_LIGHTS, true, true);
        mRenderPassObject3DColor = new RenderPassObject3D(RenderPassObject3D.ShaderType.SHADER_COLOR_LIGHTS, true, false);
        mRenderPassSprite = new RenderPassSprite();
        renderer.addRenderPass(mRenderPassObject3D);
        renderer.addRenderPass(mRenderPassObject3DColor);
        renderer.addRenderPass(mRenderPassSprite);

        renderer.setDoubleSided(true);

        SmartColor lightColor = new SmartColor(1, 1, 1);
        Vector3D lightDirection = new Vector3D(0.2f, -1, -1);
        lightDirection.normalize();
        LightParallel lightParallel = new LightParallel(lightColor, lightDirection);
        renderer.setLightParallel(lightParallel);

        mSpriteTexture = new Texture(context, R.drawable.planet);
        mObjectTexture = new Texture(context, R.drawable.coloredbg);
        mSpaceFrigateTexture = new Texture(context, R.drawable.space_frigate_6_color);
        mSpaceCruiserTexture = new Texture(context, R.drawable.space_cruiser_4_color);


        mSprite = new Sprite(120, 120);
        mSprite.setPivot(0.5f, 0.5f);
        mSprite.setPos(60, 60);
        mSprite.setTexture(mSpriteTexture);
        mSprite.setDisplayPriority(20);
        mRenderPassSprite.addSprite(mSprite);

        mBus = loadBus(context);
        mCube = loadCube(context);
        mFrigate = loadFrigate(context);
        mCruiser = loadCruiser(context);
        mEarth = loadEarth(context);

        //renderer.setDoubleSided(true);

        switchToFrigate();
    }

    @Override
    public void onReleaseView(SmartGLView smartGLView) {
        if (mSpriteTexture != null) {
            mSpriteTexture.release();
            mSpriteTexture = null;
        }
        if (mObjectTexture != null) {
            mObjectTexture.release();
            mObjectTexture = null;
        }
        if (mSpaceFrigateTexture != null) {
            mSpaceFrigateTexture.release();
            mSpaceFrigateTexture = null;
        }
        if (mSpaceCruiserTexture != null) {
            mSpaceCruiserTexture.release();
            mSpaceCruiserTexture = null;
        }
    }

    @Override
    public void onResizeView(SmartGLView smartGLView) {
//        onReleaseView(smartGLView);
//        onPrepareView(smartGLView);
    }

    @Override
    public void onTick(SmartGLView smartGLView) {

        Object3D next = mNextObject;
        Object3D nextColor = mNextObjectColor;
        if (next != null) {
            dropAllObject3D();
            mRenderPassObject3D.addObject(mNextObject);
            mObject3D = mNextObject;
            mNextObject = null;
        } else if (nextColor != null) {
            dropAllObject3D();
            mRenderPassObject3DColor.addObject(mNextObjectColor);
            mObject3D = mNextObjectColor;
            mNextObjectColor = null;
        }

        SmartGLRenderer renderer = smartGLView.getSmartGLRenderer();
        float frameDuration = renderer.getFrameDuration();
        if (mSprite != null) {

            float angle = mSprite.getRotation() + frameDuration * mRandomRotationSpeed;
            mSprite.setRotation(angle);

            float x = mSprite.getPosX() + frameDuration * mSpeedX;
            float y = mSprite.getPosY() + frameDuration * mSpeedY;
            if (x < mSprite.getWidth() / 2) {
                x = mSprite.getWidth() / 2;
                mSpeedX = -mSpeedX;
                if (mSpeedY > 0) {
                    mRandomRotationSpeed = Math.abs(mRandomRotationSpeed);
                } else {
                    mRandomRotationSpeed = -Math.abs(mRandomRotationSpeed);
                }
            } else if (x + mSprite.getWidth() / 2 >= smartGLView.getWidth()) {
                x = smartGLView.getWidth() - mSprite.getWidth() / 2;
                mSpeedX = -mSpeedX;
                if (mSpeedY > 0) {
                    mRandomRotationSpeed = -Math.abs(mRandomRotationSpeed);
                } else {
                    mRandomRotationSpeed = Math.abs(mRandomRotationSpeed);
                }
            }
            if (y < mSprite.getHeight() / 2) {
                y = mSprite.getHeight() / 2;
                mSpeedY = -mSpeedY;
                if (mSpeedX > 0) {
                    mRandomRotationSpeed = -Math.abs(mRandomRotationSpeed);
                } else {
                    mRandomRotationSpeed = Math.abs(mRandomRotationSpeed);
                }
            } else if (y + mSprite.getHeight() / 2 >= smartGLView.getHeight()) {
                y = smartGLView.getHeight() - mSprite.getHeight() / 2;
                mSpeedY = -mSpeedY;
                if (mSpeedX > 0) {
                    mRandomRotationSpeed = Math.abs(mRandomRotationSpeed);
                } else {
                    mRandomRotationSpeed = -Math.abs(mRandomRotationSpeed);
                }
            }
            mSprite.setPos(x, y);
        }

        if (mObject3D != null) {
            float rx = mObject3D.getRotX() + 50 * frameDuration;
            float ry = mObject3D.getRotY() + 37 * frameDuration;
            float rz = mObject3D.getRotZ() + 26 * frameDuration;
            mObject3D.setRotation(rx, ry, rz);
            //mObject3D.setPos(mObject3D.getPosX() + 0.01f, mObject3D.getPosY(), mObject3D.getPosZ());
        }

    }

    @Override
    public void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent event) {
    }

    private void dropAllObject3D() {
        mRenderPassObject3D.clearObjects();
        mRenderPassObject3DColor.clearObjects();
    }

    private Object3D loadCruiser(@NonNull Context context) {
        WavefrontModel model = new WavefrontModel.Builder(context, R.raw.space_cruiser_obj)
                .addTexture("", mSpaceCruiserTexture)
                .create();

//        ColladaModel model = new ColladaModel.Builder(context, R.raw.space_cruiser_collada_textured)
//                .addTexture("space_cruiser_4_color_png", mSpaceCruiserTexture)	// "space_cruiser_4_color_png" is defined in the space_cruiser_collada_textured
//                .create();

        Object3D object3D = model.toObject3D();
        object3D.setScale(0.2f, 0.2f, 0.2f);
        object3D.setPos(0, 0, -5);
        return object3D;
    }

    void switchToCruiser() {
        mNextObjectColor = null;
        mNextObject = mCruiser;
    }

    private Object3D loadFrigate(@NonNull Context context) {
        WavefrontModel model = new WavefrontModel.Builder(context, R.raw.space_frigate_obj)
                .addTexture("", mSpaceFrigateTexture)
                .create();
        Object3D object3D = model.toObject3D();
        object3D.setScale(0.2f, 0.2f, 0.2f);
        object3D.setPos(0, 0, -7);
        return object3D;
    }

    void switchToFrigate() {
        mNextObjectColor = null;
        mNextObject = mFrigate;
    }

    private Object3D loadCube(@NonNull Context context) {
        WavefrontModel modelColored = new WavefrontModel.Builder(context, R.raw.cube_color_obj)
                .create();
        Object3D object3D = modelColored.toObject3D();
        object3D.setPos(0, 0, -4);
        return object3D;
    }

    void switchToCube() {
        mNextObject = null;
        mNextObjectColor = mCube;
    }

    private Object3D loadEarth(@NonNull Context context) {
        WavefrontModel modelColored = new WavefrontModel.Builder(context, R.raw.earth_obj)
                .setColor(0.2f, 0.6f, 1)
                .create();
        Object3D object3D = modelColored.toObject3D();
        object3D.setPos(0, 0, -4);
        return object3D;
    }
    void switchToEarth() {
        mNextObject = null;
        mNextObjectColor = mEarth;
    }

    private Object3D loadBus(@NonNull Context context) {
        WavefrontModel model = new WavefrontModel.Builder(context, R.raw.bus_obj)
                .addTexture("Mat_1", mObjectTexture)
                .addTexture("Mat_2", mSpriteTexture)
                .addTexture("Mat_3", mObjectTexture)
                .addTexture("Mat_4", mSpriteTexture)
                .addTexture("Mat_5", mObjectTexture)
                .addTexture("Mat_6", mSpriteTexture)
                .create();
        Object3D object3D = model.toObject3D();
        object3D.setScale(0.1f, 0.1f, 0.1f);
        object3D.setPos(0, 0, -50);
        return object3D;
    }

    void switchToBus() {
        mNextObjectColor = null;
        mNextObject = mBus;
    }

}
