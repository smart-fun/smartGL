package fr.arnaudguyon.smartglapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.arnaudguyon.smartgl.tools.ObjectReader;
import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;

public class MainActivity extends AppCompatActivity {

    private MainGLView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmartGLRenderer renderer = new SmartGLRenderer(this);
        renderer.setClearColor(0,0,0, 1);

        mGLView = (MainGLView) findViewById(R.id.activityGLView);
        mGLView.setRenderer(renderer);

    }

    @Override
    protected void onPause() {
        if (mGLView != null) {
            mGLView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGLView != null) {
            mGLView.onResume();
        }
    }
}
