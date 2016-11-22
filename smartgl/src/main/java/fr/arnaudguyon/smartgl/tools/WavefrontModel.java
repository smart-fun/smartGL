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
package fr.arnaudguyon.smartgl.tools;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.opengl.UVList;
import fr.arnaudguyon.smartgl.opengl.VertexList;

/**
 * Created by aguyon on 21.11.16.
 * Helper to load Wavefront objects and convert them to Object3D
 */

public class WavefrontModel {

    private static final String TAG = "WavefrontModel";

    public static class Builder {
        Context mContext;
        int mRawResourceId;
        boolean mOptimizeModel = true;
        HashMap<String, Texture> mTextures = new HashMap<>();

        public Builder(Context context, int rawFileResourceId) {
            mContext = context;
            mRawResourceId = rawFileResourceId;
        }
        public Builder optimize(boolean optimizeModel) {
            mOptimizeModel = optimizeModel;
            return this;
        }
        public Builder addTexture(String textureName, Texture texture) {
            mTextures.put(textureName, texture);
            return this;
        }

        public WavefrontModel create() {
            WavefrontModel wavefront = new WavefrontModel();
            wavefront.loadObject(mContext, mRawResourceId);
            if (mOptimizeModel) {
                wavefront.mergeStrips();
            }
            wavefront.mTextures = mTextures;
            return wavefront;
        }

    }

    private static class IndexInfo {
        int mVertexIndex;
        int mUVIndex;
        int mNormalIndex;

        static IndexInfo create(Integer vertexIndex, Integer uvIndex, Integer normalIndex) {
            if (vertexIndex == null) {
                return null;
            }
            IndexInfo indexInfo = new IndexInfo();
            indexInfo.mVertexIndex = vertexIndex - 1;
            indexInfo.mUVIndex = (uvIndex != null) ? uvIndex - 1 : 0;
            indexInfo.mNormalIndex = (normalIndex != null) ? normalIndex - 1 : 0;
            return indexInfo;
        }
    }

    private static class Strip {
        String mTextureName;
        ArrayList<IndexInfo> mIndexes = new ArrayList<>();

        Strip(String textureName) {
            mTextureName = textureName;
        }
        void addIndex(IndexInfo indexInfo) {
            mIndexes.add(indexInfo);
        }
        void addAll(ArrayList<IndexInfo> indexes) {
            mIndexes.addAll(indexes);
        }
    }

    private class Vertex {
        float mX;
        float mY;
        float mZ;

