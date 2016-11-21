package fr.arnaudguyon.smartglapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.arnaudguyon.smartgl.opengl.SmartGLView;

public class MainActivity extends AppCompatActivity {

    private SmartGLView mActivityGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mActivityGLView = (SmartGLView) findViewById(R.id.activityGLView);
        mActivityGLView.setDefaultRenderer(this);
        mActivityGLView.setController(new GLViewController());
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
