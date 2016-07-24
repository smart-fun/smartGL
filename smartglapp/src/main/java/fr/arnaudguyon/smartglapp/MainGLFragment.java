package fr.arnaudguyon.smartglapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.arnaudguyon.smartgl.opengl.SmartGLFragment;
import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;

/**
 * Created by aguyon on 24/07/16.
 */
public class MainGLFragment extends SmartGLFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();
        SmartGLRenderer renderer = new SmartGLRenderer(context);
        renderer.setClearColor(0,0,0, 1);
        return new MainGLView(context, renderer, this);
    }

    @Override
    protected void onReleaseResources() {

    }

    @Override
    protected void onAcquireResources() {

    }
}
