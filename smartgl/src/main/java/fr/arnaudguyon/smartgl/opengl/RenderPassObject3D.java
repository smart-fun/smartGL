package fr.arnaudguyon.smartgl.opengl;

/**
 * Created by arnaud on 20/11/2016.
 */

public class RenderPassObject3D extends RenderPass {

    public RenderPassObject3D() {
        super(true, true);
        ShaderTexture shader = new ShaderTexture();
        addShader(shader);
    }

    public void addObject(Object3D object3D) {
        getRenderObjects().add(object3D);
    }
}
