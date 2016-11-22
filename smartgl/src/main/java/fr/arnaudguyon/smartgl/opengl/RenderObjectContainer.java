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
