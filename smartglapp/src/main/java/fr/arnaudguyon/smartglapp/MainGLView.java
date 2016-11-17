package fr.arnaudguyon.smartglapp;

import android.content.Context;
import android.util.AttributeSet;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.OpenGLRenderer;
import fr.arnaudguyon.smartgl.opengl.RenderPass;
import fr.arnaudguyon.smartgl.opengl.ShaderTexture;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.Sprite;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;

/**
 * Created by aguyon on 24/07/16.
 */
public class MainGLView extends SmartGLView {

    private Sprite mSprite;
    private Object3D mObject3D;
    private float mRandomSpeed;

    private float mSpeedX = 200;
    private float mSpeedY = 200;

    public MainGLView(Context context) {
        super(context);
        init();
    }

    public MainGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mRandomSpeed = (float) ((Math.random() * 50) + 100);
        if (Math.random() > 0.5f) {
            mRandomSpeed *= -1;
        }
    }

    @Override
    protected void acquireResources() {
        super.acquireResources();

        mSprite = new Sprite(120, 120);
        mSprite.setPos(200, 300);
        mSprite.setPivot(0.5f, 0.5f);
        mSprite.setTexture(new Texture(getContext(), R.drawable.planet));

        mObject3D = new Object3D();
        Face3D face = new Face3D();
        face.setTexture(new Texture(getContext(), R.drawable.door));
        UVList uvList = new UVList();
        uvList.init(4);
        uvList.add(0,0);
        uvList.add(1,0);
        uvList.add(0,1);
        uvList.add(1,1);
        uvList.finalizeBuffer();
        face.setUVList(uvList);   // TODO: create SquareUV for simple mapping (or load object)

        VertexList vertexList = new VertexList();
        vertexList.init(4);
        float z = 0;
        vertexList.add(0,0,z);
        vertexList.add(4,0,z);
        vertexList.add(0,8,z);
        vertexList.add(4,8,z);
        vertexList.finalizeBuffer();
        face.setVertexList(vertexList);   // TODO: load object or provide list

        mObject3D.addFace(face);
        mObject3D.setPos(0, -4, -14);   // ! clip after Z = -100

        ShaderTexture shader = new ShaderTexture();
        RenderPass renderPass = new RenderPass();
        renderPass.addShader(shader);

        renderPass.getRenderObjects().add(mSprite);
        renderPass.getRenderObjects().add(mObject3D);

        getSmartGLRenderer().addRenderPass(renderPass);
    }

    @Override
    protected void releaseResources() {

        if (mSprite != null) {
            mSprite.releaseResources();
            mSprite = null;
        }
    }

    @Override
    public void onPreRender(OpenGLRenderer renderer) {
        super.onPreRender(renderer);

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
            } else if (x + mSprite.getWidth()/2 >= getWidth()) {
                x = getWidth() - mSprite.getWidth()/2;
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
            } else if (y + mSprite.getHeight()/2 >= getHeight()) {
                y = getHeight() - mSprite.getHeight()/2;
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


    }

    @Override
    protected void onViewResized(int width, int height) {
        releaseResources();
        acquireResources();
    }
}
