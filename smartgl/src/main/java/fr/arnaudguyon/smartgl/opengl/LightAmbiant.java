package fr.arnaudguyon.smartgl.opengl;

import android.support.annotation.FloatRange;

/**
 * Ambiant Light for OpenGL
 */

public class LightAmbiant extends Light {

    private float[] mLight = new float[4];

    public LightAmbiant(@FloatRange(from=0, to=1) float red, @FloatRange(from=0, to=1) float green, @FloatRange(from=0, to=1) float blue) {
        mLight[0] = red;
        mLight[1] = green;
        mLight[2] = blue;
        mLight[3] = 1;
    }

    public float[] getArray() {
        return mLight;
    }
}
