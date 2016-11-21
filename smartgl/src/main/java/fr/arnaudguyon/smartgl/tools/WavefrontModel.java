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
 */

public class WavefrontModel {

    public static class Builder {
        Context mContext;
        int mRawResourceId;
        boolean mOptimize = false;
        HashMap<String, Texture> mTextures = new HashMap<>();

        public Builder(Context context, int rawFileResourceId) {
            mContext = context;
            mRawResourceId = rawFileResourceId;
        }
        public Builder optimize(boolean optimize) {
            mOptimize = optimize;
            return this;
        }
        public Builder addTexture(String textureName, Texture texture) {
            mTextures.put(textureName, texture);
            return this;
        }

        public WavefrontModel create() {
            WavefrontModel wavefront = new WavefrontModel();
            wavefront.loadObject(mContext, mRawResourceId);
            if (mOptimize) {
                wavefront.optimize();
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
            indexInfo.mVertexIndex = vertexIndex.intValue();
            indexInfo.mUVIndex = (uvIndex != null) ? uvIndex.intValue() : 0;
            indexInfo.mNormalIndex = (normalIndex != null) ? normalIndex.intValue() : 0;
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

        Strip reverse() {
            Strip strip = new Strip(mTextureName);
            for(IndexInfo indexInfo : mIndexes) {
                strip.mIndexes.add(0, indexInfo);
            }
            return strip;
        }
    }

    // class used in optimization phase to compare parts of Strips to collide them if they match
    private static class IndexPair {
        int mIndex0;
        int mIndex1;

        IndexPair(int index0, int index1) {
            mIndex0 = index0;
            mIndex1 = index1;
        }

        static IndexPair startOf(Strip strip) {
            IndexInfo indexInfo0 = strip.mIndexes.get(0);
            IndexInfo indexInfo1 = strip.mIndexes.get(1);
            return new IndexPair(indexInfo0.mVertexIndex, indexInfo1.mVertexIndex);
        }
        static IndexPair endOf(Strip strip) {
            int size = strip.mIndexes.size();
            IndexInfo indexInfo0 = strip.mIndexes.get(size - 2);
            IndexInfo indexInfo1 = strip.mIndexes.get(size - 1);
            return new IndexPair(indexInfo0.mVertexIndex, indexInfo1.mVertexIndex);
        }

        boolean isSame(IndexPair other) {
            return ((mIndex0 == other.mIndex0) && (mIndex1 == other.mIndex1));
        }
        boolean isOpposite(IndexPair other) {
            return ((mIndex0 == other.mIndex1) && (mIndex1 == other.mIndex0));
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
    HashMap<String, Texture> mTextures = new HashMap<>();

    private WavefrontModel() {
    }

    private void loadObject(Context context, int rawResId) throws RuntimeException {
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {

            String stripTextureName = "unknown";
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
                return Integer.parseInt(firstString) - 1;   // TODO: this is because indexes start to 1, but put somewhere else or rename
            } catch (NumberFormatException exception) {
            }
        }
        return null;
    }

    private void optimize() {
        for(int iStrip = 0; iStrip<mStrips.size() - 1; ++iStrip) {
            Strip origin = mStrips.get(iStrip);
            String originTexture = origin.mTextureName;
            IndexPair originStartPair = IndexPair.startOf(origin);
            IndexPair originEndPair = IndexPair.endOf(origin);
            for(int iOther = iStrip+1; iOther<mStrips.size(); ++iOther) {
                Strip other = mStrips.get(iOther);
                if (originTexture.equals(other.mTextureName)) {
                    IndexPair otherStartPair = IndexPair.startOf(other);
                    IndexPair otherEndPair = IndexPair.endOf(other);
                    if (originEndPair.isSame(otherStartPair)) {
                        origin.mIndexes.addAll(other.mIndexes);
                        mStrips.remove(iOther);
                        --iOther;
                        originEndPair = IndexPair.endOf(origin);
                    } else if (originStartPair.equals(otherEndPair)) {
                        other.mIndexes.addAll(origin.mIndexes);
                        origin.mIndexes = other.mIndexes;
                        mStrips.remove(iOther);
                        --iOther;
                        originStartPair = IndexPair.startOf(origin);
                        originEndPair = IndexPair.endOf(origin);
                    } else if (originEndPair.isOpposite(otherEndPair)) {
                        origin.mIndexes.addAll(other.reverse().mIndexes);
                        mStrips.remove(iOther);
                        --iOther;
                        originEndPair = IndexPair.endOf(origin);
                    } else if (originStartPair.isOpposite(otherStartPair)) {
                        Strip otherReverse = other.reverse();
                        otherReverse.mIndexes.addAll(origin.mIndexes);
                        origin.mIndexes = otherReverse.mIndexes;
                        mStrips.remove(iOther);
                        --iOther;
                        originStartPair = IndexPair.startOf(origin);
                        originEndPair = IndexPair.endOf(origin);
                    }
                }
            }
        }
    }

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

    // TODO: log what optimize has done
}
