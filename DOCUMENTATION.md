# SmartGL #

**SmartGL** is an Android Studio library that simplifies the use of **OpenGL** to display **2D Sprites** and **3D Textured Objects**.

It has been used for several games and apps on the store.

## Usage ##

The OpenGL scene is displayed using a **SmartGLView** and a **SmartGLViewController**.

```xml
    <fr.arnaudguyon.smartgl.opengl.SmartGLView
        android:id="@+id/smartGLView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

The SmartGLViewController is just an interface, so it could be implemented by your Activity or Fragment, but I recommend to create a separate class for it.

```java
public class MainActivity extends AppCompatActivity {

    private SmartGLView mSmartGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSmartGLView = (SmartGLView) findViewById(R.id.smartGLView);
        mSmartGLView.setController(new GLViewController());
    }
}
```

### LifeCycle ###

It is really important to inform the SmartGLView when the Activity or Fragment is paused or resumed. **If you miss this step the scene will not be initialized or restored correctly**.

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onPause() {
        if (mSmartGLView != null) {
            mSmartGLView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSmartGLView != null) {
            mSmartGLView.onResume();
        }
    }
}
```

When the SmartGLView is ready, **onPrepareView**(SmartGLView smartGLView) is called on the SmartGLViewController.

This method is the best place to prepare the scene (load textures, and add objects to the scene).

When the SmartGLView is dismissed, **onReleaseView**(SmartGLView smartGLView) is called. This is the best place to release content from memory (like textures).

