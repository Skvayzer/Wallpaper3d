package com.example.skvayzer.wallpaper3d;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLView extends GLSurfaceView {
    private final GLRenderer renderer;

    public GLView(Context context){
        super(context);
        renderer=new GLRenderer(context);
        setRenderer(renderer);

    }
}
