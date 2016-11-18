package fr.arnaudguyon.smartglapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;

public class MainActivity extends AppCompatActivity {

    private MainGLView mActivityGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mActivityGLView = (MainGLView) findViewById(R.id.activityGLView);
        SmartGLRenderer renderer = new SmartGLRenderer(this);   // use the default renderer
        renderer.setClearColor(0,0,0, 1);   // background color (R,G,B,A)
        mActivityGLView.setRenderer(renderer);

    }

    @Override
    protected void onPause() {
        if (mActivityGLView != null) {
            mActivityGLView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mActivityGLView != null) {
            mActivityGLView.onResume();
        }
    }
}
