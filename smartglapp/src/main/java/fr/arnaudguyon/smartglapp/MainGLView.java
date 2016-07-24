package fr.arnaudguyon.smartglapp;

import android.content.Context;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.OpenGLRenderer;
import fr.arnaudguyon.smartgl.opengl.RenderPass;
import fr.arnaudguyon.smartgl.opengl.ShaderTexture;
import fr.arnaudguyon.smartgl.opengl.SmartGLFragment;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.Sprite;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.Tools;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;

/**
 * Created by aguyon on 24/07/16.
 */
public class MainGLView extends SmartGLView {

    private Sprite mSprite;
    private Object3D mObject3D;

    public MainGLView(Context context, OpenGLRenderer renderer, SmartGLFragment fragment) {
        super(context, renderer, fragment);
    }

    @Override
    protected void onAcquireResources() {
        super.onAcquireResources();

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
    protected void onReleaseResources() {

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
            float angle = mSprite.getRotation() + frameDuration*50;
            mSprite.setRotation(angle);
        }

        if (mObject3D != null) {
            float angle = mObject3D.getRotY() + frameDuration*150;
            mObject3D.setRotation(0, angle, 0);

//            float[] windowPos = new float[3];
//            float[] vertexPos = mObject3D.getFaces().get(0).getVertexList().getInternalBuffer();
//            boolean success = Tools.worldToScreen(renderer, vertexPos, windowPos);
//            if (success) {
//                // check position
//            }

        }


    }
}
