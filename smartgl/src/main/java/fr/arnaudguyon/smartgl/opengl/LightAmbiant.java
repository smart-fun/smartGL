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

import android.support.annotation.FloatRange;

/**
 * Ambiant Light for OpenGL
 */

public class LightAmbiant extends Light {

    private SmartColor mColor;

    public LightAmbiant(@FloatRange(from=0, to=1) float red, @FloatRange(from=0, to=1) float green, @FloatRange(from=0, to=1) float blue) {
        mColor = new SmartColor(red, green, blue, 1);
    }

    public float[] getArray() {
        return mColor.getArray();
    }
}
