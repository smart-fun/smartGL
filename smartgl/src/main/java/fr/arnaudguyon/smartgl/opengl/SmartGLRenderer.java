package fr.arnaudguyon.smartgl.opengl;

import android.content.Context;

public class SmartGLRenderer extends OpenGLRenderer {

    public SmartGLRenderer(Context context) {
        super(context);
        setCamera(new SmartGLCamera());
    }

}
