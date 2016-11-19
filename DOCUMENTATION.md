# SmartGL #

A SmartGL scene works with different components:

* a SmartGL**View** in your Layout
* a SmartGL**Renderer** linked to the SmartGLView
* Render**Object**s (Object3D or Sprite) which are added to the SmartGLRenderer

## Preparing the code and layout ##

### Custom SmartGLView ###

This is the View which will be displayed on screen.

acquireResources() and releaseResources() must be used to load and unload data used in the scene, like Textures. They are called when the View is presented or dismissed. acquireResource() is a good place to build your scene, which means to construct the objects and add them to the Renderer.

onPreRender() method is called every frame. This is where you can move the objects in space.

```java
public class CustomGLView extends SmartGLView {

    public CustomGLView(Context context) {
        super(context);
    }

    public CustomGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void acquireResources() {
        super.acquireResources();
        // load resources like common Textures
        // build your objects here and add them to the Renderer
    }

    @Override
    protected void releaseResources() {
        super.releaseResources();
        // release resources like Textures
    }

    @Override
    public void onPreRender(OpenGLRenderer renderer) {
        super.onPreRender(renderer);
        // is called before rendering the next frame
        // typically move your objects on screen here
}


```

## Activity ##

```xml

    <fr.arnaudguyon.smartglapp.MainGLView
        android:id="@+id/customGLView"
        android:layout_width="match_parent"
        android:layout_height="150dp" />
        
```

You need to set an OpenGLRenderer to your CustomGLView. There is a default SmartGLRenderer that you can use. You can also set a default background color for the View using setClearColor.

```java
public class MainActivity extends AppCompatActivity {

    private MainGLView mCustomGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCustomGLView = (MainGLView) findViewById(R.id. customGLView);
        SmartGLRenderer renderer = new SmartGLRenderer(this);   // use the default renderer
        renderer.setClearColor(0,0,0, 1);   // background color (R,G,B,A)
        mCustomGLView.setRenderer(renderer);
    }

    @Override
    protected void onPause() {
        if (mCustomGLView != null) {
            mCustomGLView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCustomGLView != null) {
            mCustomGLView.onResume();
        }
    }
}

```
