/*
    Copyright 2016 Arnaud Guyon

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

/**
 * @author Arnaud Guyon
 * 
 *         Defines a face for an Object3D. A Face3D is composed of a Texture, a
 *         VertexList and a UVList
 * 
 */

public class Face3D {

	private Texture mTexture;
	private VertexList mVertexList;
	private UVList mUVList;
	private ColorList mColorList;
	private boolean mVisible = true;

	public Face3D() {
		mVertexList = null;
		mUVList = null;
		mTexture = null;
	}

	final public void setVertexList(VertexList vertexList) {
		mVertexList = vertexList;
	}

	final public VertexList getVertexList() {
		return mVertexList;
	}

	final public void setUVList(UVList uvList) {
		mUVList = uvList;
	}

	final public UVList getUVList() {
		return mUVList;
	}

	final public void setColorList(ColorList colorList) {
		mColorList = colorList;
	}

	final public ColorList getColorList() {
		return mColorList;
	}

	final public void setTexture(Texture texture) {
		mTexture = texture;
	}

	final public Texture getTexture() {
		return mTexture;
	}

	final public void setVisible(boolean visible) {
		mVisible = visible;
	}

	final public boolean isVisible() {
		return mVisible;
	}

	public void releaseResources() {

		if (mTexture != null) {
			mTexture.unbindTexture();
			mTexture = null;
		}

		if (mVertexList != null) {
			mVertexList.destroyFloatBuffer();
			mVertexList = null;
		}

		if (mUVList != null) {
			mUVList.destroyFloatBuffer();
			mUVList = null;
		}

		if (mColorList != null) {
			mColorList.destroyFloatBuffer();
			mColorList = null;
		}
	}
	
	public boolean shouldDisplay(OpenGLRenderer renderer) {
		return mVisible;
	}
	
	public void onPreRenderFace(OpenGLRenderer renderer, RenderObject object, Shader shader) {
	}

}
