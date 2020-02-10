package com.example.skvayzer.wallpaper3d;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OpenGL extends AppCompatActivity {
    GLSurfaceView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view=new GLView(this);
        setContentView(view);
    }
    @Override
    protected void onPause(){
        super.onPause();
        view.onPause();
    }
    protected void onResume(){
        super.onResume();
        view.onResume();
    }
}
