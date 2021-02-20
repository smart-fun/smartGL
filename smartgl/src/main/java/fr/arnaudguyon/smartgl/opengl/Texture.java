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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Texture {
	
	private final static int UNBIND_VALUE = 0;

	private int[] mId;
	private int mWidth;
	private int mHeight;
	private Bitmap mBitmap;
	private boolean mRecycleWhenBinded = true;

	public final int getId() {
		return mId[0];
	}

	public final int getWidth() {
		return mWidth;
	}

	public final int getHeight() {
		return mHeight;
	}
	
	public final boolean isBinded() {
		return (mId[0] != UNBIND_VALUE);
	}
	
	public final void recycleWhenBinded(boolean recycle) {
		mRecycleWhenBinded = recycle;
	}

	private Texture() {
		mId = new int[1];
		mId[0] = UNBIND_VALUE;
	}

	public Texture(int width, int height, @Nullable Bitmap bitmap) {
		this();
		mWidth = width;
		mHeight = height;
		mBitmap = bitmap;
	}
	public Texture(Bitmap bitmap) {
		this();
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		mBitmap = bitmap;
	}
	public Texture(Context context, @DrawableRes int resourceId) {
		this();
		Resources resources = context.getResources();
		mBitmap = BitmapFactory.decodeResource(resources, resourceId);
		mWidth = mBitmap.getWidth();
		mHeight = mBitmap.getHeight();
	}

	public boolean setBitmap(int width, int height, @NonNull Bitmap bitmap) {
		if (isBinded()) {
			return false;
		} else {
			this.mWidth = width;
			this.mHeight = height;
			this.mBitmap = bitmap;
			return true;
		}
	}

	boolean bindTexture() {
		if ((mBitmap != null) && (!mBitmap.isRecycled())) {
			GLES20.glGenTextures(1, mId, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mId[0]);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0); // GLES20.GL_RGBA
			if (mRecycleWhenBinded) {
				mBitmap.recycle();
				mBitmap = null;
			}
			return true;
		}
		return false;
	}

    public void release() {
        unbindTexture();
        if ((mBitmap != null) && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

	void unbindTexture() {
		if (isBinded()) {
			GLES20.glDeleteTextures(1, mId, 0);
			mId[0] = UNBIND_VALUE;
		}
	}
	
	public static Bitmap loadAndTurnAndResize(Context context, String pictureFileName, int approxWidth) {
		try {
			final ExifInterface exif = new ExifInterface(pictureFileName);
			final int srcWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, approxWidth);
			final int subSample = (srcWidth / approxWidth);
			BitmapFactory.Options resizeOptions = new BitmapFactory.Options();
			resizeOptions.inSampleSize = subSample;
			Bitmap bitmap = BitmapFactory.decodeFile(pictureFileName, resizeOptions);
			if (bitmap != null) {
				final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				if (orientation != ExifInterface.ORIENTATION_NORMAL) {
					int angle = 0;
					if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
						angle = 90;
					} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
						angle = 180;
					} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
						angle = 270;
					}
					if (angle != 0f) {
						Matrix matrix = new Matrix();
						matrix.preRotate(angle);
						Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
						bitmap.recycle();
						return rotatedBitmap;
					}
				}
			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
