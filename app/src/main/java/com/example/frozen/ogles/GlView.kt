package com.example.frozen.ogles

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.AttributeSet
import com.example.frozen.renderer.GLES20FramebufferObject
import com.example.frozen.renderer.GlFrameBufferObjectRenderer
import com.example.frozen.shaders.GlPreviewShader
import com.example.frozen.shaders.GlShader
import javax.microedition.khronos.egl.EGLConfig


class GlView : GLSurfaceView {
    private var mRenderer: Renderer? = null

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        setEGLConfigChooser(GLES20ConfigChooser(false))
        setEGLContextFactory(GLES20ContextFactory())
        mRenderer = Renderer()
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private var mShader: GlShader? = null

    fun setShader(shader: GlShader?) {
        mShader = shader
        queueEvent { mRenderer?.setShader(shader) }
    }

    inner class Renderer : GlFrameBufferObjectRenderer() {
        private var mUpdateSurface = false
        private var mImageTexture: Texture? = null
        private var mUploadTexture = false
        private var mTexName = 0
        private val mMVPMatrix = FloatArray(16)
        private val mProjMatrix = FloatArray(16)
        private val mMMatrix = FloatArray(16)
        private val mVMatrix = FloatArray(16)
        private val mSTMatrix = FloatArray(16)
        private var mCameraRatio = 1.0f
        private var mFramebufferObject: GLES20FramebufferObject? = null

        private var mShader: GlShader? = null
        private var mIsNewShader = false
        private var mMaxTextureSize = 0

        fun setShader(shader: GlShader?) {
            if (mShader != null) {
                mShader!!.release()
            }
            if (shader != null) {
                mIsNewShader = true
            }
            mShader = shader
            mIsNewShader = true
            requestRender()
        }

        init {
            Matrix.setIdentityM(mSTMatrix, 0)
        }

        override fun onSurfaceCreated(config: EGLConfig) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            val args = IntArray(1)
            GLES20.glGenTextures(args.size, args, 0)
            mTexName = args[0]

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            mFramebufferObject = GLES20FramebufferObject()
            Matrix.setLookAtM(
                mVMatrix, 0,
                0.0f, 0.0f, 5.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f
            )
            synchronized(this) { mUpdateSurface = false }
            if (mImageTexture != null) {
                mUploadTexture = true
            }
            if (mShader != null) {
                mIsNewShader = true
            }
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, args, 0)
            mMaxTextureSize = args[0]
        }

        override fun onSurfaceChanged(width: Int, height: Int) {
            mFramebufferObject!!.setup(width, height)
            if (mShader != null) {
                mShader!!.setFrameSize(width, height)
            }
            val aspectRatio = width.toFloat() / height
            Matrix.frustumM(mProjMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 5f, 7f)
        }

        override fun onDrawFrame(fbo: GLES20FramebufferObject) {
            if (mUploadTexture) {
                mImageTexture!!.setup()
                mCameraRatio = mImageTexture!!.width.toFloat() / mImageTexture!!.height
                Matrix.setIdentityM(mSTMatrix, 0)
                mUploadTexture = false
            }
            if (mIsNewShader) {
                if (mShader != null) {
                    mShader!!.setup()
                    mShader!!.setFrameSize(fbo.width, fbo.height)
                }
                mIsNewShader = false
            }
            if (mShader != null) {
                mFramebufferObject!!.enable()
                GLES20.glViewport(0, 0, mFramebufferObject!!.width, mFramebufferObject!!.height)
            }
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0) // 視野行列とモデルビュー行列を乗算します。
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0) // 投影行列と乗算します。

            if (mShader != null) {
                fbo.enable()
                GLES20.glViewport(0, 0, fbo.width, fbo.height)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                mShader!!.draw(mFramebufferObject!!.texName, fbo)
            }
        }
    }
}