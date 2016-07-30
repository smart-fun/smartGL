package fr.arnaudguyon.smartglapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.Vector;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.OpenGLRenderer;
import fr.arnaudguyon.smartgl.opengl.RenderPass;
import fr.arnaudguyon.smartgl.opengl.ShaderTexture;
import fr.arnaudguyon.smartgl.opengl.SmartGLFragment;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.Sprite;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;
import fr.arnaudguyon.smartgl.tools.ObjectReader;

/**
 * Created by aguyon on 24/07/16.
 */
public class MainGLView extends SmartGLView {

    private Sprite mSprite;
    //private Object3D mObject3D;
    private ArrayList<Object3D> mLoadedObjects;

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

//        mObject3D = new Object3D();
//        Face3D face = new Face3D();
//        face.setTexture(new Texture(getContext(), R.drawable.door));
//        UVList uvList = new UVList();
//        uvList.init(4);
//        uvList.add(0, 0);
//        uvList.add(1, 0);
//        uvList.add(0, 1);
//        uvList.add(1, 1);
//        uvList.finalizeBuffer();
//        face.setUVList(uvList);   // TODO: create SquareUV for simple mapping (or load object)
//
//        VertexList vertexList = new VertexList();
//        vertexList.init(4);
//        float z = 0;
//        vertexList.add(0, 0, z);
//        vertexList.add(4, 0, z);
//        vertexList.add(0, 8, z);
//        vertexList.add(4, 8, z);
//        vertexList.finalizeBuffer();
//        face.setVertexList(vertexList);
//
//        mObject3D.addFace(face);
//        mObject3D.setPos(0, -2, -14);   // ! clip after Z = -100

        ObjectReader reader = new ObjectReader();
        mLoadedObjects = reader.readRawResource(getContext(), R.raw.bus, new Texture(getContext(), R.drawable.door));

        ShaderTexture shader = new ShaderTexture();
        RenderPass renderPass = new RenderPass();
        renderPass.addShader(shader);

        renderPass.getRenderObjects().add(mSprite);
        //renderPass.getRenderObjects().add(mObject3D);

        if (mLoadedObjects != null) {
            autoCenter(mLoadedObjects, 1, 1);
            for (Object3D object : mLoadedObjects) {
                //object.setScale(0.001f, 0.001f, 0.001f);
                //object.setPos(0, 0, -4);
                renderPass.getRenderObjects().add(object);
            }
        }

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
            float angle = mSprite.getRotation() + frameDuration * 50;
            mSprite.setRotation(angle);
        }

//        if (mObject3D != null) {
//            float angle = mObject3D.getRotY() + frameDuration * 150;
//            mObject3D.setRotation(0, angle, 0);
//
////            float[] windowPos = new float[3];
////            float[] vertexPos = mObject3D.getFaces().get(0).getVertexList().getInternalBuffer();
////            boolean success = Tools.worldToScreen(renderer, vertexPos, windowPos);
////            if (success) {
////                // check position
////            }
//
//        }

        if (mLoadedObjects != null) {
            for (Object3D object : mLoadedObjects) {
                float angle = object.getRotZ() + frameDuration * 10;
                object.setRotation(angle, angle, angle);
            }
        }
    }

    private void autoCenter(ArrayList<Object3D> objects, float size, float distance) {
        if (objects != null) {
            Float minX = null, maxX = null, minY = null, maxY = null, minZ = null, maxZ = null;
            for (Object3D object : objects) {
                Vector<Face3D> faces = object.getFaces();
                if (faces != null) {
                    for (Face3D face : faces) {
                        VertexList vertexList = face.getVertexList();
                        if (vertexList != null) {
                            float[] vertex = vertexList.getInternalBuffer();
                            if (vertex != null) {
                                for (int i = 0; i < vertex.length; i += 3) {
                                    if (minX == null) {
                                        minX = maxX = vertex[i];
                                        minY = maxY = vertex[i + 1];
                                        minZ = maxZ = vertex[i + 3];
                                    } else {
                                        if (vertex[i] < minX) {
                                            minX = vertex[i];
                                        } else if (vertex[i] > maxX) {
                                            maxX = vertex[i];
                                        }
                                        if (vertex[i + 1] < minY) {
                                            minY = vertex[i + 1];
                                        } else if (vertex[i + 1] > maxY) {
                                            maxY = vertex[i + 1];
                                        }
                                        if (vertex[i + 2] < minZ) {
                                            minZ = vertex[i + 2];
                                        } else if (vertex[i + 2] > maxZ) {
                                            maxZ = vertex[i + 2];
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (minX != null) {
                float sizeX = maxX - minX;
                float sizeY = maxY - minY;
                float sizeZ = maxZ - minZ;
                float currentSize = Math.max(Math.max(sizeX, sizeY), sizeZ);
                float scale = size / currentSize;
                float centerX = (minX + (sizeX / 2));
                float centerY = (minY + (sizeY / 2));
                float centerZ = (minZ + (sizeZ / 2));
                for (Object3D object : objects) {

                    Vector<Face3D> faces = object.getFaces();
                    if (faces != null) {
                        for (Face3D face : faces) {
                            VertexList vertexList = face.getVertexList();
                            if (vertexList != null) {
                                float[] vertex = vertexList.getInternalBuffer();
                                if (vertex != null) {
                                    for (int i = 0; i < vertex.length; i += 3) {
                                        vertex[i] = (vertex[i] - centerX) * scale;
                                        vertex[i+1] = (vertex[i+1] - centerY) * scale;
                                        vertex[i+2] = (vertex[i+2] - centerZ) * scale;
                                    }
                                }
                                vertexList.finalizeBuffer();
                            }
                        }
                    }

                    object.setPos(0, 0, -distance);
//                    object.setPos(-centerX * scale, -centerY * scale, -centerZ * scale - distance);
//                    object.setScale(scale, scale, scale);
                }
            }
        }

    }
}
