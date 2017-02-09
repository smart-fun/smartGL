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

import android.support.annotation.NonNull;

import fr.arnaudguyon.smartgl.math.Vector3D;

/**
 * Parallel Light for OpenGL
 */

public class LightParallel extends Light {

    private SmartColor mColor;
    private Vector3D mDirection;

    public LightParallel(@NonNull SmartColor color, @NonNull Vector3D direction) {
        mColor = color;
        mDirection = direction;
        mDirection.normalize();
    }

    public @NonNull SmartColor getColor() {
        return mColor;
    }

    public @NonNull Vector3D getDirection() {
        return mDirection;
    }

}
