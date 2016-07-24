package fr.arnaudguyon.smartgl.opengl;

import java.util.Vector;

public class RenderObjectContainer extends RenderObject {
	
	public RenderObjectContainer( boolean is3d) {
		super(is3d);
	}

	@Override
	final protected boolean isContainer() {
		return true;
	}

    @Override
    protected void computeMatrix(float[] matrix) {

    }

    protected Vector<RenderObject> getRenderObjects() {
		return null;
	}

}
