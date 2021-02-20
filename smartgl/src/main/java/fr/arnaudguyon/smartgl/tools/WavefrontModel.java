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

import androidx.annotation.FloatRange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import fr.arnaudguyon.smartgl.math.Vector3D;
import fr.arnaudguyon.smartgl.opengl.ColorList;
import fr.arnaudguyon.smartgl.opengl.Face3D;
import fr.arnaudguyon.smartgl.opengl.NormalList;
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
        private Context mContext;
        private int mRawResourceId;
        private boolean mOptimizeModel = true;
        private HashMap<String, Texture> mTextures = new HashMap<>();
        private float[] mColor = {1,1,1};

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
        public Builder setColor(@FloatRange(from=0, to=1) float red, @FloatRange(from=0, to=1) float green, @FloatRange(from=0, to=1) float blue) {
            mColor[0] = red;
            mColor[1] = green;
            mColor[2] = blue;
            return this;
        }

        public WavefrontModel create() {
            WavefrontModel wavefront = new WavefrontModel();
            wavefront.loadObject(mContext, mRawResourceId);
            if (mOptimizeModel) {
                wavefront.mergeStrips();
            }
            wavefront.mTextures = mTextures;
            wavefront.mColor = mColor;
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

        float mR, mG, mB;
        boolean mHasColors = false;

        Vertex(float x, float y, float z) {
            mX = x;
            mY = y;
            mZ = z;
        }

        void setColors(float r, float g, float b) {
            mHasColors = true;
            mR = r;
            mG = g;
            mB = b;
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

        Normal(Vector3D vector) {
            float[] values = vector.getArray();
            mX = values[0];
            mY = values[1];
            mZ = values[2];
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
    private float[] mColor;

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
                String[] elements = line.split("\\s+");
                switch (elements[0]) {
                    case "v":   // VERTEX
                        if (elements.length > 3) {
                            Float x = fromString(elements[1]);
                            Float y = fromString(elements[2]);
                            Float z = fromString(elements[3]);
                            if ((x != null) && (y != null) && (z != null)) {
                                Vertex vertex = new Vertex(x, y, z);
                                mVertex.add(vertex);

                                if (elements.length > 6) {  // get vertex colors
                                    Float r = fromString(elements[4]);
                                    Float g = fromString(elements[5]);
                                    Float b = fromString(elements[6]);
                                    if (    (r != null) && (g != null) && (b != null) &&
                                            (r >= 0) && (r <= 1) && (g >= 0) && (g <=1) && (b >= 0) && (b <= 1)) {
                                        vertex.setColors(r, g, b);
                                        break;
                                    } else {
                                        throw new RuntimeException("Vertex Colors error line " + lineNumber);
                                    }
                                }
                                break;
                            }
                        }
                        throw new RuntimeException("Vertex error line " + lineNumber);
                    case "vt":  // TEXTURE MAPPING
                        if (elements.length > 2) {
                            Float u = fromString(elements[1]);
                            Float v = fromString(elements[2]);
                            if ((u != null) && (v != null)) {
                                UV uv = new UV(u, 1 - v);   // uv are upside down, use (u, 1-v)
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
                        if (elements.length == 4) {    // Triangle
                            addFaceStrips(lineNumber, stripTextureName, elements, 1, 3);
                        } else {
                            throw new RuntimeException("Only triangles supported, error line " + lineNumber);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mNormals.size() == 0) {
            // compute normals
            for(int faceIndex=0; faceIndex<mStrips.size(); ++faceIndex) {
                Strip strip = mStrips.get(faceIndex);
                Vertex v0 = mVertex.get(strip.mIndexes.get(0).mVertexIndex);
                Vertex v1 = mVertex.get(strip.mIndexes.get(1).mVertexIndex);
                Vertex v2 = mVertex.get(strip.mIndexes.get(2).mVertexIndex);
                Vector3D vec0 = new Vector3D(v1.mX - v0.mX, v1.mY - v0.mY, v1.mZ - v0.mZ);
                Vector3D vec1 = new Vector3D(v2.mX - v0.mX, v2.mY - v0.mY, v2.mZ - v0.mZ);
                vec0.normalize();
                vec1.normalize();
                Vector3D normal3D = vec1.vectorProduct(vec0);
                normal3D.normalize();
                Normal normal = new Normal(normal3D);
                mNormals.add(normal);
                strip.mIndexes.get(0).mNormalIndex = mNormals.size() - 1;
                strip.mIndexes.get(1).mNormalIndex = mNormals.size() - 1;
                strip.mIndexes.get(2).mNormalIndex = mNormals.size() - 1;
            }
        } else {
            // normalize normals
            for(Normal normal : mNormals) {
                Vector3D vec = new Vector3D(normal.mX, normal.mY, normal.mZ);
                vec.normalize();
                float[] values = vec.getArray();
                normal.mX = values[0];
                normal.mY = values[1];
                normal.mZ = values[2];
            }
        }

        Log.i("DONE", "DONE");
    }

    private void addFaceStrips(int lineNumber, String materialName, String[] elements, int offsetStart, int numberOfVertex) {

        // TODO: check order, seem to be dependant on file loaded :/
        // Maybe suggest to use a tool to re-export (http://meshlab.sourceforge.net/ ?)
        int[] triangleIndex = {0, 2 , 1};
//        int[] quadIndex = {0, 2, 1, 3};
//        // TODO: convex polygon should be like 0,1,2 0,2,3 0,3,4 0,4,5 ...
//        int[] order = (numberOfVertex == 3) ? triangleIndex : quadIndex;

        ArrayList<IndexInfo> indexInfos = new ArrayList<>(numberOfVertex);
        for(int i=0; i<numberOfVertex; ++i) {
            int index = triangleIndex[i];
            Integer vA = intPart(elements, offsetStart + index, 0);   // Vertex A
            Integer tA = intPart(elements, offsetStart + index, 1);   // Texture UV A
            Integer nA = intPart(elements, offsetStart + index ,2);   // Normal A
            IndexInfo indexA = IndexInfo.create(vA, tA, nA);
            if (indexA == null) {
                throw new RuntimeException("Face error line " + lineNumber);
            }
            indexInfos.add(indexA);
        }
        Strip strip = new Strip(materialName);
        for(IndexInfo indexInfo : indexInfos) {
            strip.addIndex(indexInfo);
        }
        mStrips.add(strip);
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

        final boolean hasUV = (mUVs.size() > 0);
        final boolean hasNormals = (mNormals.size() > 0);

        Object3D object3D = new Object3D();
        for(Strip strip : mStrips) {

            Face3D face3D = new Face3D();
            int nbIndex = strip.mIndexes.size();

            VertexList vertexList = new VertexList();
            vertexList.init(nbIndex);

            UVList uvList = null;
            ColorList colorList = null;
            if (hasUV) {
                uvList = new UVList();
                uvList.init(nbIndex);
            } else {
                colorList = new ColorList();
                colorList.init(nbIndex);
            }

            NormalList normalList = null;
            if (hasNormals) {
                normalList = new NormalList();
                normalList.init(nbIndex);
            }

            for(IndexInfo indexInfo : strip.mIndexes) {
                int vertexIndex = indexInfo.mVertexIndex;
                Vertex vertex = mVertex.get(vertexIndex);
                vertexList.add(vertex.mX, vertex.mY, vertex.mZ);

                if (hasUV) {
                    int uvIndex = indexInfo.mUVIndex;
                    UV uv = mUVs.get(uvIndex);
                    uvList.add(uv.mU, uv.mV);
                } else if (vertex.mHasColors) {
                    colorList.add(vertex.mR, vertex.mG, vertex.mB, 1);  // TODO: find a way to change the alpha channel?
                } else {
                    vertex.setColors(mColor[0], mColor[1], mColor[2]);
                    colorList.add(vertex.mR, vertex.mG, vertex.mB, 1);
//                    throw new RuntimeException("Model must have texture UVs or vertex Colors");
                }

                if (hasNormals) {
                    int normalIndex = indexInfo.mNormalIndex;
                    Normal normal = mNormals.get(normalIndex);
                    normalList.add(normal.mX, normal.mY, normal.mZ);
                }

            }
            vertexList.finalizeBuffer();
            face3D.setVertexList(vertexList);

            if (hasUV) {
                uvList.finalizeBuffer();
                face3D.setUVList(uvList);
                Texture texture = mTextures.get(strip.mTextureName);
                face3D.setTexture(texture);
            } else {
                colorList.finalizeBuffer();
                face3D.setColorList(colorList);
            }

            if (hasNormals) {
                normalList.finalizeBuffer();
                face3D.setNormalList(normalList);
            }

            object3D.addFace(face3D);
        }
        return object3D;
    }

}
