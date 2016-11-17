package fr.arnaudguyon.smartglapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;

/**
 * Created by aguyon on 24/07/16.
 */
public class MainGLFragment extends Fragment {

    private MainGLView mGLView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.glfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context context = view.getContext();
        SmartGLRenderer renderer = new SmartGLRenderer(context);
        renderer.setClearColor(0,0,0, 1);

        mGLView = (MainGLView) view.findViewById(R.id.fragmentGLView);
        mGLView.setRenderer(renderer);
    }

    @Override
    public void onPause() {
        if (mGLView != null) {
            mGLView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGLView != null) {
            mGLView.onResume();
        }
    }

}
