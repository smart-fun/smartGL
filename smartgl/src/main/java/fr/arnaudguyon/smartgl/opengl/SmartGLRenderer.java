package fr.arnaudguyon.smartgl.opengl;

import android.content.Context;

public class SmartGLRenderer extends OpenGLRenderer {

    public SmartGLRenderer(Context context) {
        super(context);
        setCamera(new SmartGLCamera());
    }

    // TODO: createRenderPasses that can be overriden but with default render passes (back, for, hud)
    // maybe insertRenderPass would be possible??

}
