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
package fr.arnaudguyon.smartglapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.arnaudguyon.smartgl.opengl.LightAmbiant;
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
        final GLViewController controller = new GLViewController();
        mFragmentGLView.setController(controller);

        mFragmentGLView.getSmartGLRenderer().setClearColor(0.01f, 0.01f, 0.1f, 1);

        LightAmbiant lightAmbiant = new LightAmbiant(0.2f, 0.2f, 0.2f);
        mFragmentGLView.getSmartGLRenderer().setLightAmbiant(lightAmbiant);

        View cruiserButton = view.findViewById(R.id.cruiserButton);
        cruiserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.switchToCruiser();
            }
        });
        View fregateButton = view.findViewById(R.id.fregateButton);
        fregateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.switchToFrigate();
            }
        });
        View busButton = view.findViewById(R.id.busButton);
        busButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.switchToBus();
            }
        });
        View cubeButton = view.findViewById(R.id.cubeButton);
        cubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.switchToCube();
            }
        });


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
