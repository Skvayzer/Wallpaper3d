package com.example.skvayzer.wallpaper3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;

public class GLRenderer implements GLSurfaceView.Renderer {
    private Context     context;
    private final GLCube cube = new GLCube();
    private long startTime;
    private long fpsStartTime;
    private long numFrames;

//    Loader loader=new Loader();
//    RawModel model=OBJLoader.loadObjModel("theearth", loader);
   // ObjLoader loader;

   // FloatBuffer vertsBuff;
   // ShortBuffer indicesBuff;
    public GLRenderer(Context context)
    {
        this.context = context;
//        loader = new ObjLoader(context);
//        loader.load(R.raw.theearth);
//        verts = loader.vtx;
//        vertsBuff = ByteBuffer.allocateDirect(verts.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        vertsBuff.put(verts).position(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Очищаем экран крч

        // Располагаем фигню таким образом, чтобы мы могли ее видеть
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
       // gl.glTranslatef(0, 0, -3.0f);
        gl.glTranslatef(xOffset, 0, -3.0f);
        //вращаем крч
        long elapsed = System.currentTimeMillis() - startTime;
        gl.glRotatef(elapsed * (30f / 1000f), 0, 1, 0);
        gl.glRotatef(elapsed * (15f / 1000f), 1, 0, 0);
        gl.glRotatef(elapsed * (15f / 1000f), 0, 0, 1);
        //рисуем
        cube.draw(gl);
        numFrames++;
        long fpsElapsed = System.currentTimeMillis() - fpsStartTime;
        if (fpsElapsed > 5 * 1000) { // every 5 seconds
            float fps = (numFrames * 1000.0F) / fpsElapsed;
            Log.d(TAG, "Frames per second: " + fps + " (" + numFrames
                    + " frames in " + fpsElapsed + " ms)" );
            fpsStartTime = System.currentTimeMillis();
            numFrames = 0;
        }

    }
    private float xOffset;
    public void setParallax(float xOffset) {
        this.xOffset = -xOffset;
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Определяет пирамиду видимости для окна просмотра
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        float ratio = (float) width / height;
        GLU.gluPerspective(gl, 45.0f, ratio, 1, 100f);
        float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1 };
        float lightDiffuse[] = new float[] { 1, 1, 1, 1 };
        float[] lightPos = new float[] { 1, 1, 1, 1 };
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
        float matAmbient[] = new float[] { 1, 1, 1, 1 };
        float matDiffuse[] = new float[] { 1, 1, 1, 1 };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
                matAmbient, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
                matDiffuse, 0);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        startTime = System.currentTimeMillis();
        fpsStartTime = startTime;
        numFrames = 0;
        // Включаем использование текстур
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
// Загружаем текстуру куба из растрового изображения
        GLCube.loadTexture(gl, context, R.drawable.diamka);
        boolean SEE_THRU = true;
// ...
        if (SEE_THRU) {
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
        }

        //gl.glDisable(GL10.GL_DITHER);
        //gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
    }
}
