package com.example.frozen

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

const val VERTEX_SHADER = "attribute vec4 a_Position;\n" +
        "\n" +
        "void main() {\n" +
        "    gl_Position = a_Position;\n" +
        "}"

const val FRAGMENT_SHADER = "precision mediump float;\n" +
        "uniform vec4 u_Color;\n" +
        "\n" +
        "void main() {\n" +
        "    gl_FragColor = u_Color;\n" +
        "}"


class NewRenderer : GLSurfaceView.Renderer {
    private var programId = 0
    private lateinit var vertexData: FloatBuffer
    private var uColorLocation = 0
    private var aPositionLocation = 0

    init {
        prepareData()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glClearColor(0f, 0f, 0f, 0f)
        val vertexShaderId: Int =
            createShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShaderId: Int =
            createShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        programId = createProgram(vertexShaderId, fragmentShaderId)
        GLES20.glUseProgram(programId)
        bindData()
    }

    override fun onSurfaceChanged(arg0: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun prepareData() {
        val vertices = floatArrayOf(-0.5f, -0.2f, 0.0f, 0.2f, 0.5f, -0.2f)
        vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(vertices)
            }
    }

    private fun bindData() {
        uColorLocation = GLES20.glGetUniformLocation(programId, "u_Color")
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")
        vertexData.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, vertexData)
        GLES20.glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onDrawFrame(arg0: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }
}