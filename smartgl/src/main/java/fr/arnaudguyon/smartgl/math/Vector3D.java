/*
    Copyright 2017 Arnaud Guyon

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
package fr.arnaudguyon.smartgl.math;

import androidx.annotation.NonNull;

/**
 * Created by aguyon on 18.01.17.
 */

public class Vector3D {

    private float[] mVector = new float[3];

    public Vector3D(float x, float y, float z) {
        mVector[0] = x;
        mVector[1] = y;
        mVector[2] = z;
    }

    public float[] getArray() {
        return mVector;
    }

    public Vector3D normalize() {
        float norm = (float) Math.sqrt(mVector[0]*mVector[0] + mVector[1]*mVector[1] + mVector[2]*mVector[2]);
        if (norm > 0) {
            mVector[0] /= norm;
            mVector[1] /= norm;
            mVector[2] /= norm;
        } else {
            mVector[0] = 1;
            mVector[1] = 0;
            mVector[2] = 0;
        }
        return this;
    }

    public float dot(@NonNull Vector3D other) {
        return (mVector[0] * other.mVector[0]) + (mVector[1] * other.mVector[1]) + (mVector[2] * other.mVector[2]);
    }

    public Vector3D add(@NonNull Vector3D other) {
        mVector[0] += other.mVector[0];
        mVector[1] += other.mVector[1];
        mVector[2] += other.mVector[2];
        return this;
    }
    public Vector3D multiply(float scale) {
        mVector[0] *= scale;
        mVector[1] *= scale;
        mVector[2] *= scale;
        return this;
    }

    public Vector3D vectorProduct(@NonNull Vector3D o) {
        float x = mVector[1] * o.mVector[2] - mVector[2] * o.mVector[1];
        float y = mVector[2] * o.mVector[0] - mVector[0] * o.mVector[2];
        float z = mVector[0] * o.mVector[1] - mVector[1] * o.mVector[0];
        return new Vector3D(x, y, z);
    }

}
