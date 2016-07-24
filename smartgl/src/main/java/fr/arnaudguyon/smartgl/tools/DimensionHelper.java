package fr.arnaudguyon.smartgl.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Arnaud Guyon on 29/03/2015.
 */
public class DimensionHelper {

    private int mScreenWidthPx;
    private int mScreenHeightPx;

    private int mScreenWidthDp;
    private int mScreenHeightDp;

    private DimensionHelper() {
    }

    public DimensionHelper(Context context) {
        this();

        WindowManager windowService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowService.getDefaultDisplay();
        Point screenSizePx = new Point();
        display.getSize(screenSizePx);
        mScreenWidthPx = screenSizePx.x;
        mScreenHeightPx = screenSizePx.y;

        Configuration config = context.getResources().getConfiguration();
        mScreenWidthDp = config.screenWidthDp;
        mScreenHeightDp = config.screenHeightDp;
    }

    public int getScreenWidthPx()	{ return mScreenWidthPx; }
    public int getScreenHeightPx()	{ return mScreenHeightPx; }

    public int getScreenWidthDp()	{ return mScreenWidthDp; }
    public int getScreenHeightDp()	{ return mScreenHeightDp; }

    public int dpHeightToPix(int dp) {
        return dp * mScreenHeightPx / mScreenHeightDp;
    }
    public int dpWidthToPix(int dp) {
        return dp * mScreenWidthPx / mScreenWidthDp;
    }
}
