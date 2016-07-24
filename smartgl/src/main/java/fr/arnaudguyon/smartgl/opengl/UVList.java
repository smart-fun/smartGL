package fr.arnaudguyon.smartgl.opengl;

public class UVList extends AttribList {

	public UVList() {
		super(2); // 2 floats per uv (u,v)
	}

	public UVList(UVList other) {
		super(other);
	}

}
