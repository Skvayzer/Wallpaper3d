package com.example.skvayzer.wallpaper3d;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import 	javax.microedition.khronos.egl.EGLContext;
import android.os.IBinder;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class WallpaperSrv extends WallpaperService {
    private boolean visible;
    private class MyEngine extends Engine {
        private GLRenderer glRenderer;
        private GL10 gl;
        private EGL10 egl;
        private EGLContext glc;
        private EGLDisplay glDisplay;
        private EGLSurface glSurface;
        private ExecutorService executor;
        private Runnable drawCommand;
        //private EGL10 egl10;
        @Override
        public void onCreate(final SurfaceHolder holder) {
            super.onCreate(holder);
            executor = Executors.newSingleThreadExecutor();
            drawCommand = new Runnable() {
                public void run() {
                    glRenderer.onDrawFrame(gl);
                    egl.eglSwapBuffers(glDisplay, glSurface);
                    if (isVisible() && egl.eglGetError() != EGL11.EGL_CONTEXT_LOST) {
                        executor.execute(drawCommand);
                    }
                }
            };
        }
        @Override
        public void onDestroy(){
            executor.shutdownNow();
            super.onDestroy();
        }
        @Override
        public void onVisibilityChanged(final boolean visible){
            super.onVisibilityChanged(visible);
            if (visible) {
                executor.execute(drawCommand);
            }
        }
        @Override
        public void onSurfaceCreated(final SurfaceHolder holder){
            super.onSurfaceCreated(holder);
            Runnable surfaceCreatedCommand = new Runnable() {
                @Override
                public void run() {
                    // Инициализация OpenGL
                    egl = (EGL10) EGLContext.getEGL();
                    glDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                    int[] version = new int[2];
                    egl.eglInitialize(glDisplay, version);
                    int[] configSpec = { EGL10.EGL_RED_SIZE, 5,
                            EGL10.EGL_GREEN_SIZE, 6, EGL10.EGL_BLUE_SIZE,
                            5, EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] numConfig = new int[1];
                    egl.eglChooseConfig(glDisplay, configSpec, configs,
                            1, numConfig);
                    EGLConfig config = configs[0];
                    glc = egl.eglCreateContext(glDisplay, config,
                            EGL10.EGL_NO_CONTEXT, null);
                    glSurface = egl.eglCreateWindowSurface(glDisplay,
                            config, holder, null);
                    egl.eglMakeCurrent(glDisplay, glSurface, glSurface,
                            glc);
                    gl = (GL10) (glc.getGL());
                    // Инициализация рендеринга
                    glRenderer = new GLRenderer(WallpaperSrv.this);
                    glRenderer.onSurfaceCreated(gl, config);
                }
            };
            executor.execute(surfaceCreatedCommand);
        }
        @Override
        public void onSurfaceDestroyed(final SurfaceHolder holder){
            Runnable surfaceDestroyedCommand = new Runnable() {
                public void run() {
                    // Освобождаем ресурсы OpenGL
                    egl.eglMakeCurrent(glDisplay, EGL10.EGL_NO_SURFACE,
                            EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                    egl.eglDestroySurface(glDisplay, glSurface);
                    egl.eglDestroyContext(glDisplay, glc);
                    egl.eglTerminate(glDisplay);
                };
            };
            executor.execute(surfaceDestroyedCommand);
            super.onSurfaceDestroyed(holder);
        }
        @Override
        public void onSurfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height){

            super.onSurfaceChanged(holder,format,width,height);
            Runnable surfaceChangedCommand = new Runnable() {
                public void run() {
                    glRenderer.onSurfaceChanged(gl, width, height);
                };
            };
            executor.execute(surfaceChangedCommand);
        }
        public void onOffsetsChanged(final float xOffset, final float yOffset, final float xOffsetStep,
                                     final float yOffsetStep, final int xPixelOffset, final int yPixelOffset){
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep,
                    yOffsetStep, xPixelOffset, yPixelOffset);
            Runnable offsetsChangedCommand = new Runnable() {
                public void run() {
                    if (xOffsetStep != 0f) {
                        glRenderer.setParallax(xOffset - 0.5f);
                    }
                };
            };
            executor.execute(offsetsChangedCommand);
        }
    }


    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }
    public WallpaperSrv() {
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
}
