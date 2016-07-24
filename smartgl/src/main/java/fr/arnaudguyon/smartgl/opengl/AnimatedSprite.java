package fr.arnaudguyon.smartgl.opengl;

public class AnimatedSprite extends Sprite implements AnimatedFace3D.AnimatedFaceListener {

	public AnimatedSprite(int width, int height) {
		super(width, height);
	}
	
	@Override
	protected void createFace(int width, int height) {
		// No UVs, as they are defined as Frames
		VertexList vertexList = createDefaultVertexList(width, height);
		AnimatedFace3D face = new AnimatedFace3D(this);
		face.setVertexList(vertexList);
		face.setTexture(null);
		addFace(face);
	}

	public void addDefaultFrame() {
		addFrame(0, 0.f, 0.f, 1.f, 1.f);
	}
	
	public void addFrame(float duration, float uStart, float vStart, float uStop, float vStop) {
		UVList uvList = new UVList();
		uvList.init(4);
		uvList.add(uStart, vStart);
		uvList.add(uStop, vStart);
		uvList.add(uStart, vStop);
		uvList.add(uStop, vStop);
		uvList.finalizeBuffer();
		AnimatedFace3D face = (AnimatedFace3D) getFace();
		face.addFrame(duration, uvList);
	}
	
	public final int getFrameNumber() {
		AnimatedFace3D aniFace = (AnimatedFace3D) getFace();
		return aniFace.getFrameNumber();
	}
	
	public void setFrame(int frameNumber) {
		AnimatedFace3D aniFace = (AnimatedFace3D) getFace();
		aniFace.setFrame(frameNumber);
	}

    @Override
    public void onFrameChanged(int frame) {
    }
}
