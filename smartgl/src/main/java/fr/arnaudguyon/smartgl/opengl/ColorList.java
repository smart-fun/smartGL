package fr.arnaudguyon.smartgl.opengl;

public class ColorList extends AttribList {

	public ColorList() {
		super(4); // 4 floats per color (r,g,b,a)
	}

	public ColorList(ColorList other) {
		super(other);
	}

}
