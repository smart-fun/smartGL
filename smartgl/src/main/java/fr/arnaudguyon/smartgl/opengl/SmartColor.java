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
package fr.arnaudguyon.smartgl.opengl;

import androidx.annotation.FloatRange;

/**
 * Created by aguyon on 18.01.17.
 */

public class SmartColor {

    private float[] mColor = new float[4];

    public SmartColor(@FloatRange(from=0, to=1) float red, @FloatRange(from=0, to=1) float green, @FloatRange(from=0, to=1) float blue) {
        mColor[0] = red;
        mColor[1] = green;
        mColor[2] = blue;
        mColor[3] = 1;
    }

    public SmartColor(@FloatRange(from=0, to=1) float red, @FloatRange(from=0, to=1) float green, @FloatRange(from=0, to=1) float blue, @FloatRange(from=0, to=1) float alpha) {
        mColor[0] = red;
        mColor[1] = green;
        mColor[2] = blue;
        mColor[3] = alpha;
    }

    public float[] getArray() {
        return mColor;
    }

}
