package com.example.frozen

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig


class NewRenderer : GLSurfaceView.Renderer {
    override fun onDrawFrame(arg0: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 1f, 0f, 1f)
    }

    override fun onSurfaceChanged(arg0: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}