        Vertex(float x, float y, float z) {
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    private class Normal {
        float mX;
        float mY;
        float mZ;

        Normal(float x, float y, float z) {
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    private class UV {
        float mU;
        float mV;
        UV(float u, float v) {
            mU = u;
            mV = v;
        }
    }

    private ArrayList<Vertex> mVertex = new ArrayList<>();
    private ArrayList<UV> mUVs = new ArrayList<>();
    private ArrayList<Normal> mNormals = new ArrayList<>();
    private ArrayList<Strip> mStrips = new ArrayList<>();
    private HashMap<String, Texture> mTextures = new HashMap<>();

    private WavefrontModel() {
    }

    private void loadObject(Context context, int rawResId) throws RuntimeException {
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {

            String stripTextureName = "";
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                ++lineNumber;
                line = line.replace("  ", " ");
                String[] elements = line.split(" ");
                switch (elements[0]) {
                    case "v":   // VERTEX
                        if (elements.length > 3) {
                            Float x = fromString(elements[1]);
                            Float y = fromString(elements[2]);
                            Float z = fromString(elements[3]);
                            if ((x != null) && (y != null) && (z != null)) {
                                Vertex vertex = new Vertex(x, y, z);
                                mVertex.add(vertex);
                                break;
                            }
                        }
                        throw new RuntimeException("Vertex error line " + lineNumber);
                    case "vt":  // TEXTURE MAPPING
                        if (elements.length > 2) {
                            Float u = fromString(elements[1]);
                            Float v = fromString(elements[2]);
                            if ((u != null) && (v != null)) {
                                UV uv = new UV(u, v);
                                mUVs.add(uv);
                                break;
                            }
                        }
                        throw new RuntimeException("Texture Mapping error line " + lineNumber);
                    case "vn":  // NORMAL
                        if (elements.length > 3) {
                            Float x = fromString(elements[1]);
                            Float y = fromString(elements[2]);
                            Float z = fromString(elements[3]);
                            if ((x != null) && (y != null) && (z != null)) {
                                Normal normal = new Normal(x, y, z);
                                mNormals.add(normal);
                                break;
                            }
                        }
                        throw new RuntimeException("Normal error line " + lineNumber);
                    case "usemtl":  // MATERIAL
                        if (elements.length > 1) {
                            stripTextureName = elements[1];
                        }
                        break;
                    case "f":   // FACES
                        if (elements.length == 5) { // Quad
                            Integer vA = intPart(elements, 1, 0);   // Vertex A
                            Integer tA = intPart(elements, 1, 1);   // Texture UV A
                            Integer nA = intPart(elements, 1 ,2);   // Normal A
                            IndexInfo indexA = IndexInfo.create(vA, tA, nA);

                            Integer vB = intPart(elements, 2, 0);   // Vertex B
                            Integer tB = intPart(elements, 2, 1);   // Texture UV B
                            Integer nB = intPart(elements, 2 ,2);   // Normal B
                            IndexInfo indexB = IndexInfo.create(vB, tB, nB);

                            Integer vC = intPart(elements, 3, 0);   // Vertex C
                            Integer tC = intPart(elements, 3, 1);   // Texture UV C
                            Integer nC = intPart(elements, 3 ,2);   // Normal C
                            IndexInfo indexC = IndexInfo.create(vC, tC, nC);

                            Integer vD = intPart(elements, 4, 0);   // Vertex D
                            Integer tD = intPart(elements, 4, 1);   // Texture UV D
                            Integer nD = intPart(elements, 4 ,2);   // Normal D
                            IndexInfo indexD = IndexInfo.create(vD, tD, nD);

                            if ((indexA != null) && (indexB != null) && (indexC != null) && (indexD != null)) {
                                Strip strip = new Strip(stripTextureName);
                                strip.addIndex(indexA);
                                strip.addIndex(indexB);
                                strip.addIndex(indexD); // quad converted to triangle strip
                                strip.addIndex(indexC);
                                mStrips.add(strip);
                            } else {
                                throw new RuntimeException("Quad error line " + lineNumber);
                            }

                        } else if (elements.length == 4) {    // Triangle
                            Integer vA = intPart(elements, 1, 0);   // Vertex A
                            Integer tA = intPart(elements, 1, 1);   // Texture UV A
                            Integer nA = intPart(elements, 1 ,2);   // Normal A
                            IndexInfo indexA = IndexInfo.create(vA, tA, nA);

                            Integer vB = intPart(elements, 2, 0);   // Vertex B
                            Integer tB = intPart(elements, 2, 1);   // Texture UV B
                            Integer nB = intPart(elements, 2 ,2);   // Normal B
                            IndexInfo indexB = IndexInfo.create(vB, tB, nB);

                            Integer vC = intPart(elements, 3, 0);   // Vertex C
                            Integer tC = intPart(elements, 3, 1);   // Texture UV C
                            Integer nC = intPart(elements, 3 ,2);   // Normal C
                            IndexInfo indexC = IndexInfo.create(vC, tC, nC);

                            if ((indexA != null) && (indexB != null) && (indexC != null)) {
                                Strip strip = new Strip(stripTextureName);
                                strip.addIndex(indexA);
                                strip.addIndex(indexB);
                                strip.addIndex(indexC);
                                mStrips.add(strip);
                            } else {
                                throw new RuntimeException("Triangle error line " + lineNumber);
                            }

                        } else {
                            // TODO: load polygonal faces (more than 4 vertex). I'm not sure how to split into triangles
                            throw new RuntimeException("Face Index error line " + lineNumber);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("DONE", "DONE");
    }

    private Float fromString(String string) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException exception) {
        }
        return null;
    }

    private Integer intPart(String[] elements, int elementNumber, int partNumber) {
        String element = elements[elementNumber];
        return intPart(element, partNumber);
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

//    private void removeDuplicates() {
//
//        int nbDuplicateVertex = 0;
//        int nbDuplicateUV = 0;
//        int nbDuplicateNormals = 0;
//
//        SparseIntArray duplicateVertex = new SparseIntArray(mVertex.size());    // duplicated, original
//        SparseIntArray duplicateUV = new SparseIntArray(mUVs.size());
//        SparseIntArray duplicateNormal = new SparseIntArray(mNormals.size());
//
//        // VERTEX
//        {
//            for (int index = 0; index < mVertex.size() - 1; ++index) {
//                Vertex vertex = mVertex.get(index);
//                for (int iOther = index + 1; iOther < mVertex.size(); ++iOther) {
//                    Vertex other = mVertex.get(iOther);
//                    if (vertex.equals(other)) {
//                        ++nbDuplicateVertex;
//                        duplicateVertex.put(iOther, index);
//                    }
//                }
//            }
//        }
//
//        // MAPPING
//        {
//            for (int index = 0; index < mUVs.size() - 1; ++index) {
//                UV uv = mUVs.get(index);
//                for (int iOther = index + 1; iOther < mUVs.size(); ++iOther) {
//                    UV other = mUVs.get(iOther);
//                    if (uv.equals(other)) {
//                        ++nbDuplicateUV;
//                        duplicateUV.put(iOther, index);
//                    }
//                }
//            }
//        }
//
//        // NORMALS
//        {
//            for (int index = 0; index < mNormals.size() - 1; ++index) {
//                Normal normal = mNormals.get(index);
//                for (int iOther = index + 1; iOther < mNormals.size(); ++iOther) {
//                    Normal other = mNormals.get(iOther);
//                    if (normal.equals(other)) {
//                        ++nbDuplicateNormals;
//                        duplicateNormal.put(iOther, index);
//                    }
//                }
//            }
//        }
//
//        Log.i(TAG, nbDuplicateVertex + " duplicated Vertex found");
//        Log.i(TAG, nbDuplicateUV + " duplicated UVs found");
//        Log.i(TAG, nbDuplicateNormals + " duplicated Normals found");
//
//        // Fix them!
//        for(Strip strip : mStrips) {
//            ArrayList<IndexInfo> indexes = strip.mIndexes;
//            for(IndexInfo indexInfo : indexes) {
//                indexInfo.mVertexIndex = duplicateVertex.get(indexInfo.mVertexIndex, indexInfo.mVertexIndex);
//                indexInfo.mUVIndex = duplicateUV.get(indexInfo.mUVIndex, indexInfo.mUVIndex);
//                indexInfo.mNormalIndex = duplicateNormal.get(indexInfo.mNormalIndex, indexInfo.mNormalIndex);
//            }
//        }
//
//    }

    // Groups Strips of same material into 1 big strip (1 triangle strip per face)
    private void mergeStrips() {
        for(int iStrip = 0; iStrip<mStrips.size() - 1; ++iStrip) {
            Strip origin = mStrips.get(iStrip);
            String originTexture = origin.mTextureName;
            for(int iOther = iStrip+1; iOther<mStrips.size(); ++iOther) {
                Strip other = mStrips.get(iOther);
                if (originTexture.equals(other.mTextureName)) {

                    IndexInfo originLastIndex = origin.mIndexes.get(origin.mIndexes.size() - 1);
                    IndexInfo otherFirstIndex = other.mIndexes.get(0);
                    origin.addIndex(originLastIndex);
                    origin.addIndex(otherFirstIndex);
                    origin.addAll(other.mIndexes);

                    mStrips.remove(iOther);
                    --iOther;
                }
            }
        }
    }

    /**
     * Converts the model to an Object3D
     * @return an Object3D
     */
    public Object3D toObject3D() {

        Object3D object3D = new Object3D();
        for(Strip strip : mStrips) {
            Face3D face3D = new Face3D();
            int nbIndex = strip.mIndexes.size();
            VertexList vertexList = new VertexList();
            vertexList.init(nbIndex);
            UVList uvList = new UVList();
            uvList.init(nbIndex);
            for(IndexInfo indexInfo : strip.mIndexes) {
                int vertexIndex = indexInfo.mVertexIndex;
                Vertex vertex = mVertex.get(vertexIndex);
                vertexList.add(vertex.mX, vertex.mY, vertex.mZ);

                if (mUVs.size() > 0) {
                    int uvIndex = indexInfo.mUVIndex;
                    UV uv = mUVs.get(uvIndex);
                    uvList.add(uv.mU, uv.mV);
                } else {
                    uvList.add((float) Math.random(), (float) Math.random());
                }
            }
            vertexList.finalizeBuffer();
            face3D.setVertexList(vertexList);
            uvList.finalizeBuffer();
            face3D.setUVList(uvList);

            Texture texture = mTextures.get(strip.mTextureName);
            face3D.setTexture(texture);

            object3D.addFace(face3D);
        }
        return object3D;
    }

}
