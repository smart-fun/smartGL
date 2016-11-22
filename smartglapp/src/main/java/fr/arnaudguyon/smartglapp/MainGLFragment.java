package fr.arnaudguyon.smartglapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.arnaudguyon.smartgl.opengl.OpenGLCamera;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;

/**
 * Created by aguyon on 24/07/16.
 */
public class MainGLFragment extends Fragment {

    private SmartGLView mFragmentGLView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.glfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context context = view.getContext();
        mFragmentGLView = (SmartGLView) view.findViewById(R.id.fragmentGLView);
        mFragmentGLView.setDefaultRenderer(context);
        mFragmentGLView.setController(new GLViewController());

        OpenGLCamera camera = mFragmentGLView.getSmartGLRenderer().getCamera();
        camera.setFOV(90);
        camera.setPosition(0, 0, -4);
        camera.setRotation(0, 0, 20f);

    }

    @Override
    public void onPause() {
        if (mFragmentGLView != null) {
            mFragmentGLView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFragmentGLView != null) {
            mFragmentGLView.onResume();
        }
    }

}
