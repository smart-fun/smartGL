package fr.arnaudguyon.smartgl.opengl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.opengl.GLES20;
import android.opengl.GLUtils;

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

	public Texture(int width, int height, Bitmap bitmap) {
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
	public Texture(Context context, int resourceId) {
		this();
		Resources resources = context.getResources();
		mBitmap = BitmapFactory.decodeResource(resources, resourceId);
		mWidth = mBitmap.getWidth();
		mHeight = mBitmap.getHeight();
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
	
	public static Bitmap loadAndTurnAndResize(Context context, String pictureName, int approxWidth) {
		try {
			final ExifInterface exif = new ExifInterface(pictureName);
			final int srcWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, approxWidth);
			final int subSample = (srcWidth / approxWidth);
			BitmapFactory.Options resizeOptions = new BitmapFactory.Options();
			resizeOptions.inSampleSize = subSample;
			Bitmap bitmap = BitmapFactory.decodeFile(pictureName, resizeOptions);
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
