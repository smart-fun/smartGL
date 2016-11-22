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

import java.util.ArrayList;

import android.os.SystemClock;

public class AnimatedFace3D extends Face3D {

    public interface AnimatedFaceListener {
        void onFrameChanged(int frame);
    }

	class Frame {
		long mDuration;
		UVList	mUVs;
		Frame(float duration, UVList uvs) {
			mDuration = (long) (duration * 1000);
			mUVs = uvs;
		}
	}
	
	private int mFrameNumber;
	private ArrayList<Frame> mFrames = new ArrayList<>();
	private long mNextChangeDate;
	
	public AnimatedFace3D(AnimatedFaceListener owner) { // owner just to be conscious of implementing listener
		super();
	}
	
	public void addFrame(float duration, UVList uvs) {
		Frame frame = new Frame(duration, uvs);
		mFrames.add(frame);
		if (mFrames.size() == 1) {
			setUVList(uvs);
		}
	}
	
	public final int getFrameNumber() {
		return mFrameNumber;
	}
	
	public void setFrame(int frameNumber) {
		if ((frameNumber >= 0) && (frameNumber < mFrames.size())) {
			mFrameNumber = frameNumber;
			Frame frame = mFrames.get(mFrameNumber);
			setUVList(frame.mUVs);
			final long now = SystemClock.uptimeMillis();
			mNextChangeDate = now + frame.mDuration;
		}
	}
	
	@Override
	public void onPreRenderFace(OpenGLRenderer renderer, RenderObject object, Shader shader) {
		
		if ((mFrames.size() == 0) || (mFrames.get(mFrameNumber).mDuration == 0)) {	// duration==0 -> manual animation
			return;
		}
		
		final long now = SystemClock.uptimeMillis();
		if ((mNextChangeDate == 0) || (now >= mNextChangeDate)) {
			//if (mFrames.size() > 0) {
				if (mFrameNumber >= mFrames.size() - 1) {
					mFrameNumber = 0;
				} else {
					++mFrameNumber;
				}
				Frame frame = mFrames.get(mFrameNumber);
				setUVList(frame.mUVs);
				mNextChangeDate = now + frame.mDuration;

            if (object instanceof AnimatedFaceListener) {
                AnimatedFaceListener listener = (AnimatedFaceListener) object;
                listener.onFrameChanged(mFrameNumber);
            }
			//}
		}
	}
}
