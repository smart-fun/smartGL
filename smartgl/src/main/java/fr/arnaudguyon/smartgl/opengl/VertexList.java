package fr.arnaudguyon.smartgl.opengl;

public class VertexList extends AttribList {

	public VertexList() {
		super(3); // 3 floats per vertex (x,y,z)
	}

	public VertexList(VertexList other) {
		super(other);
	}
	
	public VertexList(float[] other, int otherElementsPerVector) {
		super(other, otherElementsPerVector, 3);
	}

}
