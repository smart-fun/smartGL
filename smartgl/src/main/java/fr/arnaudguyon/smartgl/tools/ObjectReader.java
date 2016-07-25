package fr.arnaudguyon.smartgl.tools;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;

/**
 * Reads a .obj file and converts it into an ArrayList of Object3D models
 * see https://en.wikipedia.org/wiki/Wavefront_.obj_file
 * see https://fr.wikipedia.org/wiki/Objet_3D_(format_de_fichier)
 */
public class ObjectReader {

    private static final String TAG = "ObjectReader";

    private ArrayList<Object3D> mObjects = new ArrayList<>();
    private Object3D mObject;
    private ArrayList<Float> mVertex;
    private ArrayList<Float> mUVs;
    private ArrayList<ArrayList<Integer>> mFacesIndex;
    private Texture mDefaultTexture;

    public ObjectReader() {
    }

    public ArrayList<Object3D> readRawResource(Context context, int rawResId, Texture defaultTexure) {

        mObjects.clear();
        mObject = null;
        mDefaultTexture = defaultTexure;

        InputStream inputStream = context.getResources().openRawResource(rawResId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] elements = line.split(" ");
                if (elements.length >= 3) {
                    switch (elements[0]) {
                        case "v":
                            if (elements.length >= 4) {
                                addVertex(elements[1], elements[2], elements[3]);
                            }
                            break;
                        case "vt":
                            addUV(elements[1], elements[2]);
                            break;
                        case "f":
                            if (elements.length >= 4) {
                                addFaceIndex(elements);
                            }
                            break;
                    }
                }
            }
            finalizeObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mObjects;
    }

    private void addVertex(String sx, String sy, String sz) {

        // has already UVs, so this is a new Object
        if ((mObject != null) && (mUVs != null)) {
            finalizeObject();
        }
        // no object, create a new one
        if (mObject == null) {
            Log.i(TAG, "New Object");
            mObject = new Object3D();
            mVertex = new ArrayList<>();
        }

        Float x = fromString(sx);
        Float y = fromString(sy);
        Float z = fromString(sz);
        if ((x != null) && (y != null) && (z != null)) {
            mVertex.add(x);
            mVertex.add(y);
            mVertex.add(z);
        }
    }

    private void addUV(String su, String sv) {
        if (mUVs == null) {
            mUVs = new ArrayList<>();
        }
        Float u = fromString(su);
        Float v = fromString(sv);
        if ((u != null) && (v != null)) {
            mUVs.add(u);
            mUVs.add(v);
        }
    }

    private void addFaceIndex(String[] elements) {

        if (mFacesIndex == null) {
            mFacesIndex = new ArrayList<>();
        }

        ArrayList<Integer> faceIndex = new ArrayList<>();
        // ignore 1st element which is "f"
        for (int i = 1; i < elements.length; ++i) {
            String element = elements[i];
            Integer indexVertex = intPart(element, 0);
            Integer indexUV = intPart(element, 1);
            if ((indexVertex != null) && (indexUV != null)) {
                faceIndex.add(indexVertex);
                faceIndex.add(indexUV);
            }
        }
        if (faceIndex.size() > 0) {
            mFacesIndex.add(faceIndex);
        }
    }

    // TODO: 1 triangle = 1 face = sucks
    // vertex are duplicated, is it ok like this?
    private void finalizeObject() {

        // only supports texture mapping (vertex + uvs)
        if ((mObject != null) && (mVertex != null) && (mUVs != null) && (mFacesIndex != null) && (mVertex.size() > 0)) {

            Vector<Face3D> faces = new Vector<>();
            for (ArrayList<Integer> faceIndex : mFacesIndex) {
                int nbElements = faceIndex.size() / 2; // Vertex & UV
                VertexList vertexList = new VertexList();
                vertexList.init(nbElements);
                UVList uvList = new UVList();
                uvList.init(nbElements);
                for (int index = 0; index < faceIndex.size(); ) {
                    int vertexOffset = (faceIndex.get(index++) - 1) * 3;    // 1 indexed, not 0
                    int uvOffset = (faceIndex.get(index++) - 1) * 2;
                    vertexList.add(mVertex.get(vertexOffset++), mVertex.get(vertexOffset++), mVertex.get(vertexOffset++));
                    uvList.add(mUVs.get(uvOffset++), mUVs.get(uvOffset++));
                }
                vertexList.finalizeBuffer();
                uvList.finalizeBuffer();

                Face3D face3D = new Face3D();
                face3D.setVertexList(vertexList);
                face3D.setUVList(uvList);
                // Default Texture ??
                face3D.setTexture(mDefaultTexture);
                faces.add(face3D);
            }

            mObject.setFaces(faces);
            mObjects.add(mObject);
        }
        mObject = null;
        mVertex = null;
        mUVs = null;
        mFacesIndex = null;
    }

    private Float fromString(String string) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException exception) {
        }
        return null;
    }

    private Integer intPart(String string, int part) {
        String[] parts = string.split("/");
        if ((parts != null) && (parts.length > part)) {
            String firstString = parts[part];
            try {
                return Integer.parseInt(firstString);
            } catch (NumberFormatException exception) {
            }
        }
        return null;
    }

}
