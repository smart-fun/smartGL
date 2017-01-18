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

import android.app.Activity;
import android.os.Bundle;

import fr.arnaudguyon.smartgl.opengl.SmartGLView;

public class MainActivity extends Activity {

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
